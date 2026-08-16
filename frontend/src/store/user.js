import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, getUserInfoApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    localStorage.setItem('token', res.data.token)
    return res.data
  }
  
  async function getUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
      return res.data
    } catch (e) {
      logout()
    }
  }
  
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    getUserInfo,
    logout
  }
})
