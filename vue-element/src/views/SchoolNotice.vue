<template>
    <div class="my-notices-container">
      <el-menu mode="horizontal" class="toolbar">
      <el-menu-item index="1">
        <!-- 只查看已读的切换开关 -->
        <el-switch v-model="showOnlyRead" active-text="只查看未读" inactive-text="查看全部"></el-switch>
      </el-menu-item>



      <el-menu-item index="3">
          <!-- 显示详细内容的切换开关 -->
          <el-switch v-model="showDetailContent" active-text="显示详细内容" inactive-text="隐藏详细内容"></el-switch>
      </el-menu-item>

      <el-menu-item index="2">
      <!-- 刷新按钮，包含 RefreshRight 图标 -->
      <el-button type="primary" @click="refreshNotices" class="refresh-button">
        <el-icon>
            <el-icon-refresh-right />
          </el-icon>
        </el-button>
      </el-menu-item>

    </el-menu>


    <el-row :gutter="20">
        <!-- 仅显示当前页的通知，每行显示一个 -->
        <el-col :span="24" v-for="notice in paginatednotices" :key="notice.id">
          <el-card
            class="notice-card"
            :class="{ 'unread-card': notice.state === 0 }"
            :body-style="{ padding: '5px' }"
            @click="goTonoticeDetail(notice.id)"
          >
          <!-- 小红点标识未读通知 -->
          <div v-if="notice.state === 0" class="red-dot"></div>

          <!-- Flex 布局分配宽度 -->
          <div class="notice-content">
            <h3 class="notice-title ellipsis">{{ notice.title }}</h3>
            {{ showDetailContent ? notice.content : '详细内容已隐藏，如需查看请打开显示详细内容开关。' }}
            <p class="notice-sender ellipsis">发送者: {{ notice.sender }}</p>
          </div>
        </el-card>
        </el-col>
      </el-row>

  
      <div class="pagination-search">
        <!-- 分页器 -->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalnotices"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
          style="justify-content: center; align-items: center; margin-bottom: 20px;"
        />
  
        <!-- 通知搜索框 -->
        <el-autocomplete
          v-model="searchQuery"
          :fetch-suggestions="querySearch"
          placeholder="搜索通知"
          @select="handlenoticeSelect"
          class="notice-search"
        />
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted, computed } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  import * as ElementPlusIconsVue from '@element-plus/icons-vue';

  export default {
    components: {
    ElIconRefreshRight // 注册图标组件
  },

    setup() {
      const router = useRouter();
      const serverIP = localStorage.getItem('serverIP');
      const showOnlyRead = ref(false);
      const showDetailContent = ref(false);

      // 创建响应式变量
      const notices = ref([]);
      const currentPage = ref(1);     // 当前页码
      const pageSize = ref(10);       // 每页显示条目数
      const searchQuery = ref('');    // 搜索框的输入内容
  
      // 获取通知总数（计算属性）
      const totalnotices = computed(() => filteredNotices.value.length);
  
      // 计算当前页的通知数据
      const paginatednotices = computed(() => {
        const start = (currentPage.value - 1) * pageSize.value;
        const end = start + pageSize.value;
        return filteredNotices.value.slice(start, end); // 使用筛选后的通知进行分页
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
  
            // 刷新成功后重新获取通知数据
            await fetchnotices();
          } else {
            // 刷新失败则跳转到登录页面
            router.push({ name: 'Login' });
          }
        } catch (error) {
          console.error('刷新令牌请求失败:', error);
          router.push({ name: 'Login' });
        }
      };
  
      // 获取通知数据的函数
      const fetchnotices = async () => {
        try {
          const response = await axios.post(`http://${serverIP}:8080/Notices`);
  
          // 检查是否需要刷新令牌
          if (response.data.message === 'NOT_LOGIN') {
            console.log('令牌过期，需要刷新');
            await refreshAuthToken();
          } else {
            notices.value = response.data.data; // 假设返回数据结构为 { notices: [...] }
            console.log('通知数据获取成功:', notices.value);
          }
        } catch (error) {
          console.error('获取通知数据失败:', error);
        }
      };
      
        // 刷新通知列表函数
        const refreshNotices = async () => {
          await fetchnotices();  // 假设 fetchnotices 是你用于获取通知的函数
          console.log("通知列表已刷新");
        };

      const filteredNotices = computed(() => {
      if (showOnlyRead.value) {
        return notices.value.filter(notice => notice.state === 0); // 只显示已读通知
      } else {
        return notices.value; // 显示所有通知
      }
    });



      // 在组件挂载时获取通知数据
      onMounted(() => {
        fetchnotices();
      });
  
      // 跳转到通知详情页
      const goTonoticeDetail = (id) => {
        router.push({ name: 'NoticeDetail', params: { id: String(id) } });
      };
  
      // 联想搜索通知
      const querySearch = (queryString, cb) => {
        currentPage.value = 1;  // 重置页码
        if (!queryString) {
          cb([]);
          return;
        }
        const results = notices.value
          .filter(notice => notice.title.toLowerCase().includes(queryString.toLowerCase()))
          .map(notice => {
            return {
              value: notice.title, // 显示的通知名称
              id: notice.id       // 用于跳转通知详情的 ID
            };
          });
        cb(results);
      };
  
      // 处理用户选择的通知
      const handlenoticeSelect = (item) => {
        goTonoticeDetail(item.id);
      };
  
      return {
        notices,
        totalnotices,
        currentPage,
        pageSize,
        paginatednotices, // 分页后的通知
        searchQuery,      // 搜索输入框内容
        querySearch,      // 搜索联想函数
        handlenoticeSelect, // 处理选择的通知
        showOnlyRead,
        showDetailContent,
        goTonoticeDetail,
        handlePageChange,
        refreshNotices
      };
    }
  };
  </script>
  
  <style scoped>

