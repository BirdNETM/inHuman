<template>
    <div class="assignments">
      <h1>课程作业</h1>
      <el-row :gutter="20">
        <el-col
          v-for="assignment in assignments"
          :key="assignment.id"
          :span="24"
        >
          <el-card class="assignment-item">
            <div class="assignment-content">
              <!-- 作业名称 -->
              <el-link
                @click="viewDetails(assignment.id)"
                :underline="false"
                class="assignment-title"
              >
                {{ assignment.title }}
              </el-link>
              <!-- 公布时间和截止时间 -->
              <div class="assignment-dates">
                <el-tag type="info">开始时间: {{ formatDate(assignment.publicTime) }}</el-tag>
                <el-tag type="warning">截止时间: {{ formatDate(assignment.overTime) }}</el-tag>
              </div>
              <!-- 提交状态或提交按钮 -->
              <div class="assignment-status">
                <el-button
                    v-if="assignment.status == '未提交'"
                    type="primary"
                    @click="submitAssignment(assignment.id)"
                    class="upload-button"
                    >
                    提交
                    </el-button>

                <el-tag v-else type="success" class="completed-text">
                  已提交
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 文件提交框 -->
      <input
        type="file"
        ref="fileInput"
        style="display: none"
        @change="handleFileChange"
      />
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
  
  const assignments = ref([]);
  const selectedAssignmentId = ref(null);
  
  const fetchAssignments = async () => {
    try {
      const url = `http://${serverIP}:8080/Homework`;
      const response = await axios.post(url, null, {
        params: {
          lessonId: lessonId,
        },
        headers: {
          accessToken: accessToken,
        },
      });
  
      if (response.data.success) {
        assignments.value = response.data.data;
      } else {
        ElMessage.error('获取作业失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };
  
  const viewDetails = (assignmentId) => {
    // 跳转到作业详情页面
    router.push({ name: 'AssignmentDetails', params: { id: assignmentId } });
  };
  
  const submitAssignment = (assignmentId) => {
    // 存储当前选择的作业 ID
    selectedAssignmentId.value = assignmentId;
    // 触发文件选择框
    const fileInput = document.querySelector('input[type="file"]');
    fileInput.click();
  };
  
  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
  
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('assignmentId', selectedAssignmentId.value);
  
      const url = `http://${serverIP}:8080/Homework-submit`;
      await axios.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          accessToken: accessToken,
        },
        params: {
          'homeworkId':selectedAssignmentId.value,
        }
      });
  
      ElMessage.success('作业提交成功');
      fetchAssignments(); // 重新获取作业信息，更新已提交状态
    } catch (error) {
      ElMessage.error('作业提交失败: ' + (error.response ? error.response.data : error.message));
    } finally {
      // 重置文件输入
      event.target.value = null;
    }
  };
  
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
  
  onMounted(() => {
    fetchAssignments();
  });
  </script>
  
  <style scoped>
  .assignments {
    width: 85vw;
    max-width: 85vw;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
    overflow-y: auto
  }
  
  h1 {
    font-size: 2rem;
    margin-bottom: 20px;
  }
  
  .assignment-item {
    margin-bottom: 15px;
  }
  
  .assignment-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
  }
  
  .assignment-title {
    flex: 2;
    font-size: 1.2rem;
    color: #409eff;
    cursor: pointer;
  }
  
  .assignment-title:hover {
    color: #66b1ff;
  }
  
  .assignment-dates {
    flex: 3;
    display: flex;
    gap: 10px;
  }
  
  .assignment-status {
    flex: 1;
    text-align: center;
  }
  
  .completed-text {
    color: green;
    font-weight: bold;
  }
  </style>
  