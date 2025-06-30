<template>
  <div id="teamCardList">
    <van-card
        v-for="team in props.teamList"
        :key="team.teamId"
        :desc="team.description"
        :title="team.teamName"
        thumb="https://pic.code-nav.cn/post_picture/1738721423335120897/T0TWgFi0HcJ2Gkaf.webp"
    >
      <template #tags>
        <van-tag
            plain
            type="danger"
            style="margin-right: 8px; margin-top: 8px"
        >
          {{ team.remainNumber === 0 ? '已满员' : `剩余${team.remainNumber}个名额` }}
        </van-tag>
      </template>

      <template #bottom>
        <div>房主: {{ team.users[0].username }}</div>
        <div>创建时间: {{ team.createTime }}</div>
      </template>

      <template #footer>
        <!-- 编辑按钮 -->
        <van-button
            v-if="props.showEditButton"
            type="default"
            size="small"
            @click="handleEdit(team)"
            style="margin-right: 8px"
        >
          编辑
        </van-button>

        <!-- 解散队伍 -->
        <van-button
            v-if="props.showDissolveButton"
            type="danger"
            size="small"
            @click="handleDissolve(team.teamId)"
            style="margin-right: 8px"
        >
          解散队伍
        </van-button>

        <!-- 退出队伍 -->
        <van-button
            v-if="props.showQuitButton"
            type="danger"
            size="small"
            @click="handleQuit(team.teamId)"
            style="margin-right: 8px"
        >
          退出队伍
        </van-button>

        <!-- 加入队伍 -->
        <van-button
            v-if="props.showJoinButton"
            type="primary"
            size="small"
            @click="showPasswordDialog(team.teamId)"
        >
          加入队伍
        </van-button>
      </template>
    </van-card>

    <!-- 密码输入对话框 -->
    <van-dialog
        v-model:show="showDialog"
        title="请输入密码"
        show-cancel-button
        @confirm="confirmJoin"
    >
      <van-field
          v-model="password"
          type="password"
          placeholder="请输入队伍密码"
          style="margin: 16px"
      />
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { TeamType } from '../models/team';
import myAxios from '../plugins/myAxios';
import { showFailToast, showSuccessToast } from 'vant';

interface TeamCardListProps {
  teamList: TeamType[];
  showEditButton?: boolean;
  showDissolveButton?: boolean;
  showQuitButton?: boolean;
  showJoinButton?: boolean;
}

const props = withDefaults(
    defineProps<TeamCardListProps>(),
    {
      teamList: [] as TeamType[],
      showEditButton: false,
      showDissolveButton: false,
      showQuitButton: false,
      showJoinButton: false,
    }
);

// **一次性**定义所有事件
const emit = defineEmits<{
  (e: 'edit', team: TeamType): void;
  (e: 'dissolve', teamId: number): void;
  (e: 'quit', teamId: number): void;
}>();

const showDialog = ref(false);
const password = ref('');
const currentTeamId = ref<number | null>(null);

const showPasswordDialog = (teamId: number) => {
  currentTeamId.value = teamId;
  password.value = '';
  showDialog.value = true;
};

const confirmJoin = async () => {
  if (currentTeamId.value === null) return;

  const res = await myAxios.post('/team/join', {
    teamId: currentTeamId.value,
    password: password.value,
  });

  if (res?.code === 0) {
    showSuccessToast('加入成功');
  } else {
    showFailToast('加入失败' + (res.description ? `，${res.description}` : ''));
  }
};

// 编辑按钮
const handleEdit = (team: TeamType) => {
  emit('edit', team);
};

// 解散队伍
const handleDissolve = (teamId: number) => {
  emit('dissolve', teamId);
};

// 退出队伍
const handleQuit = (teamId: number) => {
  emit('quit', teamId);
};
</script>

<style scoped>
#teamCardList :deep(.van-card__thumb) {
  height: 128px;
  object-fit: unset;
}
</style>
