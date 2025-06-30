<template>
  <div id="teamPage">
    <van-search
        v-model="searchValue"
        show-action
        placeholder="请输入搜索队伍关键词"
    />


    <van-divider />

    <van-row align="middle" justify="space-between" gutter="16">
      <van-col span="10">
        <van-button type="primary" @click="doCreateTeam" block>
          创建队伍
        </van-button>
      </van-col>
      <van-col span="10">
        <van-button type="primary" @click="doMyTeam" block>
          我的队伍
        </van-button>
      </van-col>
    </van-row>

    <van-divider />

    <team-card-list show-join-button :team-list="teamList" />
    <van-empty v-if="!teamList || teamList.length < 1" description="未搜索到队伍！"/>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import TeamCardList from '../components/TeamCardList.vue';
import myAxios from '../plugins/myAxios.js';
import { showFailToast } from 'vant';
import moment from 'moment/moment.js';

const router = useRouter();
const teamList = ref([]);
const searchValue = ref('');

// 跳转到创建队伍 / 我的队伍
const doCreateTeam = () => router.push({ path: '/team/add' });
const doMyTeam     = () => router.push({ path: '/team/myself' });

// 通用的取列表方法：根据 keyword 动态切换接口
const fetchTeamList = async (keyword = '') => {
  const isSearch = keyword !== '';
  const url      = isSearch ? '/team/search' : '/team/list';
  const params   = isSearch ? { searchTeam: keyword } : {};

  try {
    const res = await myAxios.get(url, { params });
    // 如果后端用 40001 表示「搜索无结果」
    if (isSearch && res?.code === 40001) {
      // 直接清空
      teamList.value = [];
      return;       // 一定要 return，避免后续逻辑把旧数据又写进去
    }

    if (res?.code === 0) {

      teamList.value = res.data.map(item => ({
        ...item,
        createTime: moment(item.createTime).format('YYYY-MM-DD HH:mm:ss'),
      }));
    } else {
      console.log('查询队伍失败');
      showFailToast('查询队伍失败');
    }
  } catch (err) {
    showFailToast(err.message);
  }
};

// 页面加载时，先取一次完整列表
onMounted(() => {
  fetchTeamList();
});

// 当 searchValue 更新时，自动调用不同接口
watch(searchValue, (val) => {
  fetchTeamList(val.trim());
});
</script>

<style scoped>
#teamPage {
  padding: 16px;
}
</style>
