import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页总览', icon: 'Odometer', requiresAuth: true }
      }
    ]
  },
  {
    path: '/employees',
    component: Layout,
    redirect: '/employees/list',
    children: [
      {
        path: 'list',
        name: 'EmployeeList',
        component: () => import('@/views/employee/EmployeeList.vue'),
        meta: { title: '员工管理', icon: 'User', requiresAuth: true }
      }
    ]
  },
  {
    path: '/departments',
    component: Layout,
    redirect: '/departments/list',
    children: [
      {
        path: 'list',
        name: 'DepartmentList',
        component: () => import('@/views/department/DepartmentList.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding', requiresAuth: true }
      }
    ]
  },
  {
    path: '/salaries',
    component: Layout,
    redirect: '/salaries/list',
    children: [
      {
        path: 'list',
        name: 'SalaryList',
        component: () => import('@/views/salary/SalaryList.vue'),
        meta: { title: '薪资管理', icon: 'Money', requiresAuth: true }
      }
    ]
  },
  {
    path: '/attendances',
    component: Layout,
    redirect: '/attendances/list',
    children: [
      {
        path: 'list',
        name: 'AttendanceList',
        component: () => import('@/views/attendance/AttendanceList.vue'),
        meta: { title: '考勤管理', icon: 'Clock', requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 企业人事管理系统`
  }
  
  if (to.meta.requiresAuth !== false && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
