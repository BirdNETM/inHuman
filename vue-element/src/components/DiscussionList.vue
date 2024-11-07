<template>
  <div class="discussion-list">
    <header class="discussion-header">
      <h2>课程讨论区</h2>
      <div class="toolbar">
        <select v-model="sortOrder" class="sort-select">
          <option value="desc">按时间降序</option>
          <option value="asc">按时间升序</option>
        </select>
        <button class="post-button" @click="openCreatePost">发起讨论</button>
      </div>
    </header>
    <el-dialog
      title="创建新帖子"
      v-model="dialogVisible"
      @close="resetForm"
      width="500px"
    >
    <el-form ref="form" :model="newPost" :rules="rules" label-width="60px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newPost.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model="newPost.content"
            placeholder="请输入帖子内容"
            :autosize="{ minRows: 4, maxRows: 8 }"
          ></el-input>
        </el-form-item>
        <el-form-item label="上传图片">
          <div class="upload-container">
            <el-upload
              ref="upload"
              class="upload-demo"
              action=""
              list-type="picture-card"
              :auto-upload="false"
              :on-preview="handlePictureCardPreview"
              :on-remove="handleRemove"
              :on-change="handleFileChange"
              multiple
            >
              <i class="el-icon-plus"></i>
              <template #tip>
                <div class="upload-tip">最多可上传 9 张图片，每张图片大小不超过 2MB。</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>

      
      <div slot="footer" class="dialog-footer">
        <el-button @click="resetForm">取消</el-button>
        <el-button type="primary" @click="submitPost">提交</el-button>
      </div>
    </el-dialog>
    <div class="post-container">
      <DiscussionPost v-for="post in sortedPosts" :key="post.id" :post="post" />
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import DiscussionPost from './DiscussionPost.vue';
import { ElDialog, ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElUpload } from 'element-plus';
import dayjs from 'dayjs'
export default {
  components: {
    DiscussionPost,
    ElDialog,
    ElForm,
    ElFormItem,
    ElInput,
    ElButton,
    ElUpload
  },
  data() {
    return {
      posts: [],
      sortOrder: 'desc',
      dialogVisible: false,
      newPost: {
        title: '',
        content: '',
        images:[]
      },
      rules: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
      },
      dialogImageUrl: '',
      dialogVisibleImage: false
    };
  },
  computed: {
    sortedPosts() {
      return this.posts.slice().sort((a, b) => {
        return this.sortOrder === 'desc'
          ? new Date(b.date) - new Date(a.date)
          : new Date(a.date) - new Date(b.date);
      });
    }
  },
  methods: {
    formatDate(dateString) {
      return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss');
    },
    resetForm() {
      this.newPost = { title: '', content: '', images:[] };  // 重置表单
      this.$refs.form.resetFields();  // 清除表单验证状态
      this.$refs.upload.clearFiles(); // 清空 el-upload 文件列表
      this.dialogVisible = false;  // 关闭对话框
    },
    async fetchPosts(lessonId) {
      const serverIP = localStorage.getItem('serverIP');
      const accessToken = localStorage.getItem('accessToken');
      
      try {
        const response = await axios.post(`http://${serverIP}:8080/Postings`, null, {
          params: { lessonId },
          headers: {accessToken: accessToken}
        });

        if (response.data.success) {
          // 处理返回的数据
          this.posts = response.data.data.map(post => ({
            id: post.id,
            title: post.title,
            author: post.posterId, // 假设 content 是作者名称
            date: this.formatDate(post.time), // 替换为实际的日期字段
            likes: Math.floor(Math.random() * 1000), // 替换为实际的点赞数据
            viewCount: Math.floor(Math.random() * 1000),
            favoriteCount: Math.floor(Math.random() * 1000)
          }));
        } else {
          console.error('获取帖子失败:', response.data.message);
        }
      } catch (error) {
        console.error('请求帖子时出错:', error);
      }
    },
    openCreatePost() {
      this.dialogVisible = true;  // 打开对话框
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisibleImage = true;
    },
    handleRemove(file, fileList) {
      this.newPost.images = fileList;
    },
    handleFileChange(file, fileList) {
      this.newPost.images = fileList.map(f => f.raw); // 更新图片文件列表
    },
    async submitPost() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) {
        ElMessage.error('请填写所有必填项');
        return;
      }
    
      try {
        const serverIP = localStorage.getItem('serverIP');
        const accessToken = localStorage.getItem('accessToken');
        const lessonId = this.$route.params.id;

        // Step 1: 调用 setPosting 函数，上传帖子标题和内容
        const postResponse = await axios.post(
          `http://${serverIP}:8080/Postings-create-content`,
          {
            title: this.newPost.title,
            content: this.newPost.content,
            pictureCounts: this.newPost.images.length
          },
          {
            params: { 'lessonId': lessonId },
            headers: { 'accessToken': accessToken }
          }
        );

        // 检查帖子创建是否成功
        if (!postResponse.data || !postResponse.data.data) {
          ElMessage.error('创建帖子失败');
          return;
        }

        // 获取新建帖子的 ID
        const postingId = postResponse.data.data;
        console.log(postingId);
        // Step 2: 遍历图片列表，并依次上传图片
        if (this.newPost.images && this.newPost.images.length > 0) {
          for (let i = 0; i < this.newPost.images.length; i++) {
            const pictureId = i + 1; // 从 1 开始自增
            const formData = new FormData();
            formData.append('file', this.newPost.images[i]); 
            console.log(this.newPost.images[i]);
            try {
              await axios.post(
                `http://${serverIP}:8080/Postings-insert-picture`,
                formData,
                {
                  params: { 'pictureId': pictureId, 'postingId': postingId },
                  headers: {
                    'accessToken': accessToken
                  }
                }
              );
              ElMessage.success(`图片 ${pictureId} 上传成功`);
            } catch (error) {
              console.error(`图片 ${pictureId} 上传失败`, error);
              ElMessage.error(`图片 ${pictureId} 上传失败`);
            }
          }
        }

        // 重新获取帖子数据
        this.fetchPosts(lessonId);
        // 重置表单
        this.resetForm();
        ElMessage.success('帖子创建成功');
      } catch (error) {
        console.error('提交帖子时出错:', error);
        ElMessage.error('提交帖子时出错');
      }
    });
  }

  },
  
  mounted() {
    const courseId = this.$route.params.id;
    this.fetchPosts(courseId); 
  }
};
</script>


<style scoped>
.discussion-list {
  height: 100vh;
  width: 85vw;
  background: linear-gradient(135deg, #f3f4f6, #ffffff);
  margin: 0 auto;
  padding: 20px;
}

.discussion-header {
  display: flex;
  justify-content: space-between;
  color: #007bff;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 15px; /* 增加间距 */
}

.sort-select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.post-button {
  background-color: #007bff;
  color: #ffffff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.post-button:hover {
  background-color: #0056b3;
}

.post-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); /* 网格布局 */
  gap: 16px;
  max-height: 600px; /* 设置最大高度，超过后滚动 */
  overflow-y: auto; /* 垂直滚动 */
}
.upload-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px; /* 设置上传框间距 */
}

.upload-demo::v-deep(.el-upload-list__item) {
  width: calc(33.33% - 16px); /* 每个上传框占据三分之一宽度，减去间距 */
}
</style>
