<template>
    <div>
      <h1>课程详情</h1>
      <p>课程 ID: {{ courseId }}</p> <!-- 显示课程 ID -->
    </div>
  </template>
  
  <script setup>
  import { useRoute } from 'vue-router';
  
  const route = useRoute(); // 获取当前路由信息
  const courseId = route.params.id; // 访问路由参数中的 id
  </script>
  