<template>
    <div class="home-container">
      <TeacherBar></TeacherBar>
      <router-view /> <!-- 使用 router-view 来渲染子路由组件 -->
    </div>
  </template>
  
  <script>
  import TeacherBar from '@/components/TeacherBar.vue';
  import MtTeaching from './MyTeaching.vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  export default {
    components: {
        TeacherBar,MtTeaching,
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

  .TeacherBar {
    width: 200px; /* 假设侧边栏宽度为200px */
    flex-shrink: 0; /* 确保侧边栏不会缩小 */
  }
  
  </style>
  