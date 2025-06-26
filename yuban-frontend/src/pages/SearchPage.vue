<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入要搜索的标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0"  class="empty-tags-placeholder">
    <van-icon name="warning" style="margin-right: 6px" />
    请选择标签
  </div>
  <van-row gutter="6" style="display: flex; flex-wrap: wrap; gap: 8px 4px; padding: 0 12px">
    <van-col v-for="tag in activeIds">
      <van-tag closeable size="medium" type="primary" @close="close(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>


  <van-divider content-position="left">标签列表</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />

  <div style="padding: 12px">
    <van-button type="primary" block @click="doSearchUser()">搜索</van-button>
  </div>

</template>

<script setup>
import {ref} from 'vue';
import {showToast} from 'vant';
import {useRouter} from "vue-router";

const originalTagList = [
  {
    text: '性别',
    children: [
      { text: '男', id: '男' },
      { text: '女', id: '女' },
    ],
  },
  {
    text: '学业状态',
    children: [
      { text: '大一', id: '大一' },
      { text: '大二', id: '大二' },
      { text: '大三', id: '大三' },
      { text: '大四', id: '大四' },
      { text: '在读本科', id: '在读本科' },
      { text: '应届毕业生', id: '应届毕业生' },
      { text: '在读研究生', id: '在读研究生' },
      { text: '在读博士', id: '在读博士' },
      { text: '在职', id: '在职' },
    ],
  },
];

const searchText = ref('');

const activeIds = ref([]);
const activeIndex = ref(0);

let tagList = ref(originalTagList);

const onSearch = () => {
  // 过滤列中的信息
  let tagNums = 0;
  let index = 0;
  tagList.value = originalTagList.map(parentTag => {
    // 复制数组 和 对象
    const tempChildren = [...parentTag.children];
    const tempParentTag = {...parentTag};
    // 在子数组中过滤信息
    tempParentTag.children = tempChildren.filter(item => {
      return item.text.includes(searchText.value);
    })
    // 如果这个子数组存在查找的信息
    if(tempParentTag.children.length > 0) {
      tagNums += tempParentTag.children.length;
      activeIndex.value = index;
    }
    // 记录父标签的索引，用于重定向查找结果
    ++ index;
    return tempParentTag;
  })

  // 做一个提示
  if (tagNums === 0) {
    showToast("没有此标签！")
  }
};
const onCancel = () => {
  searchText.value = '';
  tagList.value = originalTagList;
};

const close = (tag) => {
  // 通过在 tagList 中过滤删除的信息来删除
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  })
};

const router = useRouter();

const doSearchUser = () =>{
  router.push({
        path: "/searchResult",
        query: {
          tag: activeIds.value
        },
  });
}


</script>


<style>
.empty-tags-placeholder {
  color: #c0c4cc;  /* 浅灰色，类似输入框 placeholder */
  font-size: 14px;  /* 适中字号 */
  text-align: center;  /* 居中显示 */
  padding: 12px 0;  /* 上下内边距增加留白 */
  opacity: 0.8;  /* 轻微透明 */
  font-style: italic;  /* 斜体（可选） */
  border: 1px dashed #dcdfe6;  /* 虚线边框（可选） */
  border-radius: 4px;  /* 圆角 */
  background-color: #f5f7fa;  /* 浅背景色（可选） */
}
.empty-tags-placeholder:hover {
  opacity: 1;
  border-color: #409eff;  /* 主题色 */
}

@media (max-width: 768px) {
  .empty-tags-placeholder {
    font-size: 12px;
    padding: 8px 0;
  }
}
</style>