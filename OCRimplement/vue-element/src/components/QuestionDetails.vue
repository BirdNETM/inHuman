<template>
    <div class="question-details">
      <h1>题目详情</h1>
      <el-card class="question-detail-card">
        <div>
          <p><strong>题目描述:</strong> {{ question.description }}</p>
          <p><strong>类型:</strong> {{ question.type === 'c' ? '选择题' : '非选择题' }}</p>
          <p><strong>分数:</strong> {{ question.mark }}</p>
          <p><strong>建议答案:</strong> {{ question.suggestedAnswer }}</p>
          <div v-if="question.type === 'c'">
            <strong>选项:</strong>
            <ul>
              <li v-for="(choice, index) in question.choices" :key="index">{{ choice }}</li>
            </ul>
          </div>
        </div>
        <div class="actions">
          <el-button type="danger" @click="deleteQuestion">删除题目</el-button>
          <el-button type="primary" @click="showEditDialog">编辑题目</el-button>
        </div>
      </el-card>
  
      <!-- 编辑题目对话框 -->
      <el-dialog v-model="editDialogVisible" title="编辑题目">
        <el-form ref="editForm" :model="editedQuestion">
          <el-form-item label="题目描述">
            <el-input v-model="editedQuestion.description"></el-input>
          </el-form-item>
          <el-form-item v-if="editedQuestion.type === 'c'" label="选项">
            <div class="option-inputs">
              <el-input
                v-for="(choice, index) in optionInputs"
                :key="index"
                v-model="optionInputs[index]"
                placeholder="请输入选项"
                style="margin-bottom: 10px"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item label="分数">
            <el-input v-model="editedQuestion.mark" type="number"></el-input>
          </el-form-item>
          <el-form-item label="建议答案">
            <el-input v-model="editedQuestion.suggestedAnswer"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateQuestion">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute, useRouter } from 'vue-router';
  import { ElMessage, ElMessageBox } from 'element-plus';
  
  const route = useRoute();
  const router = useRouter();
  const questionId = route.params.questionId;
  const serverIP = localStorage.getItem('serverIP');
  const accessToken = localStorage.getItem('accessToken');
  
  const question = reactive({});
  const editedQuestion = reactive({});
  const optionInputs = ref([]);
  const editDialogVisible = ref(false);
  
  // 获取题目详情
  const fetchQuestionDetails = async () => {
    try {
      const url = `http://${serverIP}:8080/Exam-getQuestionById`;
      const response = await axios.post(url, null, {
        params: { questionId },
        headers: { accessToken },
      });
      if (response.data.success) {
        Object.assign(question, response.data.data);
        Object.assign(editedQuestion, response.data.data);
        optionInputs.value = question.type === 'c' ? [...question.choices] : [];
      } else {
        ElMessage.error('获取题目详情失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };
  
  // 删除题目
  const deleteQuestion = async () => {
    try {
      await ElMessageBox.confirm('确定要删除此题目吗？', '提示', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      });
  
      const url = `http://${serverIP}:8080/Exam-deleteQuestion`;
      const response = await axios.post(url, null, {
        params: { questionId },
        headers: { accessToken },
      });
  
      if (response.data.success) {
        ElMessage.success('题目删除成功！');
        router.go(-1);
      } else {
        ElMessage.error('删除题目失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.info('已取消删除');
    }
  };
  
  // 显示编辑对话框
  const showEditDialog = () => {
    editDialogVisible.value = true;
  };
  
  // 更新题目
  const updateQuestion = async () => {
    const choicesArray = editedQuestion.type === 'c'
      ? optionInputs.value.filter(choice => choice.trim() !== '')
      : [];
  
    try {
      const url = `http://${serverIP}:8080/Exam-updateQuestion`;
      const examId = route.params.examId;
      console.log(editedQuestion.description);
      const response = await axios.post(url, null, {
        params: {
          examId: examId,
          questionId: questionId,
          type: editedQuestion.type,
          description: editedQuestion.description,
          suggestedAnswer: editedQuestion.suggestedAnswer,
          mark: editedQuestion.mark,
          choices: choicesArray,
        },
        headers: { accessToken },
      });
  
      if (response.data.success) {
        ElMessage.success('题目更新成功！');
        Object.assign(question, editedQuestion); // 更新显示数据
        editDialogVisible.value = false;
      } else {
        ElMessage.error('更新题目失败: ' + response.data.message);
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + (error.response ? error.response.data : error.message));
    }
  };
  
  onMounted(() => {
    fetchQuestionDetails();
  });
  </script>
  
  <style scoped>
  .question-details {
    width: 85vw;
    max-width: 85vw;
    margin: 0 auto;
    padding: 20px;
  }
  
  .question-detail-card {
    margin-bottom: 20px;
  }
  
  .actions {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
  }
  
  .option-inputs {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  </style>
  