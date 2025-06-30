<template>
  <div class="user-info-container">
    <template v-if="userList || userList.length > 1">
      <van-card v-for="user in userList"
                :desc="user.profile"
                :title="user.username"
                :thumb="user.avatarUrl"
      >
        <template #tags>
          <van-tag v-for="tag in user.tags" style="margin-right: 8px"  plain type="primary">{{ tag }}</van-tag>
        </template>
        <template #footer>
          <van-button size="small">联系他</van-button>
        </template>
      </van-card>
    </template>
  </div>
  <van-empty v-if="!userList || userList.length < 1" description="无用户推荐"/>
</template>

<script setup lang="ts">

import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios"
import {showSuccessToast, showFailToast} from "vant";
import qs from "qs";
import type {UserType} from "../models/user";
import "../app.css"
import {useRouter} from "vue-router";

const userList = ref ([]);
const router = useRouter();

onMounted(async () => {

  // 上述请求也可以按以下方式完成（可选）
  const userListData: UserType[] = await myAxios.get('/user/commend', {
    params: {
      pageSize: 10,
      pageNum : 1,
    },
    paramsSerializer: params => qs.stringify(params, {arrayFormat: "repeat"}),
  })
      .then(response => {
        console.log('/user/commend succeed', response);
        showSuccessToast('请求成功！');
        return response.data?.records;
      })
      .catch(error => {
        console.log('/user/commend error', error);
        showFailToast('请求失败！');
      })

  if (!userListData || userListData.length < 1) {
    showFailToast('请先登录');
    // 等动画结束后跳转
    setTimeout(() => {
      router.replace('/user/login');
    }, 800);
    return;
  }

  console.log('result: ',userListData);
  if(userListData){
    userListData.forEach((user: UserType) => {
      user.tags = JSON.parse(user.tags);
    })
    userList.value = userListData;
    console.log('yemin:', userList.value);
  }



})



</script>

<style scoped>

</style>