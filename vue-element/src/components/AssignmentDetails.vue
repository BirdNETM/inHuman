<template>
    <div class="assignment-details">
      <el-card class="box-card">
        <h2>{{ assignment.title }}</h2>
        <el-divider></el-divider>
  
        <div class="assignment-content">
          <p>{{ assignment.content }}</p>
        </div>
  
        <el-divider></el-divider>
        <div class="assignment-info">
          <el-tag type="info">开始时间: {{ formatDate(assignment.publicTime) }}</el-tag>
          <el-tag type="warning">截止时间: {{ formatDate(assignment.overTime) }}</el-tag>
        </div>
  
        <el-divider></el-divider>
        <div class="assignment-file">
          <p>附件下载:</p>
          <el-link
            v-if="assignment.filesUrl"
            :href="generateDownloadLink(assignment.filesUrl)"
            target="_blank"
            :underline="false"
          >
            下载附件
          </el-link>
          <span v-else>无附件</span>
        </div>
  
        <el-divider></el-divider>
        <div class="assignment-status">
          <el-tag v-if="assignment.completed" type="success">已提交</el-tag>
          <el-tag v-else type="danger">未提交</el-tag>
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';
  import { ElMessage } from 'element-plus';
  
  const route = useRoute();
  const assignmentId = route.params.id;
  const serverIP = localStorage.getItem('serverIP');
  const accessToken = localStorage.getItem('accessToken');
  
  const assignment = ref({
    title: '',
    content: '',
    publicTime: '',
    overTime: '',
    filesUrl: '',
    completed: false
  });
  
  const fetchAssignmentDetails = async () => {
    try {
      const url = `http://${serverIP}:8080/Homework-id`;
      const response = await axios.post(url, null, {
        params:{
            homeworkId: assignmentId,
        },
        headers: {
          accessToken: accessToken,
        },
      });
  
      if (response.data.success) {
        assignment.value = response.data.data;
      } else {
        ElMessage.error('获取作业详情失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
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
  
  const generateDownloadLink = (filePath) => {
    return `http://${serverIP}:8080/download?path=${encodeURIComponent(filePath)}`;
  };
  
  onMounted(() => {
    fetchAssignmentDetails();
  });
  </script>
  
  <style scoped>
  .assignment-details {
    width: 85vw;
    max-width: 85vw;
    margin: 0 auto;
    padding: 20px;
  }
  
  h2 {
    font-size: 1.5rem;
    margin-bottom: 10px;
  }
  
  .assignment-content {
    font-size: 1rem;
    margin-bottom: 15px;
  }
  
  .assignment-info {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .assignment-file {
    font-size: 1rem;
    margin-bottom: 15px;
  }
  
  .assignment-status {
    text-align: center;
    margin-top: 15px;
  }
  </style>
  