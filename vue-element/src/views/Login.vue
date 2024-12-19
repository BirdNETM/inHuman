<template>
  <el-container class="login-container">
    <el-header>
      <h1>登录页面</h1>
    </el-header>
    <el-main>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px" class="login-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const loginForm = ref({
      username: '',
      password: ''
    });

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    };

    const loginFormRef = ref(null);
    const router = useRouter();
    const serverIP = localStorage.getItem('serverIP');

    const onSubmit = () => {
      loginFormRef.value.validate((valid) => {
        if (valid) {
          axios.post(`http://${serverIP}:8080/login`, {
            userName: loginForm.value.username,
            password: loginForm.value.password
          })
          .then(response => {
            if (response.data.success) {
              const accessToken = response.data.data.accessToken;
              const refreshToken = response.data.data.refreshToken;
              
              // 将令牌存储到localStorage中
              localStorage.setItem('accessToken', accessToken);
              localStorage.setItem('refreshToken', refreshToken);

              console.log('登录成功, 令牌已保存');

              // 跳转到主页
              if(response.data.data.position == 1){//student's page
                router.push({ name: 'Home' });
              }else if(response.data.data.position == 0){//teacher's page
                router.push({ name: 'TeacherHome' })
              }else if(response.data.data.position == 2){//supporter's page

              }


            } else {
              console.log('登录失败，账号或密码错误');
            }
          })
          .catch(error => {
            console.error('登录请求失败: ', error);
          });
        } else {
          console.log('验证失败!');
        }
      });
    };

    return {
      loginForm,
      rules,
      loginFormRef,
      onSubmit
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f2f2f2;
}

.login-form {
  width: 300px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