.toolbar {
  margin-bottom: 20px; /* 工具栏与通知内容之间的间距 */
  width: 100%;
  background-color: #f5f5f5; /* 给工具栏一个背景色使其与内容区分 */
  padding: 10px;
  border-radius: 5px; /* 添加圆角 */
}

/* 覆盖 .toolbar 内部 el-button 的样式 */
.toolbar .el-button {
  background-color: transparent !important; /* 确保按钮默认背景为透明 */
  border: none !important; /* 去除边框 */
  box-shadow: none !important; /* 去除按钮阴影 */
  color: inherit !important; /* 保持按钮文本或图标颜色与父组件一致 */
}

/* 在按钮悬停时保持样式不变 */
.toolbar .el-button:hover {
  background-color: transparent !important;
  color: inherit !important; /* 保持按钮文本或图标颜色一致 */
}

/* 覆盖 el-menu-item 的默认样式 */
.toolbar .el-menu-item {
  background-color: transparent !important; /* 默认背景透明 */
  color: inherit !important; /* 保持文本颜色与父组件一致 */
  border-bottom: none !important; /* 去除边框 */
  box-shadow: none !important; /* 确保没有阴影 */
}

/* 覆盖 el-menu-item 在悬停时的样式 */
.toolbar .el-menu-item:hover {
  background-color: transparent !important; /* 悬停时保持背景色透明 */
  color: inherit !important; /* 保持文本颜色一致 */
  cursor: pointer; /* 悬停时显示指针 */
}

/* 去除 el-menu-item 激活状态的下划线和阴影 */
.toolbar .el-menu-item.is-active {
  border-bottom: none !important; /* 去除激活状态的下划线 */
  box-shadow: none !important; /* 确保没有激活状态的阴影 */
}



.my-notices-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  padding: 20px;
  height: 100vh; /* 填充整个高度 */
  width: 85vw;   /* 填充宽度的85% */
  overflow-y: auto; /* 内容过多时滚动 */
  justify-content: flex-start;  /* 从顶部开始对齐 */
  gap: 20px; /* 设置子组件之间的间距 */
  position: relative; /* 确保 pagination-search 可以定位 */
}

.notice-row {
  display: flex;
  justify-content: flex-start;  /* 从顶部和左侧开始布局 */
  flex-wrap: wrap; /* 宽度不足时自动换行 */
  gap: 15px; /* 卡片之间的水平和垂直间距 */
}

.red-dot {
  position: absolute;
  top: 3px; /* 控制红点的位置，距离卡片顶部 10px */
  left: 3px; /* 控制红点的位置，距离卡片左侧 10px */
  width: 20px;
  height: 20px;
  background-color: red;
  border-radius: 50%; /* 圆形 */
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.2);
  z-index: 10; /* 确保红点位于卡片上方 */
}

.notice-card {
  position: relative; /* 确保红点的绝对定位相对于卡片 */
  width: 80vw;
  height: auto; /* 使卡片高度根据内容自动调整 */
  cursor: pointer;
  transition: transform 0.3s ease;
  margin-bottom: 7px; /* 卡片之间的固定间距 */
  background-color: rgb(228, 237, 201);
}

.notice-card:hover {
  transform: scale(1.02); /* 悬停时卡片略微放大 */
}

/* Flex 布局，控制各部分的宽度比例 */
.notice-content {
  display: flex; /* 修正 display 设置为 flex，因为你需要 flex 布局 */
  margin-left: 25px; /* 使用 margin-left 使左侧空出一些空间 */
  flex-direction: column;
  width: 100%;
}


/* 设置标题、内容和发送者的宽度分配 */
.notice-title {
  flex: 1; /* 占较少宽度 */
  font-size: 16px;
  font-weight: bold;
}

.notice-text {
  flex: 2; /* content 占较大的宽度 */
  font-size: 14px;
}

.notice-sender {
  flex: 1; /* 占较少宽度 */
  font-size: 12px;
  color: gray;
}

/* 处理省略号 */
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%; /* 确保省略号生效 */
}

/* 搜索框和分页器布局 */
.pagination-search {
  position: relative;  /* 使用绝对定位 */
  bottom: 20px; /* 距离底部的距离 */
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  background: white; /* 如果需要背景以便更好分离内容和分页 */
}

.refresh-button {
  display: flex;
  align-items: center;
  gap: 5px; /* 图标和文字之间的间距 */
  background: grey; /* 如果需要背景以便更好分离内容和分页 */
}

.notice-search {
  width: 300px;
}
</style>