<template>
  <div class="resources">
    <h1>课程资源</h1>
    <div class="resource-container">
      <div
        v-for="item in resources"
        :key="item.id"
        class="resource-item"
        @click="handleItemClick(item)"
      >
        <!-- 使用 element-plus 的图标 -->
        <div class="resource-icon">
          <component :is="item.docType === '0' ? Folder : Document" class="icon" />
          <!-- 当鼠标悬停在整个卡片上时显示白色遮罩和下载图标 -->
          <div v-if="item.docType !== '0'" class="overlay">
            <Download class="download-icon" />
          </div>
        </div>
        <div class="resource-name">{{ item.docName }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { Folder, Document, Download } from '@element-plus/icons-vue'; // 引入文件夹、文件和下载的图标

const route = useRoute();
const router = useRouter();
let lessonId = route.params.id;
let fatherId = route.params.fatherId;
const resources = ref([]);
const serverIP = localStorage.getItem('serverIP');

const fetchResources = async () => {
  try {
    lessonId = route.params.id;
    fatherId = route.params.fatherId;
    const url = `http://${serverIP}:8080/lessons-docs`;
    const response = await axios.post(url, null, {
      params: {
        lessonId: lessonId,
        fatherId: fatherId,
      },
      headers: {
        accessToken: localStorage.getItem('accessToken'),
      },
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

const handleItemClick = async (item) => {
  if (item.docType === '0') {
    // 是文件夹，导航到下一级
    router.push({ name: 'Resources', params: { id: lessonId, fatherId: item.id } });
  } else {
    // 是文件，下载文件
    try {
      const url = `http://${serverIP}:8080/lessons-docs-download`;
      const response = await axios.post(url,null, {
        params: {
          docsId: item.id,
        },
        headers: {
          accessToken: localStorage.getItem('accessToken'),
        },
        responseType: 'blob', // 关键：设置响应类型为 blob
      });

      // 创建 Blob 对象
      const blob = new Blob([response.data], { type: response.headers['Content-Type'] });
      const downloadUrl = window.URL.createObjectURL(blob);

      // 创建下载链接
      const link = document.createElement('a');
      link.href = downloadUrl;

      // 从响应头中获取文件名（如果存在）
      const contentDisposition = response.headers['Content-Disposition'];
      let filename = item.docName || 'downloaded_file';

      if (contentDisposition && contentDisposition.includes('filename=')) {
        filename = contentDisposition.split('filename=')[1].split(';')[0].replace(/['"]/g, '');
      }

      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();

      // 清理 URL 对象和下载链接
      window.URL.revokeObjectURL(downloadUrl);
      document.body.removeChild(link);
    } catch (error) {
      console.error('文件下载失败:', error);
    }
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
  width: 85vw;
  max-width: 85vw;
  margin: 0 auto;
  padding: 0;
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
  justify-content: space-around;
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

/* 当鼠标悬停在整个卡片时显示遮罩和下载图标 */
.resource-item:hover .overlay {
  display: flex;
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
  font-size: 3rem; /* 调整图标大小 */
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6); /* 60% 透明的白色遮罩 */
  display: none;
  align-items: center;
  justify-content: center;
}

.download-icon {
  font-size: 1.5rem;
  color: #333;
}

.resource-name {
  font-size: 1rem;
  color: #333;
  text-align: center;
}
</style>
