<template>
  <div class="my-tutors-container">
    <!-- 添加助教卡片 -->

    <h1 >助教管理</h1>

    <!-- Tutor 卡片 -->
    <el-row :gutter="20">

      <el-col :span="24" v-for="tutor in tutors" :key="tutor.tutor_id">
        <el-card
          class="tutor-card"
          :body-style="{ padding: '10px' }"
          @click="openEditDialog(tutor)"
        >
          <div class="tutor-content">
          <p><strong>姓名:</strong> {{ tutor.username }}</p>
          <p><strong>课程:</strong> {{ tutor.lessonName }}</p>

          <p>
            <strong>作业管理权限:</strong>
            <span :style="{ color: tutor.homework_control ? 'green' : 'red' }">
              {{ tutor.homework_control ? '有权限' : '无权限' }}
            </span>
          </p>
          <p>
            <strong>文档管理权限:</strong>
            <span :style="{ color: tutor.docs_control ? 'green' : 'red' }">
              {{ tutor.docs_control ? '有权限' : '无权限' }}
            </span>
          </p>
          <p>
            <strong>成绩管理权限:</strong>
            <span :style="{ color: tutor.points_control ? 'green' : 'red' }">
              {{ tutor.points_control ? '有权限' : '无权限' }}
            </span>
          </p>
          <p>
            <strong>考试管理权限:</strong>
            <span :style="{ color: tutor.exam_control ? 'green' : 'red' }">
              {{ tutor.exam_control ? '有权限' : '无权限' }}
            </span>
          </p>
        </div>

        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card
          class="add-tutor-card"
          :body-style="{ padding: '10px', textAlign: 'center' }"
          @click="openAddTutorDialog"
        >
          <h3>+ 添加助教</h3>
        </el-card>
      </el-col>
    </el-row>


    <el-dialog
  title="添加助教"
  v-model="isAddTutorDialogVisible"
  :before-close="cancelAdd"
>
  <el-form ref="addTutorForm" :model="newTutor" label-width="100px">
    <!-- 添加助教信息 -->
    <el-form-item label="学号">
      <el-input v-model="newTutor.studentCode" placeholder="请输入助教学号" />
    </el-form-item>
    <el-form-item label="课程">
  <el-select v-model="newTutor.lessonId" placeholder="请选择课程">
    <el-option
      v-for="lesson in lessons"
      :key="lesson.id"
      :label="lesson.name"
      :value="lesson.id"
    ></el-option>
  </el-select>
</el-form-item>

      </el-form>

  <div slot="footer" class="dialog-footer">
    <el-button @click="cancelAdd">取消</el-button>
    <el-button type="primary" @click="addTutor">确认</el-button>
  </div>
</el-dialog>




  <el-dialog
  title="编辑助教信息"
  v-model="isEditDialogVisible"
  :before-close="cancelEdit"
>
  <el-form ref="editTutorForm" :model="editedTutor" label-width="100px">
    <!-- 编辑助教信息 -->
    <el-form-item label="姓名">{{editedTutor.username}}</el-form-item>

    <!-- 权限切换 -->
    <el-form-item label="作业管理权限">
      <el-switch
        v-model="editedTutor.homework_control"
        active-text="有权限"
        inactive-text="无权限"
      />
    </el-form-item>
    <el-form-item label="文档管理权限">
      <el-switch
        v-model="editedTutor.docs_control"
        active-text="有权限"
        inactive-text="无权限"
      />
    </el-form-item>
    <el-form-item label="成绩管理权限">
      <el-switch
        v-model="editedTutor.points_control"
        active-text="有权限"
        inactive-text="无权限"
      />
    </el-form-item>
    <el-form-item label="考试管理权限">
      <el-switch
        v-model="editedTutor.exam_control"
        active-text="有权限"
        inactive-text="无权限"
      />
    </el-form-item>
  </el-form>

  <div slot="footer" class="dialog-footer">
    <el-button type="danger" @click="confirmDelete">删除助教</el-button>
    <el-button @click="cancelEdit">取消</el-button>
    <el-button type="primary" @click="updateTutor">确认</el-button>
  </div>
</el-dialog>


  </div>
</template>

  
  
<script>
import { ref,reactive, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();
    const serverIP = '192.168.219.212';
    const searchQuery = ref(''); // 用户输入的搜索关键字
    const isEditDialogVisible = ref(false);
    const loading = ref(false); // 加载状态
    const error = ref(null); // 错误信息
    // 当前编辑的助教
    const editedTutor = reactive({
      lesson_id:'',
  tutor_id: '', // 助教 ID
  username: '', // 姓名
  lessonName: '', // 授课课程名称
  homework_control: false, // 作业管理权限
  docs_control: false, // 文档管理权限
  points_control: false, // 成绩管理权限
  exam_control: false, // 考试管理权限
});






    // Tutor 数据
    const tutors = ref([]); // 确保 tutors 是一个响应式数组

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
    const response = await axios.post(`http://${serverIP}:8080/refresh-token`, {
      refreshToken: refreshToken
    });

    if (response.data.success) {
      const newAccessToken = response.data.data.accessToken;
      const newRefreshToken = response.data.data.refreshToken;

      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);

      console.log('新令牌已获取并存储:', newAccessToken, newRefreshToken);

      // 刷新成功后重新获取通知数据
      await fetchNotice(noticeId);
    } else {
      // 刷新失败则跳转到登录页面
      router.push({ name: 'Login' });
    }
  } catch (error) {
    console.error('刷新令牌请求失败:', error);
    router.push({ name: 'Login' });
  }
};



