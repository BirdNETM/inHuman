<template>
  <div class="paper">
    <h1>教师端 - 考试题目管理</h1>
    <el-button type="primary" @click="showAddQuestionDialog" style="margin-bottom: 20px;">
      添加题目
    </el-button>

    <el-row :gutter="20">
      <el-col v-for="question in questions" :key="question.id" :span="24">
        <el-card class="question-item hover-zoom" @click="goToQuestionDetails(question.id)">
          <div class="question-content">
            <p class="question-description">{{ question.description }}</p>
            <div class="question-info">
              <el-tag type="info">类型: {{ question.type == "c" ? "选择题" : "非选择题" }}</el-tag>
              <el-tag type="warning">分数: {{ question.mark }}</el-tag>
              <el-tag type="success">建议答案: {{ question.suggestedAnswer }}</el-tag>
            </div>
            <div class="question-choices" v-if="question.type === 'c'">
              <h4>选项：</h4>
              <ul>
                <li v-for="(choice, index) in question.choices" :key="index">
                  {{ choice }}
                </li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加题目对话框 -->
    <el-dialog v-model="dialogVisible" title="添加新题目">
      <el-form ref="questionForm" :model="newQuestion">
        <el-form-item label="题目描述" required>
          <el-input
            v-model="newQuestion.description"
            placeholder="请输入题目描述"
            type="textarea"
            :rows="7"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-upload
            class="upload-demo"
            :show-file-list="false"
            :before-upload="handleBeforeUpload"
          >
            <el-button type="primary">上传图片并使用OCR识别题目</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="题目类型" required>
          <el-radio-group v-model="newQuestion.type">
            <el-radio label="选择题" value="c">选择题</el-radio>
            <el-radio label="非选择题" value="n">非选择题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="newQuestion.type === 'c'" label="选项">
          <div class="option-inputs">
            <el-input
              v-for="(choice, index) in optionInputs"
              :key="index"
              v-model="optionInputs[index]"
              placeholder="请输入选项"
              :style="{ marginBottom: '10px' }"
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="分数" required>
          <el-input v-model="newQuestion.mark" type="number" placeholder="请输入分数"></el-input>
        </el-form-item>
        <el-form-item label="建议答案" required>
          <el-input v-model="newQuestion.suggestedAnswer" placeholder="请输入建议答案"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addQuestion">保存题目</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

// 路由实例
const router = useRouter();
const route = useRoute();

const examId = route.params.examId;
const serverIP = localStorage.getItem('serverIP');
const accessToken = localStorage.getItem('accessToken');

const questions = ref([]);
const dialogVisible = ref(false);
const newQuestion = ref({
  description: '',
  type: 'c', // 默认类型为选择题
  mark: null,
  suggestedAnswer: '',
  choices: []
});
const optionInputs = ref(['', '', '', '']); // 用于存储选项输入框的内容
const ocrDescription = ref(""); // OCR 识别返回的题目描述

// 显示添加题目对话框
const showAddQuestionDialog = () => {
  dialogVisible.value = true;
  newQuestion.value = { description: '', type: 'c', mark: null, suggestedAnswer: '', choices: [] };
  optionInputs.value = ['', '', '', ''];
  ocrDescription.value = "";
};

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


// 上传图片并调用后端 OCR 接口
const handleBeforeUpload = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await axios.post(`http://${serverIP}:8080/recognize`, formData, {
      headers: {
        accessToken,
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    console.log(response)
    if (response.data) {
      ocrDescription.value = response.data.replace(/[^\S\r\n]/g, ''); // 接收 OCR 识别结果
      
      newQuestion.value.description = ocrDescription.value; // 自动填充到题目描述
      ElMessage.success("OCR 识别成功，已填充题目描述");
    } else {
      ElMessage.error("OCR 识别失败: " + response.data.message);
    }
  } catch (error) {
    ElMessage.error("上传失败: " + error.message);
  }
  return false; // 阻止默认上传行为
};

// 添加题目
const addQuestion = async () => {
  const choicesArray = newQuestion.value.type === 'c'
    ? optionInputs.value.filter(choice => choice.trim() !== '')
    : [];

  if (
    !newQuestion.value.description ||
    !newQuestion.value.type ||
    !newQuestion.value.mark ||
    !newQuestion.value.suggestedAnswer ||
    (newQuestion.value.type === 'c' && choicesArray.length === 0)
  ) {
    ElMessage.warning('请完整填写所有必填项');
    return;
  }

  const params = new URLSearchParams();
  params.append('examId', examId);
  params.append('type', newQuestion.value.type);
  params.append('description', newQuestion.value.description);
  params.append('suggestedAnswer', newQuestion.value.suggestedAnswer);
  params.append('mark', newQuestion.value.mark);
  choicesArray.forEach(choice => params.append('choices[]', choice));

  try {
    const url = `http://${serverIP}:8080/Exam-AssignQuestion`;
    const response = await axios.post(url, params, {
      headers: {
        accessToken,
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });

    if (response.data.success) {
      ElMessage.success('题目添加成功！');
      dialogVisible.value = false;
      fetchQuestions(); // 重新获取题目列表
    } else {
      ElMessage.error('添加题目失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
  }
};

// 跳转到 QuestionDetails 页面
const goToQuestionDetails = (questionId) => {
  router.push({
    name: 'QuestionDetails',
    params: { questionId, examId },
  });
};

// 初始化获取题目列表
onMounted(() => {
  fetchQuestions();
});

</script>

<style>
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
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.question-item:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.question-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.question-description {
  font-size: 1.2rem;
  color: #333;
  text-align: center;
}

.question-info {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.question-choices {
  margin-top: 10px;
  text-align: center;
}

.option-inputs {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

</style>
