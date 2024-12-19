<template>
    <div class="review-container">
      <!-- 返回按钮 -->
      <div class="header">
        <el-button type="primary" @click="goBack" class="back-button">
          返回
        </el-button>
      </div>
      <!-- 批改进度 -->
      <el-input    v-if="firstState !== 'Y'"   class="score-input" v-model="score" placeholder="请输入分数"></el-input>
        <h3 v-if="firstState === 'Y'">您已提交评分</h3>
      <!-- 批改列表 -->
      <div class="answers-container">
    

      </div>
  
      <!-- 提交按钮 -->
      <div class="submit-section">
        <el-button v-if="firstState !== 'Y'" type="primary" @click="submitScores">提交评分</el-button>
      </div>
    </div>
           <div>
    <h2>学生作业图片</h2>
    <img v-if="studentScores" :src="studentScores" alt="学生作业图片" />
    <p v-else>未找到学生作业图片</p>
  </div>

  </template>
<script>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useRouter,useRoute } from "vue-router";

export default {
  setup() {
const firstState = computed(() => {
  return mutual.value.length > 0 ? mutual.value[0].state : null;
});

    const router = useRouter();
    const route = useRoute();
const studentScores = ref();
    const serverIP = localStorage.getItem("serverIP");
    const homeworkId = route.params.homeworkId;
    const mutual = ref([]);
    const score = ref();
    const revieweeId = ref();
    const questionDetail = ref({});
    const answers = ref([]);
    const completedCount = computed(() => answers.value.filter((a) => a.score !== null).length);
    const totalAnswers = computed(() => answers.value.length);

    // 配置 Axios 拦截器来添加令牌
    axios.interceptors.request.use(
      config => {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');

        if (accessToken) {
          config.headers.accessToken = accessToken;
          config.headers.refreshToken = refreshToken;
          console.log('请求头已设置:', config.headers.accessToken);
        }
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );

    // 刷新令牌的函数
    const refreshAuthToken = async () => {
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        console.log('刷新令牌:', refreshToken);

        const response = await axios.post(`http://${serverIP}:8080/refresh-token`, {
          refreshToken: refreshToken
        });

        if (response.data.success) {
          const newAccessToken = response.data.data.accessToken;
          const newRefreshToken = response.data.data.refreshToken;

          // 将新令牌存储到 localStorage 中
          localStorage.setItem('accessToken', newAccessToken);
          localStorage.setItem('refreshToken', newRefreshToken);

          console.log('新令牌已获取并存储:', newAccessToken, newRefreshToken);

          // 刷新成功后重新获取课程数据
          await fetchCourses();
        } else {
          // 刷新失败则跳转到登录页面
          router.push({ name: 'Login' });
        }
      } catch (error) {
        console.error('刷新令牌请求失败:', error);
        router.push({ name: 'Login' });
      }
    };


    // 获取任务详情
    const fetchReviewData = async () => {
  try {
    const response = await axios.post(
      `http://${serverIP}:8080/getSpecificMutual`,
      null, // POST 请求体为空时使用 null
      {
        params: {
          homeWorkId: homeworkId, // Query 参数
        },
        headers: {
          accessToken: localStorage.getItem("accessToken"), // Header 参数
        },
      }
    );

    if (response.data.success) {
      // 成功处理响应数据
      mutual.value = response.data.data;
      revieweeId.value = mutual.value[0].revieweeId;
      console.error(revieweeId.value);

    } else {
      console.error("获取互评数据失败:", response.data.message);
    }
  } catch (error) {
    console.error("获取互评数据失败:", error.response?.data || error.message);
  }
};

    // 分配任务（示例功能）
    const assignTasks = async () => {
      try {
        const response = await axios.post(`http://${serverIP}:8080/api/assignTasks`, {
          homeWorkId: 1, // Replace with actual ID
          deadLine: "2024-12-10 23:59:59",
        }, {
          headers: { accessToken: localStorage.getItem("accessToken") },
        });

        if (response.data.success) {
          console.log("任务分配成功");

        } else {
          console.error("任务分配失败:", response.data.message);
        }
      } catch (error) {
        console.error("任务分配失败:", error);
      }
    };

    const fetchStudentHomework = async () => {
  try {
    // 定义请求的 URL
    const url = `http://${serverIP}:8080/getHomeWork`;

    // 发起 POST 请求
    const response = await axios.post(url, null, {
      params: {
        homeWorkId: homeworkId, // 作业 ID
        studentId: revieweeId.value, // 学生 ID
      },
      headers: {
        accessToken: localStorage.getItem("accessToken"), // 认证 Token
      },
      responseType: "blob", // 指定响应为二进制数据
    });

    // 处理响应数据
    if (response.status === 200) {
      const blob = new Blob([response.data], { type: "image/jpeg" }); // 创建 Blob 对象
      studentScores.value = URL.createObjectURL(blob); // 将 Blob 转为 URL
      console.log("学生作业图片数据获取成功:", studentScores.value);
    } else {
      console.error("获取学生作业失败: 响应状态码", response.status);
    }
  } catch (error) {
    console.error("获取学生作业失败:", error);
    console.error(
      "请求失败: " + (error.response ? error.response.data : error.message)
    );
  }
};






const submitScores = async () => {
  try {
    // 构造请求参数

    const response = await axios.post(
      `http://${serverIP}:8080/ReceiveTasks`, // 请求地址
      null, // POST 请求体为空时传递 `null`
      {
        params: {
          homeWorkId: homeworkId, // 作业 ID
          revieweeId: revieweeId.value, // 被评阅者 ID
          score: score.value, // 评分
        },
        headers: {
          accessToken: localStorage.getItem("accessToken"), // 请求头参数
        },
      }
    );


    // 处理响应
    if (response.data.success) {
      alert("评分提交成功！");
      router.go(-1); // 返回上一级页面

    } else {

      console.error("提交评分失败:", response.data.message);
    }
  } catch (error) {
    console.error("111",score.value);
    console.error("111",revieweeId.value);
    console.error("111",homeworkId);

    console.error("提交评分失败:", error.response?.data || error.message);
  }
};


    const goBack = () => {
        router.go(-1); // 返回上一级页面
    };

    onMounted(async () => {
      await fetchReviewData(); // 确保互评数据加载完成
      await fetchStudentHomework(); // 再请求学生作业数据
    });

    return {
      questionDetail,
      answers,
      
      completedCount,
      totalAnswers,
      submitScores,
      goBack,
      assignTasks,
      homeworkId,
      mutual,
      score,
      firstState,
      fetchStudentHomework,
      revieweeId,
      studentScores
    };
  },
};
</script>
<style scoped>
.review-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  min-height: 100vh;
  width: 15%; /* 使用百分比宽度，占满父容器 */
}

.header {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.back-button {
  background-color: #409eff !important;
  color: white !important;
  border: none !important;
}

.progress-container {
  width: 80%;
  margin-bottom: 30px;
}

.progress-container p {
  text-align: center;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.answers-container {
  width: 100%;
}

.answer-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 80%;
  background-color: #fff;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.answer-image img {
  max-width: 100px;
  max-height: 100px;
  object-fit: cover;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.score-input {
    margin-top: 100px;
    text-align: center;
}

.submit-section {
  margin-top: 20px;
  text-align: center;
}

.submit-section .el-button {
  padding: 10px 30px;
  font-size: 16px;
}
</style>
  