const fetchLessonsAndTutors = async () => {
  loading.value = true;
  error.value = null;

  try {
    // 第一次请求：获取 lessons 数据
    const accessToken = localStorage.getItem('accessToken'); // 获取 accessToken

    // 发送 POST 请求，传递 noticeId 和 accessToken
    const lessonsResponse = await axios.post(`http://${serverIP}:8080/lessons`, 
    {}, 
    {
      headers: {
        'Content-Type': 'application/json',
        'accessToken': accessToken // 请求头传递 accessToken
      }
    });


    if (!lessonsResponse.data.success) {
      throw new Error(lessonsResponse.data.message || '获取课程数据失败');
    }


    const lessonIds = lessonsResponse.data.data.map((lesson) => lesson.id);

    console.log('成功获取课程 ID:', lessonIds);

    for (const lessonId of lessonIds) {
      const tutorData = await fetchTutorsForLesson(lessonId);
      if (tutorData) {
        tutors.value.push(...tutorData); // 展开 tutorData 数组
      }
      console.log('最终的 tutors 数据:', tutorData); // 确认 tutors 数据是否正确

    }

  } catch (err) {
    error.value = err.message || '请求出错';
    console.error('错误:', err);
  } finally {
    loading.value = false;
  }
};


const fetchTutorsForLesson = async (lessonId) => {
  try {
    console.log('成功获取课程 ID:', lessonId);
    const lessonIdInt = parseInt(lessonId, 10);

    const response = await axios.post(
      `http://${serverIP}:8080/Tutor-get?lessonId=${lessonIdInt}`, // 将 lessonId 作为查询参数
      {}, // POST 请求体为空
      {
        headers: {
          'Content-Type': 'application/json',
          'accessToken': localStorage.getItem('accessToken'),
        },
      }
    );
    console.log(`课程 ID ${lessonId} 的导师信息:`, response.data);
    return response.data.data;
  } catch (error) {
    console.error(`获取课程 ID ${lessonId} 的导师信息失败:`, error.message);
    return null;
  }
};



    // 打开编辑对话框
    const openEditDialog = (tutor) => {
      // 将选中的助教信息复制到编辑表单
      Object.assign(editedTutor, tutor);
      isEditDialogVisible.value = true;
    };

    // 取消编辑
    const cancelEdit = () => {
      Object.assign(editedTutor, {
        lesson_id:'',
    tutor_id: '', // 助教 ID
    username: '', // 姓名
    lessonName: '', // 授课课程名称
    homework_control: false, // 作业管理权限
    docs_control: false, // 文档管理权限
    points_control: false, // 成绩管理权限
    exam_control: false, // 考试管理权限
  });

      isEditDialogVisible.value = false;
    };



    const confirmDelete = async () => {
  ElMessageBox.confirm(
    '确定要删除此助教吗？此操作不可撤销！',
    '警告',
    {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        // 发起删除请求
        const response = await axios.post(
          `http://${serverIP}:8080/Tutor-delete?tutorId=${editedTutor.tutor_id}&lessonId=${editedTutor.lesson_id}`, // 将 tutorId 和 lessonId 作为查询参数
          {}, // POST 请求体为空  
          {
            headers: {
              'Content-Type': 'application/json',
              accessToken: localStorage.getItem('accessToken'),
            },
          }
        );

        if (response.status === 200) {
          ElMessage.success('删除成功');
          // 确保后端删除成功后再更新本地数据
          tutors.value = []; // 清空现有的 tutors 数据
          await fetchLessonsAndTutors(); // 重新加载数据
          cancelEdit(); // 关闭编辑对话框
        } else {
          ElMessage.error('删除失败，请稍后再试');
        }
      } catch (error) {
        console.error('删除助教时发生错误:', error.message);
        ElMessage.error('删除助教失败，请检查网络或稍后再试');
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};




const updateTutor = async () => {
  try {
    // 构造要发送的数据
    const updateData = {
      tutor_id: editedTutor.tutor_id, // 助教 ID
      lesson_id: editedTutor.lesson_id, // 课程 ID
      homework_control: editedTutor.homework_control, // 作业管理权限
      docs_control: editedTutor.docs_control, // 文档管理权限
      points_control: editedTutor.points_control, // 成绩管理权限
      exam_control: editedTutor.exam_control, // 考试管理权限
    };

    // 发送更新请求到后端
    const response = await axios.post(`http://${serverIP}:8080/Tutor-update`, updateData, {
      headers: {
        'Content-Type': 'application/json',
        accessToken: localStorage.getItem('accessToken'),
      },
    });

    if (response.data.success) {
      // 如果更新成功，更新本地数据
      const index = tutors.value.findIndex((tutor) => tutor.tutor_id === editedTutor.tutor_id);
      if (index !== -1) {
        tutors.value[index] = { ...editedTutor }; // 更新本地列表中的助教信息
      }

      // 提示用户更新成功
      ElMessage.success('助教信息更新成功');
    } else {
      // 提示用户更新失败
      ElMessage.error(response.data.message || '更新助教信息失败');
    }
  } catch (error) {
    // 捕获和处理错误
    console.error('更新助教信息时出错:', error);
    ElMessage.error('网络错误或服务器无响应');
  } finally {
    // 无论成功或失败，均关闭编辑对话框
    cancelEdit();
  }
};

const newTutor = reactive({
  studentCode:'',
  lessonId: '', // 助教 ID
});


    // 添加助教的对话框和数据
    const isAddTutorDialogVisible = ref(false);


    // 打开对话框
    const openAddTutorDialog = () => {
      isAddTutorDialogVisible.value = true;
      console.error('1:',isAddTutorDialogVisible.value);
      fetchLessons(); // 每次打开时加载最新的课程数据
    };

    // 添加助教
    const addTutor = async () => {
      try {

        const response = await axios.post(
          `http://${serverIP}:8080/Tutor-add?studentCode=${newTutor.studentCode}&lessonId=${newTutor.lessonId}`, // 将 tutorId 和 lessonId 作为查询参数
          {}, // POST 请求体为空  
          {
            headers: {
              'Content-Type': 'application/json',
              accessToken: localStorage.getItem('accessToken'),
            },
          }
        );

        if (response.status === 200) {
          ElMessage.success('添加成功');
          // 确保后端删除成功后再更新本地数据
          tutors.value = []; // 清空现有的 tutors 数据
          await fetchLessonsAndTutors(); // 重新加载数据
          cancelEdit(); // 关闭编辑对话框
        } else {
          ElMessage.error('删除失败，请稍后再试');
        }
      } catch (error) {
        console.error('删除助教时发生错误:', error.message);
        ElMessage.error('删除助教失败，请检查网络或稍后再试');
      };
    }

    const cancelAdd =  () => {
      try {

 
        isAddTutorDialogVisible.value = false;

      } catch (error) {
            console.error('添加 Tutor 失败:', error);
          }
    };

    const lessons = ref([]); // 存储课程列表

// 获取课程数据
const fetchLessons = async () => {
  try {
    const response = await axios.post(`http://${serverIP}:8080/lessons`, null, {
      headers: {
        'accessToken': localStorage.getItem('accessToken'),
      },
    });

    if (response.data.success) {
      lessons.value = response.data.data; // 将课程数据存储到 lessons
    } else {
      console.error('获取课程数据失败:', response.data.message);
    }
  } catch (error) {
    console.error('获取课程数据时出错:', error);
  }
};


    onMounted(() => {
      fetchLessonsAndTutors(); // 组件加载时调用获取数据的函数
    });


    return {
      fetchLessonsAndTutors,
      newTutor,
      tutors,
      refreshAuthToken,
      searchQuery,
      isAddTutorDialogVisible,
      cancelAdd,
      openAddTutorDialog,
      addTutor,
      isEditDialogVisible,
      editedTutor,
      tutors,
      openEditDialog,
      cancelEdit,
      updateTutor,
      confirmDelete,
      loading,
      error,
      fetchTutorsForLesson,
      lessons,
    };
  },
};
</script>

  
  
  <style scoped>
