<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="addTeamForm.teamName"
          name="队伍名称"
          label="队伍名称"
          placeholder="队伍名称"
          :rules="[{ required: true, message: '请填写队伍名称' }]"
      />
      <van-field name="stepper" label="队伍人数" maxlength="15">
        <template #input>
          <van-stepper v-model="addTeamForm.maxNumber" />
        </template>
      </van-field>
      <van-field
          v-model="addTeamForm.teamPassword"
          type="password"
          name="队伍密码"
          label="队伍密码"
          placeholder="队伍密码"
          :rules="[{ required: true, message: '请填写队伍密码' }]"
      />
      <van-field
          v-model="addTeamForm.description"
          rows="4"
          autosize
          label="队伍描述"
          type="textarea"
          placeholder="请输入队伍描述"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        创建队伍
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">
import { ref } from "vue";
import myAxios from "../plugins/myAxios";
import {showFailToast, showSuccessToast, showToast} from "vant";
import { useRouter } from "vue-router";

const router = useRouter();

const initFormData = {
  "description": "",
  "maxNumber": 1,
  "teamName": "",
  "teamPassword": ""
}

const addTeamForm = ref({
  ...initFormData
})

console.log(addTeamForm.value);

const onSubmit = async () => {
  const res = await myAxios.post("/team/add", addTeamForm.value);
  if(res?.code === 0) {
    showSuccessToast('创建成功！');
    await router.push({
      path: '/team',
      replace: true,
    });
  } else {
    showFailToast(res?.description || "创建失败");
  }
}
</script>

<style scoped>

</style>