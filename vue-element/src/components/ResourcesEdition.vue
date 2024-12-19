<template>
  <div class="resources">
    <h1>课程资源</h1>
    <el-button type="primary" plain @click="showUpload = true">上传资源</el-button>

    <!-- 编辑资源对话框 -->
    <el-dialog title="编辑资源" v-model="showEditDialog" width="30%">
      <!-- 是否可以让学生下载 选项 -->
      <el-checkbox v-model="editDownloadable" label="可以让学生下载"></el-checkbox>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 上传资源对话框 -->
    <el-dialog title="上传资源" v-model="showUpload" width="30%">
      <!-- 是否创建文件夹 选项 -->
      <el-checkbox v-model="isCreatingFolder" label="创建文件夹" @change="handleCreateFolderChange"></el-checkbox>

      <!-- 创建文件夹名称输入框 -->
      <el-input
        v-if="isCreatingFolder"
        v-model="folderName"
        placeholder="请输入文件夹名称"
        class="folder-input"
      ></el-input>

      <!-- 上传文件框 -->
      <el-upload
        v-else
        class="upload-demo"
        drag
        :action="uploadUrl"
        :headers="{ accessToken: accessToken }"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :data="uploadData"
        :disabled="isCreatingFolder"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>

      <!-- 是否可以被下载 选项 -->
      <el-checkbox v-model="isDownloadable" label="可以被学生下载"></el-checkbox>

      <template #footer>
        <el-button @click="showUpload = false">取消</el-button>
        <el-button type="primary" @click="confirmUploadOrCreateFolder">确认</el-button>
      </template>
    </el-dialog>

    <!-- 资源列表 -->
    <div class="resource-container">
      <div
        v-for="item in resources"
        :key="item.id"
        class="resource-item"
        @click="handleItemClick(item)"
      >
        <div class="resource-icon">
          <component :is="item.docType === '0' ? Folder : Document" class="icon" />
          <div v-if="item.docType !== '0'" class="overlay">
            <Download class="download-icon" />
          </div>
        </div>
        <div class="resource-name">{{ item.docName }}</div>
        <el-button class="edit" size="small" type="info" @click.stop="openEditDialog(item)">编辑</el-button>
        <el-button class="delete" size="small" type="danger" @click.stop="deleteResource(item.id)">删除</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { Folder, Document, Download } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const resources = ref([]);
const showUpload = ref(false);
const isDownloadable = ref(true);
const isCreatingFolder = ref(false);  // 控制“是否创建文件夹”选项
const folderName = ref("");           // 创建文件夹时输入的文件夹名称
const serverIP = localStorage.getItem('serverIP');
const accessToken = localStorage.getItem('accessToken');

// 编辑资源相关
const showEditDialog = ref(false);
const editDownloadable = ref(false);
const editDocId = ref(null);  // 当前编辑的资源ID

// 上传 URL 和创建文件夹 URL
const uploadUrl = computed(() => `http://${serverIP}:8080/lessons-docs-insert`);
const createFolderUrl = computed(() => `http://${serverIP}:8080/lessons-directory-insert`);
const updateDownloadLicenseUrl = computed(() => `http://${serverIP}:8080/lessons-docs-license-update`);

// 上传的请求参数
const uploadData = computed(() => ({
  lessonId: route.params.id,
  docFatherId: route.params.fatherId,
  downloadLicense: isDownloadable.value ? 1 : 0,
  docName: isCreatingFolder.value ? folderName.value : "",
  file: isCreatingFolder.value ? null : undefined  // 如果创建文件夹，将 file 置为 null
}));

// 打开编辑对话框
const openEditDialog = (item) => {
  editDocId.value = item.id;
  editDownloadable.value = item.downloadLicense === 1;
  showEditDialog.value = true;
};

// 确认编辑并调用 API
const confirmEdit = async () => {
  try {
    const response = await axios.post(updateDownloadLicenseUrl.value, null, {
      params: {
        docsId: editDocId.value,
        license: editDownloadable.value ? 1 : 0
      },
      headers: { accessToken: accessToken }
    });
    if (response.data.success) {
      alert('权限更新成功');
      fetchResources();
      showEditDialog.value = false;
    } else {
      console.error("更新权限失败:", response.data.message);
    }
  } catch (error) {
    console.error("请求失败:", error);
  }
};

