<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="form.teamName"
          name="teamName"
          label="队伍名称"
          placeholder="请输入队伍名称"
          :rules="[{ required: true, message: '请填写队伍名称' }]"
      />
      <van-field name="stepper" label="队伍人数">
        <template #input>
          <van-stepper v-model="form.maxNumber" :min="1" />
        </template>
      </van-field>
      <van-field
          v-model="form.teamPassword"
          type="password"
          name="teamPassword"
          label="队伍密码"
          placeholder="请输入队伍密码（若不更新请勿填写）"
      />
      <van-field
          v-model="form.description"
          type="textarea"
          rows="4"
          autosize
          label="队伍描述"
          placeholder="请输入队伍描述"
      />
    </van-cell-group>

    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交修改
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import myAxios from '../plugins/myAxios';
import { showSuccessToast, showFailToast } from 'vant';
import {useTeamStore} from "../stores/team.ts";

const router = useRouter();
const store = useTeamStore();

// 编辑表单初始结构，包含 id 用于后台定位
const form = ref({
  teamId: 0,
  teamName: '',
  maxNumber: 1,
  teamPassword: '',
  captainId: 0,
  description: '',
});

onMounted(() => {
  // 从路由参数解析传过来的 teamData 并填充表单
  if (store.editingTeam) {
    try {
      const data = {...store.editingTeam};
      // console.log('获取过来的：'+data.valueOf());
      form.value = {
        teamId: data.teamId,
        teamName: data.teamName || '',
        maxNumber: data.maxNumber || 1,
        captainId: data.captainId || 0,
        teamPassword: data.teamPassword || '',
        description: data.description || '',
      };
      console.log('获取过来的：'+form.value);
    } catch (err) {
      console.error('解析 teamData 失败：', err);
    }
  }
});

const onSubmit = async () => {
  try {
    console.log(form.value)
    const res = await myAxios.post('/team/update', form.value);
    if (res?.code === 0) {
      showSuccessToast('修改成功！');
      await router.push({ path: '/team/myself', replace: true });
    } else {
      showFailToast(res?.description || '修改失败');
    }
  } catch (err) {
    console.error(err);
    showFailToast('网络或服务器错误');
  }
};
</script>


<style scoped>

</style>