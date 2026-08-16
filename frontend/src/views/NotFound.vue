<template>
  <div class="not-found">
    <div class="content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.not-found {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  
  .content {
    text-align: center;
    
    .error-code {
      font-size: 120px;
      font-weight: bold;
      color: #409EFF;
      margin-bottom: 20px;
    }
    
    .error-message {
      font-size: 18px;
      color: #606266;
      margin-bottom: 30px;
    }
  }
}
</style>