// 当选择创建文件夹时，隐藏上传文件框，取消创建文件夹则显示上传文件框
const handleCreateFolderChange = () => {
  if (!isCreatingFolder.value) {
    folderName.value = "";  // 清空文件夹名称
  }
};

const fetchResources = async () => {
  const lessonId = route.params.id;
  const fatherId = route.params.fatherId;

  try {
    const url = `http://${serverIP}:8080/lessons-docs`;
    const response = await axios.post(url, null, {
      params: { lessonId, fatherId },
      headers: { accessToken: localStorage.getItem('accessToken') },
    });

    if (response.data.success) {
      resources.value = response.data.data;
    } else {
      console.error('获取资源失败:', response.data.message);
    }
  } catch (error) {
    console.error('请求失败:', error.response ? error.response.data : error);
  }
};

const handleUploadSuccess = (response) => {
  if (response.success) {
    fetchResources();
    showUpload.value = false;
  } else {
    console.error('上传失败:', response.message);
  }
};

const handleUploadError = (error) => {
  console.error('上传请求失败:', error);
};

const beforeUpload = (file) => {
  // 如果不创建文件夹，将文件名赋值给 docName
  if (!isCreatingFolder.value) {
    uploadData.value.docName = file.name;
  }
  return true;
};

// 确认按钮处理：创建文件夹或上传文件
const confirmUploadOrCreateFolder = async () => {
  if (isCreatingFolder.value) {
    // 创建文件夹请求
    if (!folderName.value) {
      alert("请输入文件夹名称");
      return;
    }
    try {
      const response = await axios.post(createFolderUrl.value, null, {
        params: {
          lessonId: route.params.id,
          docFatherId: route.params.fatherId,
          downloadLicense: isDownloadable.value ? 1 : 0,
          docName: folderName.value
        },
        headers: { accessToken: accessToken }
      });
      if (response.data.success) {
        fetchResources();
        showUpload.value = false;
      } else {
        console.error("创建文件夹失败:", response.data.message);
      }
    } catch (error) {
      console.error("请求失败:", error);
    }
  } else {
    alert("请直接拖拽或选择文件上传");
  }
};

const handleItemClick = async (item) => {
  if (item.docType === '0') {
    router.push({ name: 'ResourcesEdition', params: { id: route.params.id, fatherId: item.id } });
  } else {
    try {
      const url = `http://${serverIP}:8080/lessons-docs-download`;
      const response = await axios.post(url, null, {
        params: { docsId: item.id },
        headers: { accessToken: localStorage.getItem('accessToken') },
        responseType: 'blob',
      });

      const blob = new Blob([response.data], { type: response.headers['Content-Type'] });
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;

      const contentDisposition = response.headers['Content-Disposition'];
      let filename = item.docName || 'downloaded_file';
      if (contentDisposition && contentDisposition.includes('filename=')) {
        filename = contentDisposition.split('filename=')[1].split(';')[0].replace(/['"]/g, '');
      }

      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      window.URL.revokeObjectURL(downloadUrl);
      document.body.removeChild(link);
    } catch (error) {
      console.error('文件下载失败:', error);
    }
  }
};

const deleteResource = async (id) => {
  try {
    const url = `http://${serverIP}:8080/lessons-docs-delete`;
    const response = await axios.post(url, null, {
      params: { docsId: id },
      headers: { accessToken: localStorage.getItem('accessToken') },
    });

    if (response.data.success) {
      alert('资源已删除');
      fetchResources();
    } else {
      console.error('删除失败:', response.data.message);
    }
  } catch (error) {
    console.error('删除资源失败:', error);
  }
};

onMounted(() => {
  fetchResources();
});

watch(() => route.params, () => {
  fetchResources();
});
</script>


<style scoped>
.resources {
  width: 82vw;
  max-width: 82vw;
  margin-left: 3vw;
  text-align: center;
  overflow-y: auto;
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.resource-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.resource-item {
  width: 150px;
  margin: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
}

.resource-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.resource-icon {
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.icon {
  font-size: 3rem;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6);
  display: none;
  align-items: center;
  justify-content: center;
}

.download-icon {
  font-size: 1.5rem;
  color: #333;
}

.edit {
  margin-bottom: 5px;
}

.delete {
  margin: 0 !important;
}

.resource-name {
  font-size: 1rem;
  color: #333;
  text-align: center;
}
</style>