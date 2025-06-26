<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editUser.editValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${ editUser.editName }` "
          :rules="[{ required: true, message: `请输入 ${ editUser.editName }` }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {ref} from "vue"
import myAxios from "../plugins/myAxios"
import {getCurrentUser} from "../services/user.ts";
import {showSuccessToast} from "vant";

const route = useRoute();
const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  editValue: route.query.editValue,
})
const router = useRouter();



const onSubmit = async () => {
  const userId = await getCurrentUser();
  const res = await myAxios.post('/user/update', {
    "id": userId.data.id,
    [editUser.value.editKey as string]: editUser.value.editValue
  })
  console.log(res);
  if(res.code == 0 && res.data > 0) {
    showSuccessToast('修改成功');
    router.back();
  }
};



</script>

<style scoped>

</style>