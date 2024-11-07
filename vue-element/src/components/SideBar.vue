<template>
  <el-menu
    class="el-menu-vertical-demo"
    :default-active="activeMenu"
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#ffd04b"
    unique-opened
    router
  >
    <el-sub-menu v-for="item in menuData" :key="item.index" :index="item.index">
      <template #title>
        <el-icon :size="20">
          <component :is="icons[item.icon]" />
        </el-icon>
        <span>{{ item.title }}</span>
      </template>
      <el-menu-item
        v-for="subItem in item.children"
        :key="subItem.index"
        :index="subItem.index"
        :route="{ path: `/Home/${subItem.index}` }"
      >
        {{ subItem.title }}
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script>
import { ref } from 'vue';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';

export default {
  setup() {
    const route = useRoute();
    const activeMenu = ref('');

    // 动态设置 activeMenu, 比如根据路由的 path 来设置
    const currentPath = route.path;
    if (currentPath.includes('/Home/1-1')) {
      activeMenu.value = '1-1';
    } else if (currentPath.includes('/Home/1-2')) {
      activeMenu.value = '1-2';
    } else if (currentPath.includes('/Home/2-1')) {
      activeMenu.value = '2-1';
    } else if (currentPath.includes('/Home/2-2')) {
      activeMenu.value = '2-2';
    } else if (currentPath.includes('/Home/3-1')) {
      activeMenu.value = '3-1';
    } else if (currentPath.includes('/Home/3-2')) {
      activeMenu.value = '3-2';
    } else if (currentPath.includes('/Home/3-3')) {
      activeMenu.value = '3-3';
    } else {
      activeMenu.value = ''; // 默认不选中任何菜单项
    }

    // 菜单数据
    const menuData = ref([
      {
        index: '1',
        title: '课程',
        icon: 'House',
        children: [
          {
            index: '1-1',
            title: '我的课程',
          },
          {
            index: '1-2',
            title: '我的课表',
          },
        ],
      },
      {
        index: '2',
        title: '通知',
        icon: 'Bell',
        children: [
          {
            index: '2-1',
            title: '学校通知',
          },
          {
            index: '2-2',
            title: '课程互动消息',
          },
        ],
      },
      {
        index: '3',
        title: '个人中心',
        icon: 'Setting',
        children: [
          {
            index: '3-1',
            title: '个人信息',
          },
          {
            index: '3-2',
            title: '修改密码',
          },
          {
            index: '3-3',
            title: '通知',
          },
        ],
      },
    ]);

    // 将所有图标组件注册到 Vue
    const icons = {};
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
      icons[key] = component;
    }

    return { activeMenu, menuData, icons };
  },
};
</script>


<style scoped>
.el-menu-vertical-demo {
  min-width: 15vw; /* 修改这里的宽度 */
  max-width: 15vw;

  height: 100vh;
  border-right: none;
}

.el-sub-menu .el-icon {
  margin-right: 5px;
}
</style>
