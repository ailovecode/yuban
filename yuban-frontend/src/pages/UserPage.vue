<template>
  <template v-if="user">
    <div class="user-info-container">
      <van-cell title="头像" is-link to="/user/edit" arrow-direction="right">
        <img style="height: 48px" :src="user.avatarUrl" alt=""/>
      </van-cell>
      <van-cell title="昵称" is-link to="/user/edit" arrow-direction="right" :value="user.username"
              @click="doEdit('username', '昵称', user.username)" />
      <van-cell title="账户" arrow-direction="right" :value="user.userAccount" />
      <van-cell title="个人介绍" is-link to="/user/edit" arrow-direction="right" :value="user.profile"
                @click="doEdit('profile', '个人介绍', user.profile)" />
      <van-cell title="标签" is-link to="/user/edit" arrow-direction="right"
          @click="doEdit('tags', '标签', user.tags)" >
        <template #value>
          <div>
            <van-tag v-for="(tag, index) in user.tags"  :key="index" style="margin-right: 8px" plain type="primary">
              {{ tag }}
            </van-tag>
          </div>
        </template>
      </van-cell>
      <van-cell title="性别" is-link to="/user/edit" arrow-direction="right" :value="user.gender"
                @click="doEdit('gender', '性别', user.gender)"/>
      <van-cell title="手机号" is-link to="/user/edit" arrow-direction="right" :value="user.phone"
                @click="doEdit('phone', '手机号', user.phone)"/>
      <van-cell title="邮箱" is-link to="/user/edit" arrow-direction="right" :value="user.email"
                @click="doEdit('email', '邮箱', user.email)"/>
      <van-cell title="星球编号" arrow-direction="right" :value="user.planetCode" />
      <van-cell title="注册时间" arrow-direction="right" :value="user.createTime" />
      <div style="margin: 8px;">
        <van-button round block type="primary" native-type="submit" @click="logout">
          退出登录
        </van-button>
      </div>
    </div>
  </template>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {onMounted} from "vue";
import {getCurrentUser, logoutCurrentUser} from "../services/user.ts";
import {ref} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import moment from "moment";
import "../app.css"

const user = ref();
const router = useRouter();


onMounted(async () => {
   const res = await getCurrentUser();
   console.log(res);
   if(res.code == 0) {
     res.data.tags = JSON.parse(res.data.tags);
     res.data.createTime = moment(res.data.createTime).format('YYYY-MM-DD HH:mm:ss');
     user.value = res.data;
   } else {
     await router.replace("/user/login");
     showSuccessToast('请先登录');
   }
});

const logout = async () => {
  const res = await logoutCurrentUser();
  if(res.code == 0) {
    await router.replace("/user/login");
    showSuccessToast('退出登录成功');
  } else {
    showFailToast('当前无用户登录');
  }
}

const doEdit = (editKey: string, editName: string, editValue: string) => {
  router.push({
    path:'/user/edit',
    query:{
      editKey,
      editName,
      editValue
    }
  });
}
</script>

<style scoped>

</style>