<template>
    <div class="personal-info">
      <!-- 用户信息部分 -->
      <div class="user-info-section">
        <div class="left-info">
          <img :src="avatarUrl" alt="用户头像" class="avatar" />
          <div class="user-details">
            <h2>{{ nickname }}</h2>
            <p>姓名：{{ fullName }}</p>
            <p>学号: {{ studentId }}</p>
            <p>性别: {{ gender }}</p>
            <p class="signature">“{{ signature }}”</p>
          </div>
        </div>
  
        <!-- 右边的统计信息 -->
        
        <div class="right-info">
          
          <div class="stats">
            
            <p>发布的帖子数: <span>{{ postCount }}</span></p>
            <p>评论数: <span>{{ commentCount }}</span></p>
            <!-- 编辑个人信息按钮 -->
            <el-button type="primary" @click="editProfileVisible = true">编辑个人信息</el-button>
          </div>
        </div>
      </div>
  
      
  
      <!-- 编辑个人信息对话框 -->
      <el-dialog title="编辑个人信息" v-model="editProfileVisible" width="30%">
        <el-form label-position="left" label-width="80px" class="edit-profile-form">
            <!-- 头像上传控件，居中显示 -->
            <div class="avatar-upload-container">
            <!-- Avatar preview or placeholder -->
              <div class="avatar-preview">
                <img v-if="newAvatarUrl" :src="newAvatarUrl" alt="头像预览" class="avatar-image" />
                <div v-else class="avatar-placeholder">上传头像</div>
              </div>
  
              <!-- File input element (hidden) -->
              <input type="file" ref="fileInput" @change="handleFileChange" class="file-input" accept="image/*" />
  
            </div>
  
          <!-- 昵称输入框 -->
          <el-form-item label="昵称">
            <el-input v-model="newNickname" placeholder="请输入新的昵称"></el-input>
          </el-form-item>
  
          <!-- 个性签名输入框 -->
          <el-form-item label="个性签名">
            <el-input v-model="newSignature" placeholder="修改个性签名"></el-input>
          </el-form-item>
        </el-form>
  
        <!-- 设置公开或私密 -->
        <el-form-item label="设置个人主页公开或私密">
          <Unlock v-if="!privacy" class="privacy-icon icon-size" @click.stop="privacy = !privacy" title="点击切换公开/私密" />
          <Lock v-else class="privacy-icon icon-size" @click.stop="privacy = !privacy" title="点击切换公开/私密" />
        </el-form-item>
        
        <template #footer>
          <el-button @click="editProfileVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </template>
      </el-dialog>
  
      <!-- 新增收藏夹 -->
      <div class="add-category">
        <input v-model="newCategoryName" placeholder="新增收藏夹名称" />
        <button @click="addCategory">添加</button>
      </div>
  
      <!-- 收藏夹分类导航 -->
      <div class="tab-section">
        <ul class="tabs">
          <li
            v-for="(category, index) in favoriteCategories"
            :key="index"
            :class="{ active: selectedTab === category.name }"
            @click="selectTab(category.name)"
          >
            {{ category.name }}
  
            
  
            <!-- 编辑和删除图标 -->
            <el-icon @click.stop="openEditPrompt(category, index)" class="edit-icon icon-size" title="编辑">
              <Edit />
            </el-icon>
            <el-icon @click.stop="confirmDelete(index)" class="delete-icon icon-size" title="删除">
              <Delete />
            </el-icon>
          </li>
        </ul>
      </div>
  
       <!-- 展示选中的收藏夹内的帖子 -->
       <div class="posts-list">
        <div v-for="post in selectedPosts" :key="post.id" class="post-item">
          <h4>{{ post.title }}</h4>
          <p class="post-excerpt">{{ post.content }}</p>
          <p class="post-time">{{ post.time }}</p>
          <a @click.prevent="viewPost(post.id)" class="read-more">查看详情</a>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
  import { ElMessageBox, ElMessage, ElUpload } from 'element-plus';
  import axios from 'axios'
  
  export default {
    components:{
      ElUpload
    },
    data() {
      return {
        avatarUrl: '',
        newAvatarUrl: '', // 上传的新头像
        fullName: '',
        nickname: '1',
        newNickname: '', // 修改后的昵称
        studentId: '22301113',
        gender: '男',
        signature: '我只想养女儿',
        newSignature: '', // 修改后的个性签名
        privacy:'',//设置个人中心私密
        postCount: 52,
        commentCount: 128,
        selectedTab: '',
        newCategoryName: '',
        editingCategoryIndex: null,
        editProfileVisible: false, // 控制个人信息编辑对话框显示
        favoriteCategories: [],
      };
    },
    computed: {
      selectedPosts() {
        const selectedCategory = this.favoriteCategories.find(category => category.name === this.selectedTab);
        return selectedCategory ? selectedCategory.posts : [];
      },
    },
  
    mounted(){
      this.fetchUserData();
      this.fetchFavoriteCategories();
    },
    methods: {
      
      //获取后端数据
      async fetchUserData(){
        try{
          const serverIP = localStorage.getItem('serverIP');
          const accessToken = localStorage.getItem('accessToken');
          const response = await axios.post(`http://${serverIP}:8080/HomePage`, null, {
            headers:{accessToken:accessToken}
           });
          const data = response.data.data;
          this.avatarUrl = await this.getAvatarImage();
          this.fullName = data.userName;
          this.nickname = data.userName; // 假设后端返回的 nickname 是 userName
          this.studentId = data.studentCode;
          this.gender = data.gender;
          this.signature = data.bio; // 个性签名是 bio 字段
          this.privacy = data.privacy;
          this.newNickname = this.nickname;
          this.newSignature = this.signature;
        }catch (error) {
          console.error('获取用户数据失败', error);
        }
      },
      //获取用户头像
      async getAvatarImage() {
        try {
          const serverIP = localStorage.getItem('serverIP');
          const accessToken = localStorage.getItem('accessToken');
          // 向后端发送请求，设置 headers 和 responseType 在同一个配置对象中
          const response = await axios.get(`http://${serverIP}:8080/Images`, {
          headers: {
            'accessToken': accessToken
          },
          responseType: 'blob' // 指定返回的数据类型为 blob
          });
  
          // 使用 URL.createObjectURL 将 Blob 数据转化为可展示的 URL
          return URL.createObjectURL(response.data);
          } catch (error) {
          console.error('获取图片失败', error);
          return '';
          }
      },
    //收藏夹信息获取  
    async fetchFavoriteCategories() {
      try {
        const serverIP = localStorage.getItem('serverIP');
        const accessToken = localStorage.getItem('accessToken');
  
        // 使用 axios 请求后端获取收藏的帖子数据
        const response = await axios.post(`http://${serverIP}:8080/Favorites-Order-By-Type`,null, {
          headers: {
            'accessToken': accessToken
          }
        });
  
        if (response && response.data && Array.isArray(response.data.data)) {
        const favoriteData = response.data.data;
  
        // 创建一个对象用于存储不同 type 的收藏夹
        const categoriesMap = {};
  
        // 遍历每一个收藏夹，将帖子合并到同一个 type 下
        favoriteData.forEach(item => {
          const type = item.type;
  
          // 打印每个收藏夹的 postingsList 以调试
          console.log(`当前收藏夹类型: ${type}, postingsList:`, item.postingsList);
  
          // 如果 postingsList 不是数组，设置为一个空数组
          const postingsList = Array.isArray(item.postingsList) ? item.postingsList : [];
          // 确保 postingsList 存在且为数组
          if (Array.isArray(item.postingsList)) {
            // 如果当前 type 的收藏夹已经存在，则合并帖子
            if (categoriesMap[type]) {
              categoriesMap[type].posts.push(...item.postingsList);
            } else {
              // 否则创建新的收藏夹并加入到 categoriesMap 中
              categoriesMap[type] = {
                name: type,
                posts: [...item.postingsList]
              };
            }
          } else {
            console.warn(`postingsList 不是数组或不存在 for type: ${type}`);
          }
        });
  
        // 将整理好的数据转换为数组形式
        this.favoriteCategories = Object.values(categoriesMap);
  
        // 设定默认选中的收藏夹
        if (this.favoriteCategories.length > 0) {
          this.selectedTab = this.favoriteCategories[0].name;
        }
      }
      } catch (error) {
      console.error('获取收藏夹数据失败', error);
      }
    },
  
  
      selectTab(tab) {
        this.selectedTab = tab;
      },
      async addCategory() {
        if (!this.newCategoryName.trim()) {
          this.$message.error('收藏夹名称不能为空');
          return;
          };
        
          const serverIP = localStorage.getItem('serverIP');
          const accessToken = localStorage.getItem('accessToken');
          // 使用 axios 上传新的收藏夹
          const response = await axios.post(`http://${serverIP}:8080/Favorites-add-type`,null, {
            params: {'type': this.newCategoryName},
            headers: {
              'accessToken': accessToken
            }
          });
          // 处理后端的响应
          if (response.data.success) {
            this.$message.success('收藏夹添加成功');
            // 将新收藏夹添加到前端列表中
            this.favoriteCategories.push({
              name: this.newCategoryName,
              posts: [],
            });
            this.newCategoryName = ''; // 清空输入框
          } else {
            this.$message.error('收藏夹添加失败');
          }
          
        
      },
      async openEditPrompt(category, index) {
      try {
        const { value } = await ElMessageBox.prompt('请输入新的收藏夹名称', '编辑收藏夹', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          inputValue: category.label,
        });
  
        if (value.trim()) {
          const serverIP = localStorage.getItem('serverIP');
          const accessToken = localStorage.getItem('accessToken');
  
  
          // 向后端发送请求，更新收藏夹名称
          const response = await axios.post(`http://${serverIP}:8080/Favorites-update-type`, null, {
            params: {
              'type': category.name,
              'newType': value.trim()
            }, // 传递新的收藏夹名称
            headers: {
              'accessToken': accessToken, // 使用Bearer格式的JWT令牌
            },
          });
  
          // 检查后端的响应
          if (response.data.success) {
            // 更新前端显示的收藏夹名称
            this.favoriteCategories[index].name = value.trim();
            this.favoriteCategories[index].label = value.trim();
  
            ElMessage({
              message: '收藏夹名称已更新',
              type: 'success',
            });
          } else {
            ElMessage({
              message: '更新收藏夹名称失败',
              type: 'error',
            });
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('更新收藏夹名称时出现错误', error);
          ElMessage({
            type: 'error',
            message: '更新收藏夹名称时出现错误',
          });
        } else {
          ElMessage({
            type: 'info',
            message: '已取消编辑',
          });
        }
      }
    },
  
    async confirmDelete(index) {
    // 弹出确认框
    try {
      await ElMessageBox.confirm(
        '确定要删除这个收藏夹吗？此操作不可撤销。',
        '警告',
        {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning',
        }
      );
  
      // 获取服务器IP和JWT令牌
      const serverIP = localStorage.getItem('serverIP');
      const accessToken = localStorage.getItem('accessToken');
      
      // 获取要删除的收藏夹类型
      const typeToDelete = this.favoriteCategories[index].name; // 假设name是收藏夹的类型
  
      // 调用后端API删除收藏夹，传递type和JWT令牌
      const response = await axios.post(`http://${serverIP}:8080/Favorites-delete-type`, null, {
        params: { 'type': typeToDelete }, // 传递type参数给后端
        headers: {
          'accessToken': accessToken, 
        },
      });
  
      if (response.data.success) {
        // 删除成功后从前端列表中移除收藏夹
        this.favoriteCategories.splice(index, 1);
  
        ElMessage({
          type: 'success',
          message: '收藏夹已删除',
        });
      } else {
        ElMessage({
          type: 'error',
          message: '删除收藏夹失败',
        });
      }
    } catch (error) {
      // 处理取消删除或请求错误的情况
      if (error !== 'cancel') {
        console.error('删除收藏夹时出现错误', error);
        ElMessage({
          type: 'error',
          message: '删除收藏夹时出现错误',
        });
      } else {
        ElMessage({
          type: 'info',
          message: '已取消删除',
        });
      }
    }
  },
  
      
      beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
        const isLt2M = file.size / 1024 / 1024 < 2;
  
        if (!isJPG) {
          ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
        }
        if (!isLt2M) {
          ElMessage.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
      },
      handleAvatarSuccess(response, file) {
        this.newAvatarUrl = URL.createObjectURL(file.raw);
      },
      
      // 上传成功后的处理
      handleAvatarSuccess(response, file) {
        this.newAvatarUrl = URL.createObjectURL(file.raw);  // 预览新头像
        this.newAvatarFile = file.raw;  // 保存上传的文件
      },
      // 触发隐藏的 input[file] 元素
      triggerFileInput() {
        this.$refs.fileInput.click();
      },
  
      // 处理文件选择
      handleFileChange(event) {
        const file = event.target.files[0];
        if (file) {
          const isImage = file.type.startsWith('image/');
          const isLt2M = file.size / 1024 / 1024 < 2;
  
          if (!isImage) {
            this.$message.error('只能上传图片文件');
            return;
          }
          if (!isLt2M) {
            this.$message.error('图片文件不能超过 2MB');
            return;
          }
  
          // 将文件保存到 newAvatarFile，并生成预览 URL
          this.newAvatarFile = file;
          this.newAvatarUrl = URL.createObjectURL(file);
        }
      },
      // 上传头像与个人信息
      async saveProfile() {
        try {
          const serverIP = localStorage.getItem('serverIP');
          const accessToken = localStorage.getItem('accessToken');
  
          // 先构造修改个人信息的数据，包含用户名和个性签名
          const requestData = {
            userName: this.newNickname,
            bio: this.newSignature,
            privacy: this.privacy,
            gender: this.gender,
          };
  
          // 如果用户上传了新的头像文件，则单独发送头像上传请求
          if (this.newAvatarFile) {
            const formData = new FormData();
            formData.append('file', this.newAvatarFile);
  
            const avatarResponse = await axios.post(`http://${serverIP}:8080/HomePage-updateAvatar`, formData, {
              headers: {
                'accessToken': accessToken,
              },
            });
  
            // 检查头像上传是否成功
            if (!avatarResponse.data.success) {
              this.$message.error('头像上传失败');
              return; // 如果头像上传失败，则中断
            }
          }
  
          // 无论是否上传头像，都发送个人信息更新请求
          const profileResponse = await axios.post(`http://${serverIP}:8080/HomePage-update`, requestData, {
            headers: {
              'accessToken': accessToken,
            },
          });
  
          // 检查个人信息更新是否成功
          if (profileResponse.data.success) {
            this.$message.success('个人信息保存成功');
            this.editProfileVisible = false; // 关闭对话框
          } else {
            this.$message.error('个人信息保存失败');
          }
  
          this.fetchUserData();
        } catch (error) {
          console.error('保存个人信息失败', error);
          this.$message.error('保存个人信息时出现错误');
        }
      }
  
      
    },
  };
  </script>
  
  <style scoped>
  .personal-info {
    height: 100vh;
    width: 85vw;
    background: linear-gradient(135deg, #f3f4f6, #ffffff);
    padding: 30px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }
  
  .user-info-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    background-color: #fff;
    padding: 20px;
    border-radius: 15px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  
  .left-info {
    display: flex;
    align-items: center;
  }
  
  .right-info {
    display: flex;
    align-items: center;
  }
  
  .avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    margin-right: 20px;
    border: 3px solid #eee;
  }
  
  .avatar-uploader {
    display: block;
    width: 100px;
    height: 100px;
    border: 1px dashed #d9d9d9;
    border-radius: 50%;
    cursor: pointer;
    overflow: hidden;
    position: relative;
    margin: 0 auto; /* 让上传控件居中 */
  }
  
  .avatar-upload-wrapper {
    text-align: center; /* 让整个头像上传区域居中对齐 */
    margin-bottom: 20px;
  }
  
  .avatar-upload-text {
    font-size: 14px;
    color: #666;
    margin-top: 10px;
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    text-align: center;
    line-height: 100px;
  }
  
  .icon-size {
    font-size: 24px;
    width: 24px;
    height: 24px;
    color:#777;
  }
  
  .user-details h2 {
    font-size: 26px;
    margin: 0;
    color: #333;
    font-weight: 600;
  }
  
  .user-details p {
    font-size: 16px;
    color: #666;
    margin: 8px 0;
  }
  
  .signature {
    font-style: italic;
    color: #999;
    font-size: 14px;
  }
  
  .stats p {
    font-size: 18px;
    margin: 10px 0;
    color: #333;
  }
  
  .stats span {
    font-weight: 600;
    color: #007bff;
  }
  
  .add-category {
    margin-bottom: 20px;
  }
  
  .add-category input {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-right: 10px;
  }
  
  .add-category button {
    padding: 8px 12px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .add-category button:hover {
    background-color: #0056b3;
  }
  
  .tab-section {
    margin-bottom: 30px;
  }
  
  .tabs {
    list-style: none;
    padding: 0;
    display: flex;
    gap: 30px;
  }
  
  .tabs li {
    position: relative;
    cursor: pointer;
    padding: 10px 20px;
    background-color: #fff;
    border-radius: 25px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    color: #666;
    transition: background-color 0.3s, color 0.3s;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .tabs li:hover {
    background-color: #f5f5f5;
  }
  
  .tabs li.active {
    background-color: #007bff;
    color: white;
    font-weight: 600;
  }
  
  .tabs i {
    margin-left: 10px;
    cursor: pointer;
    color: #666;
  }
  
  .tabs i:hover {
    color: #007bff;
  }
  
  .posts-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  
  .post-item {
    padding: 20px;
    background-color: #fff;
    border-radius: 15px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  .post-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  }
  
  .post-item h4 {
    margin: 0 0 10px;
    color: #333;
    font-size: 20px;
    font-weight: 500;
  }
  
  .post-excerpt {
    color: #777;
    margin-bottom: 15px;
  }
  
  .read-more {
    color: #007bff;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s;
  }
  
  .read-more:hover {
    color: #0056b3;
  }
  
  .edit-profile-section {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .edit-profile-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .el-form-item {
    width: 100%;
  }
  
  .el-input {
    width: 100%;
  }
  
  .avatar-preview {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 150px; /* 根据需要设置合适的宽度 */
    height: 150px; /* 根据需要设置合适的高度 */
    border-radius: 50%;
    overflow: hidden; /* 防止图片溢出预览框 */
    border: 1px solid #d9d9d9; /* 适当的边框 */
  }
  
  .avatar-image {
    max-width: 100%; /* 确保图片不会超过容器的宽度 */
    max-height: 100%; /* 确保图片不会超过容器的高度 */
    object-fit: cover; /* 图片按比例裁剪，覆盖整个预览区域 */
  }
  
  </style>
  