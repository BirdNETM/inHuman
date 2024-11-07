<template>
    <div class="assignments">
      <h1>教师端 - 课程作业管理</h1>
      <el-button type="primary" @click="showAssignmentDialog" style="margin-bottom: 20px;">
        布置作业
      </el-button>
  
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
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 布置作业对话框 -->
      <el-dialog v-model="dialogVisible" title="布置新作业">
        <el-form ref="assignmentForm" :model="newAssignment">
          <el-form-item label="作业标题" required>
            <el-input v-model="newAssignment.title" placeholder="请输入作业标题"></el-input>
          </el-form-item>
          <el-form-item label="作业内容" required>
            <el-input type="textarea" v-model="newAssignment.content" placeholder="请输入作业内容"></el-input>
          </el-form-item>
          <el-form-item label="开始时间" required>
            <el-date-picker v-model="newAssignment.publicTime" type="datetime" placeholder="选择开始时间"></el-date-picker>
          </el-form-item>
          <el-form-item label="截止时间" required>
            <el-date-picker v-model="newAssignment.overTime" type="datetime" placeholder="选择截止时间"></el-date-picker>
          </el-form-item>
          <el-form-item label="附件">
            <el-upload :before-upload="handleFileSelect" :show-file-list="false">
              <el-button>选择文件</el-button>
            </el-upload>
            <span v-if="selectedFile">{{ selectedFile.name }}</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="publishHomework">发布作业</el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';
  import { ElMessage } from 'element-plus';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  
  const route = useRoute();
  const lessonId = route.params.id;
  const serverIP = localStorage.getItem('serverIP');
  const accessToken = localStorage.getItem('accessToken');
  
  const assignments = ref([]);
  const dialogVisible = ref(false);
  const newAssignment = ref({
    title: '',
    content: '',
    publicTime: '',
    overTime: '',
  });
  const selectedFile = ref(null);
  
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
  
  // 显示布置作业对话框
  const showAssignmentDialog = () => {
    dialogVisible.value = true;
  };
  
  // 获取作业列表
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
        ElMessage.error('获取作业列表失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };
  
  // 选择文件
  const handleFileSelect = (file) => {
    selectedFile.value = file;
    return false; // 阻止自动上传
  };
  
  // 发布作业
  const publishHomework = async () => {
    try {
      // 发布作业
      const publishUrl = `http://${serverIP}:8080/Homework-publish`;
      const publishResponse = await axios.post(
        publishUrl,
        {
          title: newAssignment.value.title,
          content: newAssignment.value.content,
          publicTime: newAssignment.value.publicTime,
          overTime: newAssignment.value.overTime,
        },
        {
          params: { lessonId: lessonId },
          headers: { accessToken: accessToken },
        }
      );
  
      if (publishResponse.data.success) {
        const homeworkId = publishResponse.data.data;
  
        // 上传附件（如果有选择文件）
        if (selectedFile.value) {
          const formData = new FormData();
          formData.append('file', selectedFile.value);
          const fileUploadUrl = `http://${serverIP}:8080/Homework-publish-file`;
          await axios.post(fileUploadUrl, formData, {
            params: { homeworkId: homeworkId },
            headers: { accessToken: accessToken, 'Content-Type': 'multipart/form-data' },
          });
        }
  
        ElMessage.success('作业发布成功');
        dialogVisible.value = false;
        fetchAssignments(); // 重新获取作业列表
      } else {
        ElMessage.error('发布作业失败: ' + publishResponse.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };

  const viewDetails = (assignmentId) => {
  // 跳转到作业详情页面，并传递作业 ID
  router.push({ name: 'AssignmentDetails', params: { id: assignmentId } });
};
  
  // 页面加载时获取作业列表
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
  </style>
  