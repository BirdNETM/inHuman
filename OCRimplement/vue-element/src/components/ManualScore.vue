<template>
    <div class="paper">
      <h1>学生答题评分</h1>
      <el-row :gutter="20">
        <el-col
          v-for="(answersList, questionId) in filteredAnswers"
          :key="questionId"
          :span="24"
        >
          <el-card
            v-for="answer in answersList"
            :key="answer.id"
            class="question-item"
            shadow="hover"
          >
            <div class="question-content">
              <!-- 题目描述 -->
              <p class="question-description">{{ getQuestionDescription(questionId) }}</p>
              <!-- 学生答案 -->
              <p>学生 ID: {{ answer.studentId }}</p>
              <p>学生答案: {{ answer.answer }}</p>
              <!-- 打分输入框 -->
              <div class="score-input">
                <el-input-number
                  v-model="scores[`${questionId}-${answer.studentId}`]"
                  :min="0"
                  :max="getQuestionMark(questionId)"
                  placeholder="请输入分数"
                ></el-input-number>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <el-button type="primary" @click="submitScores" style="margin-top: 20px;">
        提交评分
      </el-button>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  import { useRoute } from 'vue-router';
  
  const route = useRoute();
  const examId = route.params.examId; // 从路由参数中获取 examId
  const serverIP = localStorage.getItem('serverIP');
  const accessToken = localStorage.getItem('accessToken');
  
  const questions = ref([]);
  const answers = ref({});
  const scores = ref({}); // 使用 "QuestionId-StudentId" 作为键
  
  // 获取题目描述
  const getQuestionDescription = (questionId) => {
    const question = questions.value.find((q) => q.id === parseInt(questionId));
    return question ? question.description : '未知题目';
  };
  
  // 获取题目分值
  const getQuestionMark = (questionId) => {
    const question = questions.value.find((q) => q.id === parseInt(questionId));
    return question ? question.mark : 0;
  };
  
  // 过滤非选择题的答案
  const filteredAnswers = computed(() => {
    const nonChoiceQuestions = questions.value.filter((q) => q.type === 'n');
    const nonChoiceQuestionIds = nonChoiceQuestions.map((q) => q.id);
    return Object.fromEntries(
      Object.entries(answers.value).filter(([questionId]) =>
        nonChoiceQuestionIds.includes(parseInt(questionId))
      )
    );
  });
  
  // 获取题目列表
  const fetchQuestions = async () => {
    try {
      const url = `http://${serverIP}:8080/Exam-getQuestions`;
      const response = await axios.post(
        url,
        {},
        {
          params: { examId },
          headers: { accessToken },
        }
      );
  
      if (response.data.success) {
        questions.value = response.data.data;
        await fetchAnswersForQuestions();
      } else {
        ElMessage.error('获取题目列表失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + error.message);
    }
  };
  
  // 获取答案列表
  const fetchAnswersForQuestions = async () => {
    try {
      for (const question of questions.value) {
        const url = `http://${serverIP}:8080/Exam-getAnswersByQuestionId`;
        const response = await axios.post(
          url,
          {},
          {
            params: { questionId: question.id },
            headers: { accessToken },
          }
        );
  
        if (response.data.success) {
          answers.value[question.id] = response.data.data;
        } else {
          ElMessage.warning(
            `获取题目 ${question.id} 答案失败: ` + response.data.message
          );
        }
      }
    } catch (error) {
      ElMessage.error('获取答案失败: ' + error.message);
    }
  };
  
  // 提交评分
  const submitScores = async () => {
    const studentScores = {};

    // 按学生分类分数
    Object.entries(scores.value).forEach(([key, value]) => {
      const [questionId, studentId] = key.split('-');
      if (!studentScores[studentId]) {
        studentScores[studentId] = {};
      }
      studentScores[studentId][questionId] = value?.toString();
    });
  
    console.log(studentScores);
    try {
      for (const [studentId, scoresForStudent] of Object.entries(studentScores)) {
        const url = `http://${serverIP}:8080/Exam-ManualScore`;
        const response = await axios.post(
          url,
          scoresForStudent,
          {
            params: { examId, studentId }, // 添加 examId 和 studentId 作为 query 参数
            headers: { accessToken },
          }
        );
  
        if (!response.data.success) {
          ElMessage.error(`学生 ID ${studentId} 的评分提交失败: ${response.data.message}`);
        }
      }
  
      ElMessage.success('评分提交成功！');
    } catch (error) {
      ElMessage.error('请求失败: ' + error.message);
    }
  };
  
  onMounted(() => {
    if (!examId) {
      ElMessage.error('无法获取 examId，请检查路由配置');
      return;
    }
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
  
  .score-input {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .el-button {
    margin-top: 20px;
    width: 100px;
  }
  </style>
  