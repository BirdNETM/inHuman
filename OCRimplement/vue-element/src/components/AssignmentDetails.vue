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
          @click.prevent="downloadFile"
          :underline="false"
        >
          下载附件
        </el-link>
        <span v-else>无附件</span>
      </div>

      <el-divider></el-divider>
      <div class="assignment-status">
        <el-tag v-if="assignment.status == '待批改'" type="success">已提交</el-tag>
        <el-tag v-else type="danger">未提交</el-tag>
      </div>

      <el-divider></el-divider>
      <!-- 上传作业 -->
      <div v-if="!assignment.completed" class="assignment-submit">
        <el-upload
          action=""
          :before-upload="handleFileSelect"
          :show-file-list="false"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>
        <span v-if="selectedFile">{{ selectedFile.name }}</span>
        <el-button
          type="success"
          @click="submitHomework"
          :disabled="!selectedFile"
          style="margin-top: 10px;"
        >
          提交作业
        </el-button>
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

const selectedFile = ref(null);

const fetchAssignmentDetails = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-id`;
    const response = await axios.post(url, null, {
      params: {
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

// 下载文件
const downloadFile = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-files-download`;
    const response = await axios.post(
      url,
      null,
      {
        params: { homeworkId: assignmentId },
        headers: { accessToken: accessToken },
        responseType: 'blob', // 返回文件数据
      }
    );

    if (response.status === 200) {
      // 创建一个URL链接用于下载文件
      const blob = new Blob([response.data]);
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = assignment.value.filesUrl.split('/').pop(); // 使用文件名作为下载名称
      link.click();
      window.URL.revokeObjectURL(downloadUrl);
    } else {
      ElMessage.error('文件下载失败');
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

// 提交作业
const submitHomework = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件');
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  formData.append('homeworkId', assignmentId);

  try {
    const url = `http://${serverIP}:8080/Homework-submit`;
    const response = await axios.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        accessToken: accessToken,
      },
    });

    if (response.data.success) {
      ElMessage.success('作业提交成功');
      assignment.value.completed = true; // 更新提交状态
    } else {
      ElMessage.error('提交作业失败: ' + response.data.message);
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
  overflow-y: auto;
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

.assignment-submit {
  text-align: center;
}
</style>
