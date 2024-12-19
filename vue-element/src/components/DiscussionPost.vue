<template>
  <div class="post-card">
    <h3 class="post-title">{{ post.title }}</h3>
    <p class="post-author">作者: {{ post.author }}</p>
    <p class="post-date">{{ post.date }}</p>
    
    <div class="post-likes">
      <span class="like-icon">👍</span> {{ post.likes }}
      <span class="favorite-count"><ElIcon><StarFilled /></ElIcon> </span>{{ post.favoriteCount }}
    </div>
    <button class="details-button" @click="goTonoticeDetail(post.id)">查看详情</button>
  </div>
</template>

<script>
import { ElIcon } from 'element-plus';
import { View, StarFilled } from '@element-plus/icons-vue'; // 新增 ThumbUp 图标

export default {
  components: {
    ElIcon,
    View,
    StarFilled,
  },
  props: {
    post: Object
  },
  methods: {
    goTonoticeDetail(id) {
      this.$router.push({ name: 'Posting', params: { id: String(id) } });
    }
  }
};
</script>

<style scoped>
.post-card {
  width: 220px; /* 固定宽度 */
  height: 180px; /* 固定高度 */
  background-color: #ffffff;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.post-title {
  font-size: 1.1em;
  color: #333;
  margin: 0;
}

.post-author,
.post-date {
  font-size: 0.85em;
  color: #666;
  margin: 0;
}

.post-card:hover{
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}
.post-likes {
  display: flex;
  align-items: center;
  gap: 4px; /* 图标与数字之间的间距 */
  font-size: 0.9em;
  color: #666;
}

.view-count, .like-icon, .favorite-count {
  display: flex;
  margin-right: 4px;
  gap: 7px; /* 每个图标和数字的间距 */
}

.details-button {
  background-color: #007bff;
  color: #ffffff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85em;
}

.details-button:hover {
  background-color: #0056b3;
}
</style>
