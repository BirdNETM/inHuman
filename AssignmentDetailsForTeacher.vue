<template>
  <div class="assignment-details">
    <el-card class="box-card">
      <h2>{{ assignment.title }}</h2>
      <el-divider></el-divider>
      <p>{{ assignment.content }}</p>

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
      <!-- 编辑、删除和查看未提交按钮 -->
      <el-button type="primary" @click="showEditDialog">编辑作业</el-button>
      <el-button type="danger" @click="deleteHomework" style="margin-left: 10px;">删除作业</el-button>
      <el-button type="warning" @click="showNoSubmitStudents" style="margin-left: 10px;">查看未提交学生</el-button>
      <el-button type="warning" @click="showAssignDialog" style="margin-left: 10px;">设置互评</el-button>

    </el-card>

    <el-dialog title="设置互评截止时间" v-model="ismutualDialogVisible">
      <p>请选择截止时间：</p>
      <el-date-picker
        v-model="selectedDeadline"
        type="datetime"
        placeholder="选择截止时间"
        style="width: 100%;"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="ismutualDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="assignMutualReview">确认</el-button>
      </span>
    </el-dialog>


    <!-- 编辑作业的对话框 -->
    <el-dialog title="编辑作业" v-model="dialogVisible">
      <el-form :model="editAssignment">
        <el-form-item label="作业标题">
          <el-input v-model="editAssignment.title"></el-input>
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input type="textarea" v-model="editAssignment.content"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="editAssignment.publicTime" type="datetime" placeholder="选择开始时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="editAssignment.overTime" type="datetime" placeholder="选择截止时间"></el-date-picker>
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
        <el-button type="primary" @click="updateHomework">保存修改</el-button>
      </template>
    </el-dialog>

    <!-- 未提交学生的对话框 -->
    <el-dialog title="未提交作业的学生名单" v-model="noSubmitDialogVisible">
      <ul v-if="noSubmitStudents.length > 0">
        <li v-for="student in noSubmitStudents" :key="student">{{ student }}</li>
      </ul>
      <p v-else>所有学生均已提交作业</p>
      <template #footer>
        <el-button @click="noSubmitDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const assignmentId = route.params.id;
const serverIP = localStorage.getItem('serverIP');
const accessToken = localStorage.getItem('accessToken');

const ismutualDialogVisible = ref(false);
const selectedDeadline = ref(null);
const showAssignDialog = () => {
  ismutualDialogVisible.value = true;
};


const assignment = ref({
  title: '',
  content: '',
  publicTime: '',
  overTime: '',
  filesUrl: '',
});

const editAssignment = ref({ ...assignment.value });
const dialogVisible = ref(false);
const noSubmitDialogVisible = ref(false); // 控制未提交学生对话框的显示
const noSubmitStudents = ref([]); // 存储未提交的学生名单
const selectedFile = ref(null);

// 获取作业详情的函数
const fetchAssignmentDetails = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-id`;
    const response = await axios.post(url, null, {
      params: { homeworkId: assignmentId },
      headers: { accessToken: accessToken },
    });

    if (response.data.success) {
      assignment.value = response.data.data;
      editAssignment.value = { ...assignment.value };
          console.log(accessToken);

    } else {
      ElMessage.error('获取作业详情失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};





const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // 确保月为两位
  const day = String(date.getDate()).padStart(2, "0"); // 确保日为两位
  const hours = String(date.getHours()).padStart(2, "0"); // 确保小时为两位
  const minutes = String(date.getMinutes()).padStart(2, "0"); // 确保分钟为两位
  const seconds = String(date.getSeconds()).padStart(2, "0"); // 确保秒数为两位

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};


const assignMutualReview = async () => {
  if (!selectedDeadline.value) {
    ElMessage.error("请选择截止时间！");
    return;
  }

  // 使用 formatDateTime 函数生成符合示例格式的时间
  const deadlineFormatted = formatDateTime(new Date(selectedDeadline.value));
  console.log(deadlineFormatted); // 输出格式化后的日期

  try {
    const response = await axios.post(
      `http://${serverIP}:8080/AssignTasks`,
      null,
      {
        params: {
          homeworkId: assignmentId, // 作业 ID
          deadLine: deadlineFormatted, // 使用动态生成的截止时间
        },
        headers: {
          accessToken: localStorage.getItem("accessToken"), // 请求头：Token
        },
      }
    );

    if (response.data.success) {
      ElMessage.success("互评任务分配成功！");
      ismutualDialogVisible.value = false; // 关闭弹框
    } else {
      ElMessage.error(`设置互评失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("设置互评失败:", error);
    ElMessage.error("设置互评失败，请稍后重试！");
  }
};


// 显示未提交学生名单的函数
const showNoSubmitStudents = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-didn't-submit`;
    const response = await axios.post(url, null, {
      params: { homeworkId: assignmentId },
      headers: { accessToken: accessToken },
    });

    if (response.data.success) {
      console.log(response.data.data);
      noSubmitStudents.value = response.data.data;
      noSubmitDialogVisible.value = true;
    } else {
      ElMessage.error('获取未提交学生名单失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};



// 下载文件的函数
const downloadFile = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-files-download`;
    const response = await axios.post(url, null, {
      params: { homeworkId: assignmentId },
      headers: { accessToken: accessToken },
      responseType: 'blob',
    });

    if (response.status === 200) {
      const blob = new Blob([response.data]);
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = assignment.value.filesUrl.split('/').pop();
      link.click();
      window.URL.revokeObjectURL(downloadUrl);
    } else {
      ElMessage.error('文件下载失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};

// 显示编辑对话框
const showEditDialog = () => {
  editAssignment.value = { ...assignment.value };
  dialogVisible.value = true;
};

// 删除作业的函数
const deleteHomework = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该作业吗？', '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const url = `http://${serverIP}:8080/Homework-delete`;
    const response = await axios.post(url, null, {
      params: { homeworkId: assignmentId },
      headers: { accessToken: accessToken },
    });

    if (response.data.success) {
      ElMessage.success('作业删除成功');
      router.go(-1); // 返回上一级页面
    } else {
      ElMessage.error('删除作业失败: ' + response.data.message);
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  }
};

// 更新作业的函数
const updateHomework = async () => {
  try {
    const url = `http://${serverIP}:8080/Homework-update`;
    const response = await axios.post(
      url,
      {
        title: editAssignment.value.title,
        content: editAssignment.value.content,
        publicTime: editAssignment.value.publicTime,
        overTime: editAssignment.value.overTime,
      },
      {
        params: { homeworkId: assignmentId },
        headers: { accessToken: accessToken },
      }
    );

    if (response.data.success) {
      if (selectedFile.value) {
        const formData = new FormData();
        formData.append('file', selectedFile.value);
        const fileUploadUrl = `http://${serverIP}:8080/Homework-publish-file`;
        await axios.post(fileUploadUrl, formData, {
          params: { homeworkId: assignmentId },
          headers: {
            accessToken: accessToken,
            'Content-Type': 'multipart/form-data',
          },
        });
      }

      ElMessage.success('作业更新成功');
      assignment.value = { ...editAssignment.value };
      dialogVisible.value = false;
      fetchAssignmentDetails();
    } else {
      ElMessage.error('更新作业失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};

// 格式化日期函数
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
.dialog-footer {
  text-align: right;
  z-index: 1000;
}
</style>
