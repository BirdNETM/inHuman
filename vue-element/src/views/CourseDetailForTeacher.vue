<template>
    <div class="course-edit">
      <h1>编辑课程大纲</h1>
      <textarea v-model="outline" rows="10" placeholder="在此输入课程大纲..."></textarea>
      <button @click="saveOutline">保存大纲</button>
  
      <div class="cards-container">
        <div class="card resources" @click="goTo('ResourcesEdition')">
          <h2>课程资源</h2>
        </div>
        <div class="card assignments" @click="goTo('TeacherAssignments')">
          <h2>课程作业</h2>
        </div>
        <div class="card labs" @click="goTo('Labs')">
          <h2>课程实验</h2>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRoute, useRouter } from 'vue-router';
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  
  const route = useRoute();
  const router = useRouter();
  const courseId = route.params.id;
  
  const outline = ref('');
  const serverIP = localStorage.getItem('serverIP');
  
  const fetchCourseDetails = async (courseId) => {
    try {
      const response = await axios.post(`http://${serverIP}:8080/lessons-detail`, null, {
        params: { lessonId: courseId },
        headers: { 'accessToken': localStorage.getItem('accessToken') }
      });
  
      if (response.data.message === 'NOT_LOGIN') {
        await refreshAuthToken();
      } else {
        outline.value = response.data.data.outline;
      }
    } catch (error) {
      console.error('获取课程数据失败:', error.response ? error.response.data : error);
    }
  };
  
  const saveOutline = async () => {
    try {
      const response = await axios.post(`http://${serverIP}:8080/lessons-outline-update`, null, {
        params: {
          lessonId: courseId,
          outline: outline.value
        },
        headers: { 'accessToken': localStorage.getItem('accessToken') }
      });
  
      if (response.data.success) {
        alert('课程大纲已成功保存!');
      } else {
        console.error('保存失败:', response.data.message);
      }
    } catch (error) {
      console.error('保存课程大纲失败:', error);
    }
  };
  
  const goTo = (section) => {
    router.push({ name: section, params: { id: courseId, fatherId: 0 } });
  };
  
  onMounted(() => {
    fetchCourseDetails(courseId);
  });
  </script>
  
  <style scoped>
  .course-edit {
    width: 85vw;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
  }
  
  textarea {
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 1rem;
  }
  
  button {
    padding: 10px 20px;
    font-size: 1rem;
    background-color: #4caf50; /* 绿色 */
    color: white;
    margin-bottom: 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #45a049; /* 深绿色 */
  }
  
  .cards-container {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 20px;
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
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .card:hover {
    transform: scale(1.05);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }
  
  .resources {
    background-color: #e1f5fe;
  }
  
  .assignments {
    background-color: #ffe0b2;
  }
  
  .labs {
    background-color: #fce4ec;
  }
  
  h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
  }
  
  h2 {
    font-size: 1.5rem;
    margin: 0;
    color: #333;
  }
  </style>
  