<template>
    <div class="notice-detail-container">
      <el-button type="primary" @click="goBack">返回</el-button>
  
      <h1>通知详情</h1>
      <div v-if="notice">
        <el-card class="notice-detail-card">
          <h2>{{ notice.title }}</h2>
          <p><strong>发送者:</strong> {{ notice.sender }}</p>
          <p><strong>内容:</strong> {{ notice.content || '无详细内容' }}</p>
          <p><strong>时间:</strong> {{ formatTime(notice.time) }}</p>
          <p><strong>状态:</strong> {{ notice.state === 1 ? '已读' : '未读' }}</p>
        </el-card>
      </div>
      <div v-else>
        <p>加载中...</p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router'; // 确保引入 router
  import axios from 'axios';
  
  const route = useRoute(); // 获取当前路由信息
  const router = useRouter(); // 确保 router 可用
  const noticeId = route.params.id; // 获取路由参数中的 id
  const notice = ref(null); // 用于存储通知详情
  const serverIP = localStorage.getItem('serverIP'); // 假设你已经在 localStorage 中保存了 serverIP
  
  // 格式化时间的函数
  const formatTime = (time) => {
    const date = new Date(time);
    return date.toLocaleString();
  };
  
  // 刷新令牌的函数
  const refreshAuthToken = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      const response = await axios.post(`http://${serverIP}:8080/refresh-token`, {
        refreshToken: refreshToken
      });
  
      if (response.data.success) {
        const newAccessToken = response.data.data.accessToken;
        const newRefreshToken = response.data.data.refreshToken;
  
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
  
        console.log('新令牌已获取并存储:', newAccessToken, newRefreshToken);
  
        // 刷新成功后重新获取通知数据
        await fetchNotice(noticeId);
      } else {
        // 刷新失败则跳转到登录页面
        router.push({ name: 'Login' });
      }
    } catch (error) {
      console.error('刷新令牌请求失败:', error);
      router.push({ name: 'Login' });
    }
  };
  
  const fetchNotice = async (noticeId) => {
    try {
      const accessToken = localStorage.getItem('accessToken'); // 获取 accessToken
  
      // 发送 POST 请求，传递 noticeId 和 accessToken
      const response = await axios.post(`http://${serverIP}:8080/Notices-detail`, 
      {
        noticeId: noticeId // 请求体传递 noticeId
      }, 
      {
        headers: {
          'Content-Type': 'application/json',
          'accessToken': accessToken // 请求头传递 accessToken
        }
      });
  
      // 如果令牌过期
      if (response.data.message === 'NOT_LOGIN') {
        console.log('令牌过期，需要刷新');
        await refreshAuthToken();
      } else {
        notice.value = response.data.data; // 将通知详情保存到响应式变量
        console.log('通知详情获取成功:', notice.value);
      }
    } catch (error) {
      if (error.response) {
        // 服务器端返回了错误状态码
        console.error('Error status:', error.response.status);
        console.error('Error data:', error.response.data);
      } else if (error.request) {
        // 请求已发出，但没有收到响应
        console.error('No response:', error.request);
      } else {
        // 发送请求时出了点问题
        console.error('Error:', error.message);
      }
    }
  };
  
  const markAsRead = async (noticeId) => {
    try {
      const accessToken = localStorage.getItem('accessToken'); // 获取 accessToken
      // 发送请求，标记通知为已读
      await axios.post(`http://${serverIP}:8080/Notices-mark-as-read`, 
      {
        noticeId: noticeId // 传递通知的 ID
      }, 
      {
        headers: {
          'Content-Type': 'application/json',
          'accessToken': accessToken // 请求头传递 accessToken
        }
      });
  
      console.log('通知已标记为已读');
    } catch (error) {
      if (error.response) {
        console.error('Error status:', error.response.status);
        console.error('Error data:', error.response.data);
      } else if (error.request) {
        console.error('No response:', error.request);
      } else {
        console.error('Error:', error.message);
      }
    }
  };
  
  
  const goBack = () => {
    router.go(-1); // 使用 Vue Router 的 go 方法返回到上一页
  };
  // 在组件挂载时获取通知详情
  onMounted(() => {
    fetchNotice(noticeId); // 确保传递 noticeId 参数
    markAsRead(noticeId); // 页面加载时标记通知为已读
  });
  
  </script>
  
  <style scoped>
  .notice-detail-container {
    padding: 20px;
  }
  
  .notice-detail-card {
    padding: 20px;
    background-color: #f9f9f9;
    border: 1px solid #eaeaea;
  }
  </style>
  