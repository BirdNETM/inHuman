<script>
import SideBar from './components/SideBar.vue';
import Login from './views/Login.vue';
import { RouterView } from 'vue-router';

export default {
  components: { SideBar,Login },
  setup(){
    const serverIP = '192.168.188.212';
    localStorage.setItem('serverIP',serverIP);
  }
};
</script>

<template>
  <div class="app-container">
    <router-view></router-view>
  </div>
</template>

<style scoped>

#app {
  padding: 0;
  margin: 0;
}

.app-container {
  display: flex; /* 使用 Flexbox 布局 */
  height: 100vh; /* 使整个布局占满屏幕高度 */
  width: 100vw;
}

.sidebar {
  width: 200px; /* 侧边栏宽度 */
  background-color: #545c64; /* 背景色与 Element Plus 的侧边栏风格一致 */
  color: #fff;
  height: 100%; /* 使侧边栏高度与容器高度一致 */
}

.content {
  flex: 1; /* 主内容区域占据剩余空间 */
  padding: 20px; /* 添加一些内边距 */
  overflow-y: auto; /* 使内容区域可滚动 */
  background-color: #f5f5f5; /* 设置背景颜色以区分内容区域 */
}
</style>
