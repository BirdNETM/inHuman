<template>
  <div class="my-courses-container">
    <el-row :gutter="20">
      <!-- 仅显示当前页的课程 -->
      <el-col :span="6" v-for="course in paginatedCourses" :key="course.id">
        <el-card
          class="course-card"
          :body-style="{ padding: '20px' }"
          @click="goToCourseDetail(course.id)"
        >
          <h3>{{ course.name }}</h3>
          <p>任课老师: {{ course.teacher }}</p>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination-search">
      <!-- 分页器 -->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalCourses"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
        style="justify-content: center; align-items: center; margin-bottom: 20px;"
      />

      <!-- 课程搜索框 -->
      <el-autocomplete
        v-model="searchQuery"
        :fetch-suggestions="querySearch"
        placeholder="搜索课程"
        @select="handleCourseSelect"
        class="course-search"
      />
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();
    const serverIP = localStorage.getItem('serverIP');

    // 创建响应式变量
    const courses = ref([]);
    const currentPage = ref(1);     // 当前页码
    const pageSize = ref(12);       // 每页显示条目数
    const searchQuery = ref('');    // 搜索框的输入内容

    // 获取总课程数（计算属性）
    const totalCourses = computed(() => courses.value.length);

    // 计算当前页的课程数据
    const paginatedCourses = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return courses.value.slice(start, end); // 获取当前页要显示的课程
    });

    // 处理分页器页码变化的函数
    const handlePageChange = (newPage) => {
      currentPage.value = newPage; // 更新当前页码，分页显示自动更新
      console.log("jump to page " + newPage);
    };

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

    // 获取课程数据的函数
    const fetchCourses = async () => {
      try {
        const response = await axios.post(`http://${serverIP}:8080/lessons`);

        // 检查是否需要刷新令牌
        if (response.data.message === 'NOT_LOGIN') {
          console.log('令牌过期，需要刷新');
          await refreshAuthToken();
        } else {
          courses.value = response.data.data; // 假设返回数据结构为 { courses: [...] }
          console.log('课程数据获取成功:', courses.value);
        }
      } catch (error) {
        console.error('获取课程数据失败:', error);
      }
    };

    // 在组件挂载时获取课程数据
    onMounted(() => {
      fetchCourses();
    });

    // 跳转到课程详情页
    const goToCourseDetail = (id) => {
      router.push({ name: 'CourseDetail', params: { id: String(id) } });
    };

    // 联想搜索课程
    const querySearch = (queryString, cb) => {
      const results = courses.value
        .filter(course => course.name.toLowerCase().includes(queryString.toLowerCase()))
        .map(course => {
          return {
            value: course.name, // 显示的课程名称
            id: course.id       // 用于跳转课程详情的 ID
          };
        });
      cb(results);
    };


    // 处理用户选择的课程
    const handleCourseSelect = (item) => {
      goToCourseDetail(item.id);
    };

    return {
      courses,
      totalCourses,
      currentPage,
      pageSize,
      paginatedCourses, // 分页后的课程
      searchQuery,      // 搜索输入框内容
      querySearch,      // 搜索联想函数
      handleCourseSelect, // 处理选择的课程
      goToCourseDetail,
      handlePageChange
    };
  }
};
</script>


<style scoped>
.my-courses-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  padding: 20px;
  height: 100vh; /* 确保填充整个高度 */
  width: 85vw;
  overflow-y: auto; /* 如果内容过多，可以滚动 */
  justify-content: space-between; 
}

.course-card {
  width: 19vw;
  height: 20vh;
  cursor: pointer;
  transition: transform 0.3s ease;
  margin-bottom: 20px;         /* 下方的固定间距 */
  background-color: rgb(228, 237, 201);
}

.course-card:hover {
  transform: scale(1.1);
}

/* 新增样式，用于搜索框和分页器的布局 */
.pagination-search {
  justify-content: space-between;
  align-items: center;
}

.course-search {
  width: 30px;
}
</style>
