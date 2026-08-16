<template>
  <div class="attendance-list">
    <!-- 搜索筛选 -->
    <div class="search-form card-container">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/工号" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.departmentId" placeholder="全部部门" clearable>
            <el-option v-for="dept in departmentOptions" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="正常" value="normal" />
            <el-option label="迟到" value="late" />
            <el-option label="早退" value="early" />
            <el-option label="旷工" value="absent" />
            <el-option label="请假" value="leave" />
            <el-option label="加班" value="overtime" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 考勤统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="8" :sm="4" v-for="stat in attendanceStats" :key="stat.key">
        <div class="stat-card" :class="stat.type">
          <div class="stat-icon">
            <el-icon :size="24"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 操作工具栏 -->
    <div class="table-toolbar card-container">
      <div class="toolbar-title">考勤记录</div>
      <div class="toolbar-actions">
        <el-button type="warning" @click="handleSupplementary">
          <el-icon><EditPen /></el-icon>手动补录
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增考勤
        </el-button>
        <el-button :disabled="selectedRows.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="card-container">
      <el-table :data="tableData" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="attendanceDate" label="考勤日期" width="110" />
        <el-table-column prop="checkInTime" label="上班打卡" width="100">
          <template #default="{ row }">{{ row.checkInTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="下班打卡" width="100">
          <template #default="{ row }">{{ row.checkOutTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.status)" 
              :class="{ 'abnormal-tag': isAbnormal(row.status) }"
            >
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="90">
          <template #default="{ row }">
            <span :class="{ 'abnormal-text': row.duration > 0 }">{{ row.duration || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isSupplementary" label="补录" width="70">
          <template #default="{ row }">
            <el-tag v-if="row.isSupplementary" type="warning" size="small">补录</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="searchForm.pageNum"
          v-model:page-size="searchForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="员工" prop="employeeId">
          <el-select v-model="formData.employeeId" placeholder="请选择员工" filterable style="width: 100%">
            <el-option v-for="emp in employeeOptions" :key="emp.id" :label="`${emp.name} (${emp.employeeNo})`" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤日期" prop="attendanceDate">
          <el-date-picker v-model="formData.attendanceDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="上班打卡">
          <el-time-picker v-model="formData.checkInTime" placeholder="选择时间" format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="下班打卡">
          <el-time-picker v-model="formData.checkOutTime" placeholder="选择时间" format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" value="normal" />
            <el-option label="迟到" value="late" />
            <el-option label="早退" value="early" />
            <el-option label="旷工" value="absent" />
            <el-option label="请假" value="leave" />
            <el-option label="加班" value="overtime" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getAttendanceList, addAttendance, updateAttendance, deleteAttendance, batchDeleteAttendances, supplementaryAttendance } from '@/api/attendance'
import { getAllDepartments } from '@/api/department'
import { getEmployeeList } from '@/api/employee'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)
const isSupplementary = ref(false)

const now = new Date()
const firstDay = dayjs(now).startOf('month').format('YYYY-MM-DD')
const lastDay = dayjs(now).endOf('month').format('YYYY-MM-DD')

const searchForm = reactive({
  keyword: '',
  departmentId: null,
  status: '',
  dateRange: [firstDay, lastDay],
  startDate: firstDay,
  endDate: lastDay,
  pageNum: 1,
  pageSize: 10
})

const departmentOptions = ref([])
const employeeOptions = ref([])

const formData = reactive({
  id: null,
  employeeId: null,
  attendanceDate: dayjs().format('YYYY-MM-DD'),
  checkInTime: null,
  checkOutTime: null,
  status: 'normal',
  remark: ''
})

const formRules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  attendanceDate: [{ required: true, message: '请选择考勤日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const attendanceStats = computed(() => {
  const data = tableData.value
  return [
    { key: 'normal', label: '正常', value: data.filter(d => d.status === 'normal').length, icon: 'CircleCheck', type: 'success' },
    { key: 'late', label: '迟到', value: data.filter(d => d.status === 'late').length, icon: 'Clock', type: 'warning' },
    { key: 'early', label: '早退', value: data.filter(d => d.status === 'early').length, icon: 'Bell', type: 'danger' },
    { key: 'absent', label: '旷工', value: data.filter(d => d.status === 'absent').length, icon: 'Warning', type: 'danger' },
    { key: 'leave', label: '请假', value: data.filter(d => d.status === 'leave').length, icon: 'Document', type: 'info' },
    { key: 'overtime', label: '加班', value: data.filter(d => d.status === 'overtime').length, icon: 'Sunny', type: 'primary' }
  ]
})

const loadDepartments = async () => {
  try {
    const res = await getAllDepartments()
    departmentOptions.value = flattenTree(res.data)
  } catch (e) {
    console.error(e)
  }
}

const loadEmployees = async () => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    employeeOptions.value = res.data.records
  } catch (e) {
    console.error(e)
  }
}

const flattenTree = (tree) => {
  let result = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      result.push(node)
      if (node.children?.length) traverse(node.children)
    })
  }
  traverse(tree)
  return result
}

const loadData = async () => {
  loading.value = true
  try {
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      searchForm.startDate = searchForm.dateRange[0]
      searchForm.endDate = searchForm.dateRange[1]
    }
    
    const res = await getAttendanceList(searchForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.departmentId = null
  searchForm.status = ''
  searchForm.dateRange = [firstDay, lastDay]
  searchForm.startDate = firstDay
  searchForm.endDate = lastDay
  searchForm.pageNum = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    employeeId: null,
    attendanceDate: dayjs().format('YYYY-MM-DD'),
    checkInTime: null,
    checkOutTime: null,
    status: 'normal',
    remark: ''
  })
}

const handleAdd = () => {
  isEdit.value = false
  isSupplementary.value = false
  dialogTitle.value = '新增考勤记录'
  resetForm()
  dialogVisible.value = true
}

const handleSupplementary = () => {
  isEdit.value = false
  isSupplementary.value = true
  dialogTitle.value = '手动补录考勤'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  isSupplementary.value = false
  dialogTitle.value = '编辑考勤记录'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isSupplementary.value) {
      await supplementaryAttendance(formData)
      ElMessage.success('补录成功')
    } else if (isEdit.value) {
      await updateAttendance(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addAttendance(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除该考勤记录吗？`, '删除确认', { type: 'warning' })
    await deleteAttendance(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '批量删除', { type: 'warning' })
    const ids = selectedRows.value.map(r => r.id)
    await batchDeleteAttendances(ids)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const getStatusType = (status) => ({ normal: 'success', late: 'warning', early: 'danger', absent: 'danger', leave: 'info', overtime: '' }[status] || 'info')
const isAbnormal = (status) => ['late', 'early', 'absent'].includes(status)

onMounted(() => {
  loadDepartments()
  loadEmployees()
  loadData()
})
</script>

<style scoped lang="scss">
.attendance-list {
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    
    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }
      
      .stat-label {
        font-size: 12px;
        color: #909399;
        margin-top: 2px;
      }
    }
    
    &.success .stat-icon { background: #67C23A; }
    &.warning .stat-icon { background: #E6A23C; }
    &.danger .stat-icon { background: #F56C6C; }
    &.info .stat-icon { background: #909399; }
    &.primary .stat-icon { background: #409EFF; }
  }
  
  .toolbar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  
  .abnormal-tag {
    animation: blink 1.5s infinite;
  }
  
  .abnormal-text {
    color: #F56C6C;
    font-weight: 600;
  }
  
  @keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.6; }
  }
  
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
