<template>
    <div class="posting-detail-container">
      <el-menu mode="horizontal" class="toolbar">
        <el-menu-item index="1">
          <el-button type="primary" @click="goBack" class="back-button">
            返回
          </el-button>
        </el-menu-item>
      </el-menu>
  
      <!-- 帖子详情 -->
      <div class="posting-detail-card">
        <h1 class="posting-title">{{ postingDetail.title }}</h1>
        <p class="posting-time">发布于: {{ formatDate(postingDetail.time) }}</p>
        <div class="posting-content">
          <p>{{ postingDetail.content }}</p>
        </div>
        <div class="pictures-container">
      <img
        v-for="(picture, index) in pictures"
        :key="index"
        :src="picture.url"
        alt="图片加载失败"
        @click="openImage(picture.url)"
        class="picture"
      />
      
      <!-- 放大图片的遮罩层 -->
      <div v-if="isImageOpen" class="overlay" @click="isImageOpen = false">
        <img :src="selectedImageUrl" alt="放大图片" class="enlarged-picture" />
      </div>
    </div>
        <p class="posting-sender">发布者 : {{ postingDetail.username }}</p>
      </div>

      <!-- 帖子互动区域：点赞、收藏 -->
    <div class="post-interactions">
      <el-button
        @click="handleLike"
        :class="{ liked: isLiked }"
        type="text"
        class="action-button"
      >
        <el-icon><StarFilled /></el-icon> {{ postingDetail.likes }} 点赞
      </el-button>
      <el-button
        @click="handleFavorite"
        :class="{ favorited: isFavorited }"
        type="text"
        class="action-button"
      >
        <el-icon><Star /></el-icon> {{ postingDetail.favorites }} 收藏
      </el-button>
    </div>

      <!-- 评论区域 -->
      <div class="comments-container">
        <el-row :gutter="20">
          <el-col :span="24" v-for="comment in paginatedComments" :key="comment.id">
            <el-card class="comment-card" :body-style="{ padding: '10px' }">
              <div class="comment-content">
                <h3 class="comment-author ellipsis">评论者: {{ comment.username }}</h3>
                {{ showDetailContent ? comment.content : '详细内容已隐藏，如需查看请打开显示详细内容开关。' }}
                <p class="comment-time ellipsis">评论时间: {{ formatDate(comment.time) }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
  
      <!-- 评论输入框 -->
      <div class="comment-input-section">
        <el-input
          v-model="newComment"
          type="textarea"
          placeholder="输入您的评论..."
          :rows="3"
          class="comment-input"
        ></el-input>
        <el-button type="primary" @click="submitComment" class="submit-button">提交评论</el-button>
      </div>
  
      <!-- 分页器 -->
  
      <div class="pagination-search">
  
        <el-pagination
        v-if="comments.length > pageSize"
        background
        layout="prev, pager, next"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        :total="comments.length"
        @current-change="handlePageChange"
        style="justify-content: center; align-items: center; margin-bottom: 20px;"
      />
  
    </div>
  </div>
  </template>
  <script>
  import { ref, computed, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute, useRouter } from 'vue-router';
  import router from '@/router';
  export default {
    setup() {
      const router = useRouter();
      const serverIP = localStorage.getItem('serverIP');
      const postingDetail = ref({});
      const pictures = ref([]); // 存储所有图片的数组
      const picturesCount = ref(0); // 存储所有图片的数组
      const isImageOpen = ref(false);
      const selectedImageUrl = ref("");
      
      const route = useRoute(); // 获取当前路由信息
      const postingId = route.params.id; // 获取路由参数中的 id
      const openImage = (url) => {
        selectedImageUrl.value = url;
        isImageOpen.value = true;
      };
      const comments = ref([
        { id: 1, content: "这是第一条测试评论，非常有趣！", username: "用户A", time: "2024-10-25T14:30:00.000+08:00" },
        { id: 2, content: "这篇帖子非常有帮助，感谢分享！", username: "用户B", time: "2024-10-25T15:00:00.000+08:00" },
        { id: 3, content: "我也遇到过类似的问题，已经解决了。", username: "用户C", time: "2024-10-25T16:45:00.000+08:00" },
        { id: 4, content: "这里的信息非常有价值，学习到了很多。", username: "用户D", time: "2024-10-25T17:30:00.000+08:00" },
        { id: 5, content: "非常棒的帖子，希望继续更新！", username: "用户E", time: "2024-10-25T18:15:00.000+08:00" },
        { id: 6, content: "这篇帖子非常有帮助，感谢分享！", username: "用户B", time: "2024-10-25T15:00:00.000+08:00" },
        { id: 7, content: "我也遇到过类似的问题，已经解决了。", username: "用户C", time: "2024-10-25T16:45:00.000+08:00" },
        { id: 8, content: "这里的信息非常有价值，学习到了很多。", username: "用户D", time: "2024-10-25T17:30:00.000+08:00" },
        { id: 9, content: "非常棒的帖子，希望继续更新！", username: "用户E", time: "2024-10-25T18:15:00.000+08:00" },
        { id: 10, content: "这篇帖子非常有帮助，感谢分享！", username: "用户B", time: "2024-10-25T15:00:00.000+08:00" },
        { id: 11, content: "我也遇到过类似的问题，已经解决了。", username: "用户C", time: "2024-10-25T16:45:00.000+08:00" },
        { id: 12, content: "这里的信息非常有价值，学习到了很多。", username: "用户D", time: "2024-10-25T17:30:00.000+08:00" },
        { id: 13, content: "非常棒的帖子，希望继续更新！", username: "用户E", time: "2024-10-25T18:15:00.000+08:00" },
        { id: 14, content: "这篇帖子非常有帮助，感谢分享！", username: "用户B", time: "2024-10-25T15:00:00.000+08:00" },
        { id: 15, content: "我也遇到过类似的问题，已经解决了。", username: "用户C", time: "2024-10-25T16:45:00.000+08:00" },
        { id: 16, content: "这里的信息非常有价值，学习到了很多。", username: "用户D", time: "2024-10-25T17:30:00.000+08:00" },
        { id: 17, content: "非常棒的帖子，希望继续更新！", username: "用户E", time: "2024-10-25T18:15:00.000+08:00" },
        { id: 18, content: "这篇帖子非常有帮助，感谢分享！", username: "用户B", time: "2024-10-25T15:00:00.000+08:00" },
        { id: 19, content: "我也遇到过类似的问题，已经解决了。", username: "用户C", time: "2024-10-25T16:45:00.000+08:00" },
        { id: 20, content: "这里的信息非常有价值，学习到了很多。", username: "用户D", time: "2024-10-25T17:30:00.000+08:00" },
        { id: 21, content: "非常棒的帖子，希望继续更新！", username: "用户E", time: "2024-10-25T18:15:00.000+08:00" }
      ]);
      const newComment = ref("");
      const currentPage = ref(1);
      const pageSize = ref(10);
      const showDetailContent = ref(true);
  
      // 获取帖子详情
      const fetchPostingDetail = async () => {
        //const postingId = "1"; // 测试用 postingId
        try {
          const response = await axios.post(
            `http://${serverIP}:8080/Postings-detail?postingId=${postingId}`,
            {},
            {
              headers: { accessToken: localStorage.getItem('accessToken') }
            }
          );
  
          if (response.data.success) {
            postingDetail.value = response.data.data;
            await fetchAllPictures(postingDetail.value.pictureCounts);
          } else {
            console.error("获取帖子详细信息失败，可能是身份验证问题:", response.data.message);
          }
        } catch (error) {
          console.error('获取帖子详细信息失败:', error.response || error);
        }
      };
  
      const fetchAllPictures = async (pictureCounts) => {
    if (!pictureCounts) {
      console.error("pictureCounts 未定义或为零。");
      return;
    }
  
    try {
      for (let i = 1; i <= pictureCounts; i++) {
        console.log(`开始获取图片 ${i}`);
        const response = await axios.post(
          `http://${serverIP}:8080/Postings-pictures`,
          {},
          {
            headers: {
              accessToken: localStorage.getItem("accessToken"),
            },
            params: {
              postingId: postingDetail.value.id,
              pictureId: i,
            },
            responseType: 'blob' // 设置响应类型为 blob
          }
        );
  
        if (response.status === 200) {
          const imageUrl = URL.createObjectURL(response.data); // 将 blob 转换为临时 URL
          pictures.value.push({ url: imageUrl });
        } else {
          console.error(`获取图片 ${i} 失败:`, response.data.message);
        }
      }
    } catch (error) {
      console.error("获取图片失败:", error);
    }
  };

  // const handleLike = () => {
  //     if (isLiked.value) {
  //       postingDetail.value.likes -= 1;
  //     } else {
  //       postingDetail.value.likes += 1;
  //     }
  //     isLiked.value = !isLiked.value;
  //   };

  //   const handleFavorite = () => {
  //     if (isFavorited.value) {
  //       postingDetail.value.favorites -= 1;
  //     } else {
  //       postingDetail.value.favorites += 1;
  //     }
  //     isFavorited.value = !isFavorited.value;
  //   };
      // 获取评论列表
      const fetchComments = async () => {
        //const postingId = 1;
        try {
          const response = await axios.post(
            `http://${serverIP}:8080/Comments-get`,
            {},
            {
              headers: { accessToken: localStorage.getItem('accessToken') },
              params: { postingId: parseInt(postingId) }
            }
          );
          if (response.data.success) {
            comments.value = response.data.data;
          }
        } catch (error) {
          console.error('获取评论失败:', error.response || error);
        }
      };

      //点赞收藏
      const handleLike = () => {
        postingDetail.value.likes += 1;
        // 这里可以加入请求逻辑，将点赞数据提交到服务器
      };

      const handleFavorite = () => {
        postingDetail.value.favorites += 1;
        // 这里可以加入请求逻辑，将收藏数据提交到服务器
      };

      // 提交新评论
      const submitComment = async () => {
        //const postingId = 1;
        if (!newComment.value) {
          console.error("评论内容不能为空");
          return;
        }
        try {
          const response = await axios.post(
            `http://${serverIP}:8080/Comments-set`,
            { postingId: postingId, content: newComment.value },
            { headers: { accessToken: localStorage.getItem("accessToken") } }
          );
          if (response.data.success) {
            comments.value.push({ content: newComment.value, username: "当前用户", time: new Date() });
            newComment.value = ""; // 清空输入框
          } else {
            console.error("评论提交失败:", response.data.message);
          }
        } catch (error) {
          console.error("评论提交失败:", error.response || error);
        }
      };
  
      // 分页评论
      const paginatedComments = computed(() => {
        const start = (currentPage.value - 1) * pageSize.value;
        const end = start + pageSize.value;
        return comments.value.slice(start, end);
      });
      const handlePageChange = (page) => {
    currentPage.value = page;
  };
      const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return date.toLocaleString();
      };
  
      const goBack = () => {
      router.push({ name: 'DiscussionList', params: { id: String(postingDetail.value.lessonId) } });
    };
  
      onMounted(() => {
        fetchPostingDetail();
        fetchComments();
      });
  
      return {
        postingDetail,
        comments,
        paginatedComments,
        currentPage,
        pageSize,
        formatDate,
        goBack,
        newComment,
        submitComment,
        showDetailContent,
        handlePageChange,
        postingDetail,
        pictures,
        picturesCount,
        isImageOpen,
        selectedImageUrl,
        openImage,
        postingId
      };
    }
  };
  </script>
  
  <style scoped>
  .liked {
    color: #ffdd57; /* 点赞后的颜色 */
  }

  .favorited {
    color: #ff7b72; /* 收藏后的颜色 */
  }
  .toolbar {
    margin-bottom: 15px;
    width: 100%;
    background-color: #f5f5f5;
    padding: 10px;
    border-radius: 5px;
    display: flex;
    justify-content: flex-start;
  }
  
  .back-button {
    background-color: #666 !important;
    color: white !important;
    border: none !important;
  }
  
  .posting-detail-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    gap: 20px;
    height: 100vh;
    width: 85vw;
    overflow-y: auto;
  }
  
  .posting-detail-card {
    position: relative;  /* 使用绝对定位 */
    width: 100%;
    max-width: 1200px;
    padding: 20px;
    background-color: #e4edc9;
  }
  
  .comments-container {
    width: 100%;
    max-width: 1200px;
    margin-bottom: 20px;
  }
  
  .comment-card {
    transition: transform 0.3s ease;
    margin-bottom: 10px;
    background-color: #f0f5e9;
  }
  
  .comment-card:hover {
    transform: scale(1.02);
  }
  
  .comment-content {
    display: flex;
    flex-direction: column;
  }
  
  .comment-author,
  .comment-time {
    font-size: 12px;
    color: gray;
  }
  
  .ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .comment-input-section {
    position: fixed;
    bottom: 0;
    left: 30%;
    width: 100%;
    max-width: 800px;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1); /* 阴影效果 */
    z-index: 1000; /* 确保悬浮层显示在最上面 */
  }
  
  .comment-input {
    flex: 1;
  }
  
  .submit-button {
    min-width: 80px;
  }
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
  .pictures-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr); /* 每行三列 */
    gap: 10px; /* 图片之间的间距 */
    width: 100%;
  }
  
  .picture {
    width: 300px; /* 图片占满父容器 */
    height: 300px; /* 图片占满父容器 */
    object-fit: cover; /* 图片按比例填充容器 */
    cursor: pointer; /* 鼠标悬停时显示点击手势 */
  }

.post-interactions {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;
  gap: 4px; /* 每个元素之间的间隔 */
  margin: 20px 0;
}

.action-button, .view-count {
  display: flex;
  align-items: center;
  gap: 4px; /* 图标与文字之间的间隔 */
  font-size: 1em;
  color: #333;
  text-decoration: none;
}

  /* 点击放大图片的遮罩层 */
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.8); /* 半透明黑色背景 */
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000; /* 显示在顶部 */
  }
  
  .enlarged-picture {
    max-width: 90%;
    max-height: 90%;
    border-radius: 5px;
  }
  
  </style>