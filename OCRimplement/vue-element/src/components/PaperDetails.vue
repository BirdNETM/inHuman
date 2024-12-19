<template>
  <div class="paper">
    <h1>学生端 - 考试详情</h1>

    <el-row :gutter="20">
      <el-col
        v-for="question in questions"
        :key="question.id"
        :span="24"
      >
        <el-card class="question-item" shadow="hover">
          <div class="question-content">
            <!-- 题目描述 -->
            <p class="question-description">{{ question.description }}</p>
            <!-- 题目信息 -->
            <div class="question-info">
              <el-tag type="info">类型: {{ question.type == "c" ? "选择题" : "非选择题" }}</el-tag>
              <el-tag type="warning">分数: {{ question.mark }}</el-tag>
            </div>
            <!-- 选项内容 -->
            <div v-if="question.type === 'c'">
              <el-radio-group v-model="selectedAnswers[question.id]">
                <el-radio
                  v-for="(choice, index) in question.choices"
                  :key="index"
                  :label="choice"
                >
                  {{ choice }}
                </el-radio>
              </el-radio-group>
            </div>
            <!-- 填空内容 -->
            <div v-else-if="question.type === 'n'">
              <el-input
                v-model="selectedAnswers[question.id]"
                placeholder="请输入答案"
                style="width: 100%;"
              ></el-input>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-button type="primary" @click="submitAnswers" style="margin-top: 20px;">
      提交答案
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';

import { ElMessage } from 'element-plus';

// 从路由参数中获取 examId
const route = useRoute();
const router = useRouter();

const examId = route.params.examId;

const serverIP = localStorage.getItem('serverIP');
const accessToken = localStorage.getItem('accessToken');

const questions = ref([]);
const selectedAnswers = ref({});

// 获取题目列表
const fetchQuestions = async () => {
  try {
    const url = `http://${serverIP}:8080/Exam-getQuestions`;
    const response = await axios.post(url, null, {
      params: { examId },
      headers: { accessToken }
    });

    if (response.data.success) {
      questions.value = response.data.data;
    } else {
      ElMessage.error('获取题目列表失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};

// 提交所有答案
const submitAnswers = async () => {
  const payload = {};
  for (const questionId in selectedAnswers.value) {
    payload[questionId] = selectedAnswers.value[questionId];
  }

  const isAllAnswered = questions.value.every(question => {
    return selectedAnswers.value[question.id];
  });

  if (!isAllAnswered) {
    ElMessage.warning('请完成所有题目的作答后提交');
    return;
  }

  try {
    const url = `http://${serverIP}:8080/Exam-ReceiveHomework`;
    const response = await axios.post(url, payload, {
      params: { examId },
      headers: { accessToken }
    });

    if (response.data.success) {
      ElMessage.success('答案提交成功！');
      router.go(-1);
    } else {
      ElMessage.error('提交答案失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};

onMounted(() => {
  fetchQuestions();
});
</script>

<style scoped>
.paper {
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

.question-item {
  margin-bottom: 15px;
}

.question-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.question-description {
  font-size: 1.2rem;
  color: #333;
}

.question-info {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.el-radio-group {
  display: flex;
  flex-direction: column; /* 垂直排列选项 */
  align-items: flex-start; /* 左对齐 */
  gap: 10px; /* 选项之间的间距 */
  width: 100%; /* 占据父容器宽度 */
}

.el-radio {
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  align-items: center; /* 垂直居中 */
  text-align: left;
  width: auto; /* 自动适应内容大小 */
}

.el-button {
  width: 100px;
  margin-top: 20px;
}
</style>
