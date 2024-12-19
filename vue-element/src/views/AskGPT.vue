<template>
    <div>
      <!-- Circular button to toggle chat -->
      <div class="toggle-button" @click="toggleChat">GPT</div>
  
      <!-- Chat container -->
      <div v-if="isChatOpen" class="ai-chat-container">
        <div class="chat-window">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message', message.sender]"
          >
            <div class="message-box">
              <span>{{ message.text }}</span>
            </div>
          </div>
        </div>
        <div class="input-area">
          <input
            type="text"
            v-model="userInput"
            @keyup.enter="sendMessage"
            placeholder="请不要用GPT写作业"
          />
          <button @click="sendMessage">发送</button>
        </div>
      </div>
    </div>
  </template>
  
<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  setup() {
    const isChatOpen = ref(false);
    const messages = ref([{ sender: 'bot', text: '有什么可以帮忙的？目前回答最长为100token，约50字' }]);
    const userInput = ref('');

    const toggleChat = () => {
      isChatOpen.value = !isChatOpen.value;
    };

         const sendMessage = async () => {
        if (!userInput.value.trim()) return;

        // 添加用户消息到队列
         messages.value.push({ sender: 'user', text: userInput.value });

        // 发送用户消息到 GPT
        await sendMessageToGPT(userInput.value);

        // 清空输入框
        };


        const sendMessageToGPT = async (userMessage) => {
  try {
    // 添加用户消息到队列

    // 显示“AI 正在思考”的占位消息
    const thinkingMessageIndex = messages.value.push({ sender: 'bot', text: 'AI is thinking...' }) - 1;
    // 发出请求
    userInput.value = '';

    console.log(userMessage)
    const response = await axios.post(
      "http://127.0.0.1:8080/chat",
      { message: userMessage }, // 请求体
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`, // 使用 Bearer Token
          'Content-Type': 'application/json' // 明确声明请求体类型
        }
      }
    );


    // 检查是否需要刷新令牌
    if (response.data.message === 'NOT_LOGIN') {
      await refreshAuthToken();
    } else {
      const gptReply = response.data;

      // 替换“AI 正在思考”占位消息为实际回复
      messages.value[thinkingMessageIndex] = { sender: 'bot', text: gptReply.reply };
    }
  } catch (error) {
    console.error('发送信息给 GPT 失败:', error.response ? error.response.data : error);

    // 替换“AI 正在思考”占位消息为错误信息
  }
};



    return {
      isChatOpen,
      sendMessageToGPT,
      toggleChat,
      messages,
      userInput,
      sendMessage,
      
    };
  },
};
</script><style scoped>
/* Circular button styles */
.toggle-button {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 50px;
  height: 50px;
  background-color: #28a745;
  color: #fff;
  font-size: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  user-select: none;
  z-index: 1000;
}

/* Chat container styles */
.ai-chat-container {
  position: fixed;
  bottom: 170px;
  right: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 400px;
  height: 600px;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  z-index: 999;
}

.chat-window {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: #f9f9f9;
}

/* Message styles */
.message {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 12px;
}

.message.user {
  justify-content: flex-end;
}

.message-box {
  max-width: 70%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: #f1f1f1;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message.user .message-box {
  background-color: #d1e7fd;
  text-align: right;
  border: 1px solid #007bff;
}

.message.bot .message-box {
  background-color: #f1f1f1;
  text-align: left;
  border: 1px solid #ccc;
}

.input-area {
  display: flex;
  padding: 8px;
  background-color: #fff;
  border-top: 1px solid #ccc;
}

input {
  flex: 1;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  margin-left: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
