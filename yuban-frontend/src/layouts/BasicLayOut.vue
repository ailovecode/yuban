<template>
  <div class="layout-container">
    <van-nav-bar
      title="宇伴"
      left-arrow
      fixed
      
      @click-left="onClickLeft"
      @click-right="onClickRight"
    >
      <template #right>
        <van-icon name="search" size="18" />
      </template>
    </van-nav-bar>

    <div id="content">
      <router-view />
    </div>


    <van-tabbar route safe-area-inset-bottom  @change="onChange">
      <van-tabbar-item to="/" icon="home-o" name="index">主页</van-tabbar-item>
      <van-tabbar-item to="/team" icon="friends-o" name="Team">队伍</van-tabbar-item>
      <van-tabbar-item to="/user" icon="contact-o" name="User">个人</van-tabbar-item>
    </van-tabbar>
  </div>
  
</template>

<script setup >

import {showToast} from "vant";
import {useRouter} from "vue-router";

const router = useRouter();

const onClickLeft = () => {
  router.back();
};
const onClickRight = () => {
  router.push('/search');
};

const onChange = (name) => showToast(`标签 ${name}`);

</script>

<style>
.layout-container {
  padding-top: 46px; /* 导航栏高度 */
  height: 100vh;
  position: relative;
}

#content {
  padding-bottom: 50px;
  height: calc(100vh - 96px); /* 减去导航栏和底部tabbar的高度 */
  overflow: hidden;
}
</style>