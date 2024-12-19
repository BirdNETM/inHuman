<template>
    <div class="home-container">
      <SideBar></SideBar>
      <router-view /> <!-- 使用 router-view 来渲染子路由组件 -->
    </div>
  </template>
  
  <script>
  import SideBar from '@/components/SideBar.vue';
  import MyOptions from './MyOptions.vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  export default {
    components: {
      SideBar,MyOptions,
    },
    setup() {
      const router = useRouter();

    }
  };
  </script>
  
  <style scoped>
  .home-container {
    display: flex;
    height: 100vh; /* 让容器充满整个视口高度 */
  }
  
  .SideBar {
    width: 200px; /* 假设侧边栏宽度为200px */
    flex-shrink: 0; /* 确保侧边栏不会缩小 */
  }
  
  </style>
  