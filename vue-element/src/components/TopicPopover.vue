<template>
    <div v-if="visible" class="popover">
      <div class="popover-content">
        <h3>#{{ topicName }} 相关帖子</h3>
        <ul>
          <li v-for="post in relatedPosts" :key="post.id">{{ post.content }}</li>
        </ul>
      </div>
    </div>
</template>
  
<script>
import axios from 'axios';
  
export default {
    name: 'TopicPopover',
    props: ['topicName'],
    data() {
      return {
        visible: false,
        relatedPosts: []
      };
    },
    methods: {
      async loadRelatedPosts() {
        try {
          const response = await axios.get(`/api/posts?topic=${this.topicName}`);
          this.relatedPosts = response.data;
        } catch (error) {
          console.error("获取相关帖子失败:", error);
        }
      },
      show() {
        this.visible = true;
        this.loadRelatedPosts();
      },
      hide() {
        this.visible = false;
      }
    }
  };
  </script>
  
  <style scoped>
  .popover {
    position: absolute;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 10px;
    width: 300px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    z-index: 10;
  }
  
  .popover-content ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
  }
  
  .popover-content li {
    margin: 5px 0;
  }
  </style>
  