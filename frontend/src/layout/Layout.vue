<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo-area">
        <img src="/logo.svg" alt="logo" class="logo-icon" v-if="!isCollapse" />
        <span class="logo-text" v-if="!isCollapse">人事管理系统</span>
        <span class="logo-mini" v-else>HR</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#ffffffb3"
        active-text-color="#ffffff"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页总览</template>
        </el-menu-item>
        
        <el-sub-menu index="employee">
          <template #title>
            <el-icon><User /></el-icon>
            <span>员工管理</span>
          </template>
          <el-menu-item index="/employees/list">员工列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="department">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </template>
          <el-menu-item index="/departments/list">部门列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="salary">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>薪资管理</span>
          </template>
          <el-menu-item index="/salaries/list">薪资列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="attendance">
          <template #title>
            <el-icon><Clock /></el-icon>
            <span>考勤管理</span>
          </template>
          <el-menu-item index="/attendances/list">考勤列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title && $route.path !== '/dashboard'">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :style="{ backgroundColor: '#409EFF' }">
                {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.realName || '用户' }}</span>
              <el-tag :type="userStore.isAdmin ? 'danger' : 'info'" size="small" effect="dark">
                {{ userStore.isAdmin ? '管理员' : '普通用户' }}
              </el-tag>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { updatePasswordApi } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        type: 'warning'
      })
      userStore.logout()
      router.push('/login')
    } catch {}
  } else if (command === 'password') {
    passwordDialogVisible.value = true
  } else if (command === 'profile') {
    ElMessage.info('个人信息页面开发中')
  }
}

// 修改密码
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleUpdatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await updatePasswordApi(passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch {}
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #001529;
  transition: width 0.3s;
  overflow: hidden;
  
  .logo-area {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid #ffffff1a;
    
    .logo-icon {
      width: 32px;
      height: 32px;
    }
    
    .logo-text {
      margin-left: 10px;
      white-space: nowrap;
    }
    
    .logo-mini {
      font-size: 16px;
    }
  }
  
  .sidebar-menu {
    border-right: none;
    
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 50px;
      line-height: 50px;
    }
    
    :deep(.el-menu-item:hover),
    :deep(.el-sub-menu__title:hover) {
      background-color: #ffffff1a !important;
    }
    
    :deep(.el-menu-item.is-active) {
      background-color: #1890ff !important;
    }
  }
}

.header {
  background: #fff;
  border-bottom: 1px solid #e6e8eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }
  
  .collapse-btn {
    font-size: 20px;
    cursor: pointer;
    color: #606266;
    
    &:hover {
      color: #409EFF;
    }
  }
  
  .breadcrumb {
    font-size: 14px;
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      padding: 4px 12px;
      border-radius: 4px;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: #f5f5f5;
      }
      
      .username {
        font-size: 14px;
        color: #303133;
      }
    }
  }
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
