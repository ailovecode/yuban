<template>
  <div id="userTeamPage">
    <!-- 顶部分栏 -->
    <van-tabs v-model:active="active" sticky @change="handleTabChange">
      <van-tab title="我加入的队伍" />
      <van-tab title="我管理的队伍" />
    </van-tabs>

    <!-- 内容区：显示队伍列表 -->
    <team-card-list
        :team-list="teamList"
        :show-quit-button="showQuitButton === 0"
        :show-edit-button="showEditButton === 1"
        :show-dissolve-button="showDissolveButton === 1"
        @edit="handleEditTeam"
        @quit="handleQuitTeam"
        @dissolve="handleDissolveTeam"
    />

    <van-empty v-if="!teamList || teamList.length < 1" description="没有队伍信息哦!"/>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {showFailToast, showSuccessToast} from 'vant'
import myAxios from '../plugins/myAxios'
import TeamCardList from '../components/TeamCardList.vue'
import {useRouter} from "vue-router";
import {useTeamStore} from "../stores/team.ts";
import moment from "moment";

const teamList = ref([])
const showEditButton = ref(0)
const showQuitButton = ref(0)
const showDissolveButton = ref(0)
const router = useRouter()

// 获取队伍数据
const fetchTeams = async (type) => {
  try {
    const endpoint = type === 'joined' ? '/team/get/myTeam' : '/team/get/manageTeam'
    const res = await myAxios.get(endpoint)

    if (res?.code === 0) {
      teamList.value = res.data.map(item => ({
        ...item,
        createTime: moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')
      }))

    } else {
      teamList.value = [];
      showSuccessToast(res?.description || '获取队伍列表失败')
    }
  } catch (error) {
    showFailToast('网络错误，请重试')
  }
}

// Tab 切换处理
const handleTabChange = (index) => {
  // 先清空列表
  // showFailToast(showEditButton === 0 ? '我加入的队伍' : '我管理的队伍')
  showEditButton.value = index;
  showQuitButton.value = index;
  showDissolveButton.value = index;

  // 根据 index 决定调用哪个接口
  const type = index === 0 ? 'joined' : 'managed';

  fetchTeams(type);
}

// 处理编辑逻辑
const handleEditTeam = (team) => {
  // 这里可以跳转到编辑页面或打开编辑对话框
  console.log('编辑队伍', team)
  const store = useTeamStore()
  store.setEditingTeam(team)
  // 例如跳转到编辑页面：
  router.push({
    name: 'EditTeam', // 使用name而不是path
  });
};

// 处理退出队伍逻辑
const handleQuitTeam = async (teamId) => {
  const res = await myAxios.post('/team/quit', {
    teamId: teamId
  });
  if (res?.code === 0) {
    showSuccessToast('退出队伍成功')
    fetchTeams('joined')
  } else {
    showFailToast('退出队伍失败')
  }
}

// 处理解散队伍逻辑
const handleDissolveTeam = async (teamId) => {
  const res = await myAxios.post('/team/delete', {
    teamId: teamId
  });
  if (res?.code === 0) {
    showSuccessToast('解散队伍成功')
    fetchTeams('managed')
  } else {
    showFailToast('解散队伍失败')
  }
}

// 初始化加载我加入的队伍
onMounted(() => {
  fetchTeams('joined')
})
</script>

<style scoped>
#userTeamPage {
  padding-bottom: 50px;
}

.van-tabs {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #fff;
}
</style>