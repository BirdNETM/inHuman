<template>
  <div class="exams">
    <h1>教师端 - 课程考试管理</h1>
    <el-button type="primary" @click="openCreateDialog" class="create-exam-btn">
      创建考试
    </el-button>

    <!-- 创建/修改考试弹窗 -->
    <el-dialog title="考试管理" v-model="showExamDialog" width="40%">
      <el-form :model="newExam" label-width="80px" class="exam-form">
        <el-form-item label="名称">
          <el-input v-model="newExam.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="newExam.examStartTime"
            type="datetime"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="newExam.examEndTime"
            type="datetime"
            placeholder="选择截止时间"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="newExam.examDescription" placeholder="请输入考试描述" />
        </el-form-item>
        <el-form-item label="教室">
          <el-select v-model="newExam.classrooms" multiple placeholder="选择教室">
            <el-option
              v-for="classroom in classroomOptions"
              :key="classroom.classroom_id"
              :label="classroom.classroom_name"
              :value="classroom"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button @click="showExamDialog = false">取消</el-button>
        <el-button type="primary" @click="submitExam">
          {{ isEdit ? "保存修改" : "创建" }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 考试卡片列表 -->
    <el-row :gutter="20">
      <el-col
        v-for="exam in exams"
        :key="exam.id"
        :span="24"
      >
        <el-card
          class="exam-item"
          shadow="hover"
          @click="viewExamPaper(exam.id)"
        >
          <div class="exam-content">
            <el-link :underline="false" class="exam-title">
              {{ exam.examName }}
            </el-link>
            <div class="exam-dates">
              <el-tag type="info">开始时间: {{ formatDate(exam.examStartTime) }}</el-tag>
              <el-tag type="warning">截止时间: {{ formatDate(exam.examEndTime) }}</el-tag>
            </div>
            <div class="exam-description">
              {{ exam.examDescription }}
            </div>
            <div v-if="exam.classroom_name && exam.classroom_name.length" class="exam-classrooms">
              <el-tag type="success" v-for="(room, index) in exam.classroom_name" :key="index">
                {{ room }}
              </el-tag>
            </div>
          </div>
          <div class="exam-actions" @click.stop>
            <el-button type="success" size="mini" @click="goToManualScore(exam.id)">批改</el-button>
            <el-button type="primary" size="mini" @click="editExam(exam)">修改</el-button>
            <el-button type="danger" size="mini" @click="deleteExam(exam.id)">删除</el-button>
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

const exams = ref([]);
const showExamDialog = ref(false);
const isEdit = ref(false); // 判断是创建还是修改
const newExam = ref({
  id: null, // 考试ID，修改时需要
  examName: '',
  examStartTime: '',
  examEndTime: '',
  examDescription: '',
  classrooms: [],
});
const classroomOptions = ref([]);

// 打开创建弹窗
const openCreateDialog = () => {
  isEdit.value = false;
  resetForm();
  showExamDialog.value = true;
};

// 打开修改弹窗
const editExam = (exam) => {
  isEdit.value = true;
  newExam.value = { ...exam };
  newExam.value.classrooms = exam.classrooms || [];
  showExamDialog.value = true;
};

// 提交考试表单
const submitExam = async () => {
  try {
    const url = isEdit.value
      ? `http://${serverIP}:8080/Exam-update`
      : `http://${serverIP}:8080/Exam-add`;

    const payload = {
      id: newExam.value.id, // 修改时需要，创建时忽略
      examName: newExam.value.examName,
      lessonId,
      examStartTime: newExam.value.examStartTime,
      examEndTime: newExam.value.examEndTime,
      examDescription: newExam.value.examDescription,
      classrooms: newExam.value.classrooms.map(({ classroom_id, classroom_name }) => ({
        classroom_id,
        classroom_name,
      })),
    };

    const response = await axios.post(url, payload, {
      headers: { accessToken },
    });

    if (response.data.success) {
      ElMessage.success(isEdit.value ? "考试修改成功" : "考试创建成功");
      fetchExams(); // 刷新考试列表
      showExamDialog.value = false;
    } else {
      ElMessage.error('操作失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 删除考试
const deleteExam = async (examId) => {
  try {
    const url = `http://${serverIP}:8080/Exam-delete`;
    const response = await axios.post(
      url, null, {
      params: { examId },
      headers: { accessToken }
      }
    );

    if (response.data.success) {
      ElMessage.success('考试删除成功');
      fetchExams(); // 刷新考试列表
    } else {
      ElMessage.error('删除失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 跳转到考试详情页面
const viewExamPaper = (examId) => {
  router.push({ name: 'TeacherPaper', params: { examId } });
};

// 跳转到批改页面
const goToManualScore = (examId) => {
  router.push({ name: 'ManualScore', params: { examId } });
};

// 重置表单
const resetForm = () => {
  newExam.value = {
    id: null,
    examName: '',
    examStartTime: '',
    examEndTime: '',
    examDescription: '',
    classrooms: [],
  };
};

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

// 获取考试列表
const fetchExams = async () => {
  try {
    const url = `http://${serverIP}:8080/Exam-get`;
    const response = await axios.post(url, null, {
      params: { lessonId },
      headers: { accessToken },
    });

    if (response.data.success) {
      exams.value = response.data.data;
    } else {
      ElMessage.error('获取考试列表失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 获取教室列表
const fetchClassrooms = async () => {
  try {
    const url = `http://${serverIP}:8080/Classroom-get`;
    const response = await axios.post(url, null, {
      headers: { accessToken },
    });

    if (response.status === 200 && response.data) {
      classroomOptions.value = response.data.data;
    } else {
      ElMessage.error('获取教室数据失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

onMounted(() => {
  fetchExams();
  fetchClassrooms();
});
</script>

<style scoped>
.exams {
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

.exam-item {
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.exam-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  flex: 1;
}

.exam-title {
  font-size: 1.2rem;
  color: #409eff;
}

.exam-dates,
.exam-classrooms {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.exam-description {
  font-size: 1rem;
  color: #666;
}

.exam-actions {
  display: flex;
  align-items: center;
  margin-top: 20px;
}
</style>