.my-tutors-container {
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

.tutor-row {
  display: flex;
  justify-content: flex-start;  /* 从顶部和左侧开始布局 */
  flex-wrap: wrap; /* 宽度不足时自动换行 */
  gap: 15px; /* 卡片之间的水平和垂直间距 */
}

.add-tutor-card {
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  border: 2px dashed #d3d3d3;
  background-color: #f9f9f9;
  color: #606266;
  font-size: 18px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.add-tutor-card:hover {
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #409eff;
}

.tutor-card {
  position: relative; /* 确保红点的绝对定位相对于卡片 */
  width: 80vw;
  height: auto; /* 使卡片高度根据内容自动调整 */
  cursor: pointer;
  transition: transform 0.3s ease;
  margin-bottom: 7px; /* 卡片之间的固定间距 */
  background-color: rgb(228, 237, 201);
}

.tutor-card:hover {
  transform: scale(1.02); /* 悬停时卡片略微放大 */
}

/* Flex 布局，控制各部分的宽度比例 */
.tutor-content {
  display: flex; /* 修正 display 设置为 flex，因为你需要 flex 布局 */
  margin-left: 25px; /* 使用 margin-left 使左侧空出一些空间 */
  flex-direction: column;
  width: 100%;
}


/* 设置标题、内容和发送者的宽度分配 */
.tutor-title {
  flex: 1; /* 占较少宽度 */
  font-size: 16px;
  font-weight: bold;
}

.tutor-text {
  flex: 2; /* content 占较大的宽度 */
  font-size: 14px;
}

.tutor-sender {
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

.tutor-search {
  width: 300px;
}

  </style>