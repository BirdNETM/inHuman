<template>
  <div class="course-details">
    <h1>课程详情</h1>
    <p v-if="outline">{{ outline }}</p> <!-- 显示课程大纲 -->

    <div class="cards-container">
      <!-- 课程资源卡片 -->
      <div class="card resources" @click="goTo('Resources')">
        <h2>课程资源</h2>
      </div>

      <!-- 课程作业卡片 -->
      <div class="card assignments" @click="goTo('Assignments')">
        <h2>课程作业</h2>
      </div>

      <!-- 课程讨论区卡片 -->
      <div class="card labs" @click="goTo('DiscussionList')">
        <h2>课程讨论区</h2>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';
import axios from 'axios'; // 确保引入 axios

const route = useRoute(); // 获取当前路由信息
const router = useRouter(); // 获取路由对象
const courseId = route.params.id; // 访问路由参数中的 id

// 定义响应式变量来存储大纲
const outline = ref('');

const serverIP = localStorage.getItem('serverIP');

// 刷新令牌的函数
const refreshAuthToken = async () => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    console.log('刷新令牌:', refreshToken);

    const response = await axios.post(`http://${serverIP}:8080/refresh-token`, {
      refreshToken: refreshToken
    });

    if (response.data.success) {
      const newAccessToken = response.data.data.accessToken;
      const newRefreshToken = response.data.data.refreshToken;

      // 将新令牌存储到 localStorage 中
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);

      console.log('新令牌已获取并存储:', newAccessToken, newRefreshToken);

      // 刷新成功后重新获取课程数据
      await fetchCourseDetails(courseId); // 传入课程 ID
    } else {
      // 刷新失败则跳转到登录页面
      router.push({ name: 'Login' });
    }
  } catch (error) {
    console.error('刷新令牌请求失败:', error);
    router.push({ name: 'Login' });
  }
};

// 获取课程数据的函数
const fetchCourseDetails = async (courseId) => {
  try {
    const response = await axios.post(`http://${serverIP}:8080/lessons-detail`, null, {
      params: {
        lessonId: courseId // 将课程 ID 作为查询参数传递
      },
      headers: {
        'accessToken': localStorage.getItem('accessToken') // 使用 accessToken
      }
    });

    // 检查是否需要刷新令牌
    if (response.data.message === 'NOT_LOGIN') {
      console.log('令牌过期，需要刷新');
      await refreshAuthToken();
    } else {
      outline.value = response.data.data.outline; // 获取大纲内容
      console.log('课程数据获取成功:', outline.value);
    }
  } catch (error) {
    console.error('获取课程数据失败:', error.response ? error.response.data : error);
  }
};

// 跳转函数
const goTo = (section) => {
  router.push({ name: section, params: { id: courseId,fatherId:0} });
};

// 组件挂载时调用 API
onMounted(() => {
  fetchCourseDetails(courseId); // 传入课程 ID
});
</script>

<style scoped>
.course-details {
  width: 85vw;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.cards-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px; /* 设置卡片之间的间隔 */
}

.card {
  flex: 1;
  padding: 20px;
  height: 16vh;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  text-align: center;
  display: flex; /* 使用flex布局让标题居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
}

.card:hover {
  transform: scale(1.05); /* 悬停时放大 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15); /* 更大的阴影效果 */
}

.resources {
  background-color: #e1f5fe; /* 浅蓝色 */
}

.assignments {
  background-color: #ffe0b2; /* 浅橙色 */
}

.labs {
  background-color: #fce4ec; /* 浅粉色 */
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 10px; /* 更改底部间距 */
}

p {
  font-size: 1.2rem; /* 大纲文本大小 */
  color: #666; /* 大纲文本颜色 */
  margin-bottom: 40px; /* 底部间距 */
}

h2 {
  font-size: 1.5rem;
  margin: 0;
  color: #333;
}
</style>
