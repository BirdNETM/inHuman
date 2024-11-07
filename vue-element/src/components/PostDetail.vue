<template>
  <div class="post-detail">
    <h2>{{ post.title }}</h2>
    <p>作者: {{ post.author }}</p>
    <p>发布日期: {{ post.date }}</p>
    <p>点赞数: {{ post.likes }}</p>
    <div class="post-content">
      <p>{{ post.content }}</p>
    </div>
    <button @click="goBack">返回</button>
  </div>
</template>

<script>
export default {
  props: ['id'],
  data() {
    return {
      post: {}
    };
  },
  created() {
    // 模拟获取帖子数据
    const posts = [
      { id: 1, title: '如何评价孙笑川', author: '亡灵', date: '2024-10-04', likes: 666, content: '这里是帖子内容' },
      { id: 2, title: 't8', author: 'c8', date: '2024-10-24', likes: 8, content: '这里是另一个帖子内容' }
    ];
    this.post = posts.find(post => post.id === parseInt(this.id));
  },
  methods: {
    goBack() {
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.post-detail {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

button {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
