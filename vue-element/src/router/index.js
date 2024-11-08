import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/Home.vue';
import Login from '@/views/Login.vue';
import MyOptions from '@/views/MyOptions.vue';
import Schedule from '@/views/Schedule.vue'; // 假设这是 "我的课表" 页面
import SchoolNotice from '@/views/SchoolNotice.vue'; // 假设这是 "学校通知" 页面
import CourseInteraction from '@/views/CourseInteraction.vue'; // 假设这是 "课程互动消息" 页面
import UserInfo from '@/views/UserInfo.vue'; // 假设这是 "个人信息" 页面
import ChangePassword from '@/views/ChangePassword.vue'; // 假设这是 "修改密码" 页面
import CourseDetail from '@/views/CourseDetail.vue';
import Assignments from '@/components/Assignments.vue';
import Labs from '@/components/Labs.vue';
import Resources from '@/components/Resources.vue';
import AssignmentDetails from '@/components/AssignmentDetails.vue';
import TeacherHome from '@/views/TeacherHome.vue';
import MyTeaching from '@/views/MyTeaching.vue';
import CourseDetailForTeacher from '@/views/CourseDetailForTeacher.vue';
import ResourcesEdition from '@/components/ResourcesEdition.vue';
import TeacherAssignments from '@/views/TeacherAssignments.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/Home',
      name: 'Home',
      component: Home,  // 主页路由
      children: [
        {
          path: '1-1',
          name: 'MyOptions',
          component: MyOptions, // "我的课程" 页面
        },
        {
          path: '1-2',
          name: 'Schedule',
          component: Schedule, // "我的课表" 页面
        },
        {
          path: '2-1',
          name: 'SchoolNotice',
          component: SchoolNotice, // "学校通知" 页面
        },
        {
          path: '2-2',
          name: 'CourseInteraction',
          component: CourseInteraction, // "课程互动消息" 页面
        },
        {
          path: '3-1',
          name: 'UserInfo',
          component: UserInfo, // "个人信息" 页面
        },
        {
          path: '3-2',
          name: 'ChangePassword',
          component: ChangePassword, // "修改密码" 页面
        },
        {
          path: '3-3',
          name: 'Notification',
          component: SchoolNotice, // "通知" 页面（可以替换成合适的组件）
        },
        {
          path: 'CourseDetail/:id',
          name: 'CourseDetail',
          component: CourseDetail, 
        },
        {
          path: 'Assignments/:id',
          name: 'Assignments',
          component: Assignments
        },
        {
          path: 'Labs',
          name: 'Labs',
          component: Labs
        },
        {
          path: 'Resources/:id:fatherId',
          name: 'Resources',
          component: Resources
        },
        {
          path: 'AssignmentDetails/:id',
          name: 'AssignmentDetails',
          component: AssignmentDetails,
        },

      ],
      redirect:'/Home/1-1'
    },
    {
      path: '/Login',
      name: 'Login',
      component: Login  // 登录页路由
    },
    {
      path: '/',
      redirect: '/Login'
    },
    {
      path: '/TeacherHome',
      name: 'TeacherHome',
      redirect: '/TeacherHome/1-1',
      component: TeacherHome,
      children:[
        {
          path: '1-1',
          name: 'MyTeaching',
          component: MyTeaching, // "我的课程" 页面
        },
        {
          path: 'CourseDetailForTeacher/:id',
          name: 'CourseDetailForTeacher',
          component: CourseDetailForTeacher,
        },
        {
          path: 'ResourcesEdition/:id:fatherId',
          name: 'ResourcesEdition',
          component: ResourcesEdition,
        },
        {
          path: 'TeacherAssignments/:id',
          name: 'TeacherAssignments',
          component: TeacherAssignments,
        },
      ]
    }
  ]
})

export default router;
