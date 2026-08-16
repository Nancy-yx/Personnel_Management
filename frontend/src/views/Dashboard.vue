<template>
  <div class="dashboard-container">
    <!-- 时间筛选 -->
    <div class="time-filter card-container">
      <span class="filter-label">统计周期：</span>
      <el-radio-group v-model="timeRange" @change="loadData">
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="quarter">本季度</el-radio-button>
        <el-radio-button label="year">本年度</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 数据卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="4" v-for="card in statCards" :key="card.key">
        <div class="stat-card" :class="card.color" @click="handleCardClick(card)">
          <div class="card-icon">
            <el-icon :size="32"><component :is="card.icon" /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ card.value }}</div>
            <div class="card-label">{{ card.label }}</div>
          </div>
          <div class="card-trend" v-if="card.trend">
            <el-icon><Top /></el-icon>
            <span>{{ card.trend }}%</span>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 员工状态分布 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card card-container">
          <div class="chart-header">
            <h3>员工状态分布</h3>
          </div>
          <v-chart class="chart" :option="statusChartOption" autoresize />
        </div>
      </el-col>
      
      <!-- 部门员工分布 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card card-container">
          <div class="chart-header">
            <h3>部门员工分布</h3>
          </div>
          <v-chart class="chart" :option="departmentChartOption" autoresize />
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <!-- 薪资趋势 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card card-container">
          <div class="chart-header">
            <h3>薪资趋势（近6个月）</h3>
          </div>
          <v-chart class="chart" :option="salaryTrendOption" autoresize />
        </div>
      </el-col>
      
      <!-- 考勤统计 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card card-container">
          <div class="chart-header">
            <h3>当月考勤统计</h3>
          </div>
          <v-chart class="chart" :option="attendanceChartOption" autoresize />
        </div>
      </el-col>
    </el-row>
    
    <!-- 异常考勤列表 -->
    <div class="card-container abnormal-list">
      <div class="chart-header">
        <h3>最近异常考勤</h3>
        <el-link type="primary" @click="$router.push('/attendances/list')">查看全部</el-link>
      </div>
      <el-table :data="abnormalRecords" stripe style="width: 100%">
        <el-table-column prop="employeeName" label="员工姓名" width="120" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="attendanceDate" label="考勤日期" width="120" />
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100">
          <template #default="{ row }">
            {{ row.duration || '-' }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!abnormalRecords.length" description="暂无异常考勤" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart, RadarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { getOverview } from '@/api/dashboard'

use([
  CanvasRenderer,
  PieChart,
  BarChart,
  LineChart,
  RadarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const router = useRouter()
const timeRange = ref('month')
const dashboardData = ref({})
const loading = ref(false)

// 统计卡片配置
const statCards = computed(() => [
  {
    key: 'total',
    label: '员工总数',
    value: dashboardData.value.totalEmployees || 0,
    icon: 'User',
    color: 'blue',
    path: '/employees/list'
  },
  {
    key: 'active',
    label: '在职人数',
    value: dashboardData.value.activeEmployees || 0,
    icon: 'CircleCheck',
    color: 'green',
    path: '/employees/list'
  },
  {
    key: 'resigned',
    label: '离职人数',
    value: dashboardData.value.resignedEmployees || 0,
    icon: 'CircleClose',
    color: 'orange',
    path: '/employees/list'
  },
  {
    key: 'probation',
    label: '试用人员',
    value: dashboardData.value.probationEmployees || 0,
    icon: 'Loading',
    color: 'purple',
    path: '/employees/list'
  },
  {
    key: 'salary',
    label: '当月薪资总额',
    value: formatMoney(dashboardData.value.currentMonthSalary),
    icon: 'Money',
    color: 'red',
    path: '/salaries/list'
  },
  {
    key: 'abnormal',
    label: '考勤异常',
    value: getAbnormalCount(),
    icon: 'Warning',
    color: 'yellow',
    path: '/attendances/list'
  }
])

const getAbnormalCount = () => {
  const stats = dashboardData.value.attendanceStats
  if (!stats) return 0
  return (stats.lateCount || 0) + (stats.earlyCount || 0) + (stats.absentCount || 0)
}

const handleCardClick = (card) => {
  if (card.path) {
    router.push(card.path)
  }
}

// 异常考勤记录
const abnormalRecords = computed(() => dashboardData.value.abnormalRecords || [])

// 员工状态分布图表
const statusChartOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
    label: { show: false },
    emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
    data: [
      { value: dashboardData.value.activeEmployees || 0, name: '在职', itemStyle: { color: '#67C23A' } },
      { value: dashboardData.value.probationEmployees || 0, name: '试用', itemStyle: { color: '#E6A23C' } },
      { value: dashboardData.value.resignedEmployees || 0, name: '离职', itemStyle: { color: '#909399' } }
    ]
  }]
}))

// 部门员工分布图表
const departmentChartOption = computed(() => {
  const deptData = dashboardData.value.departmentDistribution || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: deptData.map(d => d.department_name), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: '人数' },
    series: [{
      type: 'bar',
      data: deptData.map(d => d.count),
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' }
          ]
        },
        borderRadius: [4, 4, 0, 0]
      },
      barWidth: '50%'
    }]
  }
})

// 薪资趋势图表
const salaryTrendOption = computed(() => {
  const trend = dashboardData.value.salaryTrend || []
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: trend.map(t => t.month) },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [{
      type: 'line',
      data: trend.map(t => t.total),
      smooth: true,
      itemStyle: { color: '#67C23A' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ]
        }
      }
    }]
  }
})

// 考勤统计图表
const attendanceChartOption = computed(() => {
  const stats = dashboardData.value.attendanceStats || {}
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: '0' },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      data: [
        { value: stats.normalCount || 0, name: '正常', itemStyle: { color: '#67C23A' } },
        { value: stats.lateCount || 0, name: '迟到', itemStyle: { color: '#E6A23C' } },
        { value: stats.earlyCount || 0, name: '早退', itemStyle: { color: '#F56C6C' } },
        { value: stats.absentCount || 0, name: '旷工', itemStyle: { color: '#8B0000' } },
        { value: stats.leaveCount || 0, name: '请假', itemStyle: { color: '#409EFF' } },
        { value: stats.overtimeCount || 0, name: '加班', itemStyle: { color: '#9B59B6' } }
      ]
    }]
  }
})

const formatMoney = (val) => {
  if (!val) return '¥0'
  return '¥' + Number(val).toLocaleString()
}

const getStatusType = (status) => {
  const map = { normal: 'success', late: 'warning', early: 'danger', absent: 'danger', leave: 'info', overtime: '' }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOverview()
    dashboardData.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .time-filter {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .filter-label {
      font-size: 14px;
      color: #606266;
    }
  }
  
  .stat-cards {
    margin-bottom: 20px;
  }
  
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    position: relative;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    .card-content {
      flex: 1;
      
      .card-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
      }
      
      .card-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
    
    .card-trend {
      position: absolute;
      top: 12px;
      right: 12px;
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #67C23A;
    }
    
    &.blue .card-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    &.green .card-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
    &.orange .card-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
    &.purple .card-icon { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); color: #606266; }
    &.red .card-icon { background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); }
    &.yellow .card-icon { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .chart-card {
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .chart {
      height: 320px;
    }
  }
  
  .abnormal-list {
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
}
</style>
