<template>
    <div class="labs">
      <h1>学生端 - 课程实验</h1>
  
      <el-row :gutter="20">
        <el-col
          v-for="lab in labs"
          :key="lab.id"
          :span="24"
        >
          <el-card class="lab-item" @click="viewLabDetails(lab.id)" shadow="hover">
            <div class="lab-content">
              <!-- 实验名称和成绩 -->
              <div class="lab-header">
                <el-link :underline="false" class="lab-title">
                  {{ lab.examName }}
                </el-link>
                <el-tag v-if="lab.mark !== null" type="success" class="lab-mark">
                  成绩: {{ lab.mark }}
                </el-tag>
              </div>
              <!-- 实验时间和描述 -->
              <div class="lab-dates">
                <el-tag type="info">开始时间: {{ formatDate(lab.examStartTime) }}</el-tag>
                <el-tag type="warning">截止时间: {{ formatDate(lab.examEndTime) }}</el-tag>
              </div>
              <div class="lab-description">
                <p>{{ lab.examDescription }}</p>
              </div>
              <!-- 教室信息 -->
              <div v-if="lab.classroom_name && lab.classroom_name.length" class="lab-classrooms">
                <el-tag type="success" v-for="(room, index) in lab.classroom_name" :key="index">
                  {{ room }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute, useRouter } from 'vue-router';
  import { ElMessage } from 'element-plus';
  
  const route = useRoute();
  const router = useRouter();
  const lessonId = route.params.id;
  const serverIP = localStorage.getItem('serverIP');
  const accessToken = localStorage.getItem('accessToken');
  
  const labs = ref([]);
  
  // 格式化日期
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };
  
  // 获取实验列表
  const fetchLabs = async () => {
    try {
      const url = `http://${serverIP}:8080/Exam-get`;
      const response = await axios.post(url, null, {
        params: { lessonId },
        headers: { accessToken },
      });
  
      if (response.data.success) {
        labs.value = response.data.data.map(lab => ({ ...lab, mark: null })); // 初始化 mark 为 null
        fetchMarks(); // 获取每个实验的成绩
      } else {
        ElMessage.error('获取实验列表失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };
  
  // 获取每个实验的成绩
  const fetchMarks = async () => {
    for (const lab of labs.value) {
      try {
        const url = `http://${serverIP}:8080/Exam-getMarkById`;
        const response = await axios.post(url, null, {
          params: { examId: lab.id },
          headers: { accessToken },
        });
  
        if (response.data.success) {
          lab.mark = response.data.data; // 将获取到的成绩存入实验对象
        } else {
          ElMessage.error(`获取成绩失败（实验 ID: ${lab.id}）: ${response.data.message}`);
        }
      } catch (error) {
        ElMessage.error(`请求失败（实验 ID: ${lab.id}）: ${error.response ? error.response.data : error.message}`);
      }
    }
  };
  
  // 跳转到 PaperDetails.vue，并传递 examId
  const viewLabDetails = (examId) => {
    router.push({ name: 'PaperDetails', params: { examId } });
  };
  
  onMounted(() => {
    fetchLabs();
  });
  </script>
  
  <style scoped>
  .labs {
    width: 85vw;
    max-width: 85vw;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
    overflow-y: auto;
  }
  
  h1 {
    font-size: 2rem;
    margin-bottom: 20px;
  }
  
  .lab-item {
    margin-bottom: 15px;
    cursor: pointer; /* 指针样式，表示可点击 */
  }
  
  .lab-content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .lab-header {
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
  }
  
  .lab-title {
    font-size: 1.2rem;
    color: #409eff;
  }
  
  .lab-mark {
    font-size: 1rem;
  }
  
  .lab-dates,
  .lab-classrooms {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .lab-description {
    font-size: 1rem;
    color: #666;
  }
  </style>
  