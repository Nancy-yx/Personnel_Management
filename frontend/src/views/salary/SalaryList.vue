<template>
  <div class="salary-list">
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
        <el-form-item label="薪资月份">
          <el-date-picker
            v-model="searchForm.month"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待核算" :value="0" />
            <el-option label="已核算" :value="1" />
            <el-option label="已发放" :value="2" />
          </el-select>
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
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <div class="stat-card total-salary">
          <div class="stat-title">{{ currentMonth }} 薪资总额</div>
          <div class="stat-value">¥{{ totalSalary }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card avg-salary">
          <div class="stat-title">平均薪资</div>
          <div class="stat-value">¥{{ avgSalary }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card count-info">
          <div class="stat-title">发放人数</div>
          <div class="stat-value">{{ salaryCount }} 人</div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 操作工具栏 -->
    <div class="table-toolbar card-container">
      <div class="toolbar-title">薪资列表</div>
      <div class="toolbar-actions">
        <el-button type="success" @click="handleCalculate">
          <el-icon><Calculator /></el-icon>核算当月薪资
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增薪资
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
        <el-table-column prop="month" label="月份" width="100" />
        <el-table-column prop="baseSalary" label="基本工资" width="100">
          <template #default="{ row }">¥{{ row.baseSalary?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="performanceBonus" label="绩效" width="100">
          <template #default="{ row }">¥{{ row.performanceBonus?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="grossSalary" label="应发工资" width="110">
          <template #default="{ row }">
            <span class="amount">¥{{ row.grossSalary?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="netSalary" label="实发工资" width="110">
          <template #default="{ row }">
            <span class="amount highlight">¥{{ row.netSalary?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px" :disabled="isView">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工" prop="employeeId">
              <el-select v-model="formData.employeeId" placeholder="请选择员工" filterable style="width: 100%" @change="handleEmployeeChange">
                <el-option v-for="emp in employeeOptions" :key="emp.id" :label="`${emp.name} (${emp.employeeNo})`" :value="emp.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月份" prop="month">
              <el-date-picker v-model="formData.month" type="month" placeholder="选择月份" value-format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资" prop="baseSalary">
              <el-input-number v-model="formData.baseSalary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绩效奖金">
              <el-input-number v-model="formData.performanceBonus" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位津贴">
              <el-input-number v-model="formData.positionAllowance" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交通补贴">
              <el-input-number v-model="formData.transportAllowance" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通讯补贴">
              <el-input-number v-model="formData.communicationAllowance" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="其他补贴">
              <el-input-number v-model="formData.otherAllowance" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="其他扣款">
              <el-input-number v-model="formData.otherDeduction" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="formData.status" style="width: 100%">
                <el-option label="待核算" :value="0" />
                <el-option label="已核算" :value="1" />
                <el-option label="已发放" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 自动计算显示 -->
        <div class="calc-result" v-if="formData.employeeId">
          <el-divider content-position="left">自动计算结果</el-divider>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="应发工资">¥{{ calcResult.grossSalary }}</el-descriptions-item>
            <el-descriptions-item label="社保(10%)">¥{{ calcResult.socialInsurance }}</el-descriptions-item>
            <el-descriptions-item label="公积金(12%)">¥{{ calcResult.housingFund }}</el-descriptions-item>
            <el-descriptions-item label="个税">¥{{ calcResult.personalTax }}</el-descriptions-item>
            <el-descriptions-item label="实发工资">
              <span class="amount highlight">¥{{ calcResult.netSalary }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { getSalaryList, addSalary, updateSalary, deleteSalary, batchDeleteSalaries, calculateMonthlySalary } from '@/api/salary'
import { getAllDepartments } from '@/api/department'
import { getEmployeeList } from '@/api/employee'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)
const isView = ref(false)

const now = new Date()
const currentMonth = ref(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`)

const searchForm = reactive({
  keyword: '',
  departmentId: null,
  month: currentMonth.value,
  status: null,
  pageNum: 1,
  pageSize: 10
})

const departmentOptions = ref([])
const employeeOptions = ref([])

const formData = reactive({
  id: null,
  employeeId: null,
  month: currentMonth.value,
  baseSalary: 0,
  performanceBonus: 0,
  positionAllowance: 0,
  transportAllowance: 0,
  communicationAllowance: 0,
  otherAllowance: 0,
  otherDeduction: 0,
  status: 0,
  remark: ''
})

const formRules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }]
}

const calcResult = computed(() => {
  const base = Number(formData.baseSalary) || 0
  const perf = Number(formData.performanceBonus) || 0
  const pos = Number(formData.positionAllowance) || 0
  const trans = Number(formData.transportAllowance) || 0
  const comm = Number(formData.communicationAllowance) || 0
  const other = Number(formData.otherAllowance) || 0
  const ded = Number(formData.otherDeduction) || 0
  
  const gross = base + perf + pos + trans + comm + other
  const social = (gross * 0.10).toFixed(2)
  const housing = (gross * 0.12).toFixed(2)
  
  let taxable = gross - 5000
  let tax = 0
  if (taxable > 0) {
    if (taxable <= 3000) tax = taxable * 0.03
    else if (taxable <= 12000) tax = taxable * 0.10 - 252
    else if (taxable <= 25000) tax = taxable * 0.20 - 1692
    else tax = taxable * 0.25 - 3192
  }
  tax = tax.toFixed(2)
  
  const net = (gross - Number(social) - Number(housing) - Number(tax) - ded).toFixed(2)
  
  return {
    grossSalary: gross.toFixed(2),
    socialInsurance: social,
    housingFund: housing,
    personalTax: tax,
    netSalary: net
  }
})

const totalSalary = computed(() => {
  return tableData.value.reduce((sum, item) => sum + (item.netSalary || 0), 0).toFixed(2)
})

const salaryCount = computed(() => tableData.value.length)
const avgSalary = computed(() => {
  if (tableData.value.length === 0) return '0.00'
  return (totalSalary.value / tableData.value.length).toFixed(2)
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
    const res = await getSalaryList(searchForm)
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
  searchForm.month = currentMonth.value
  searchForm.status = null
  searchForm.pageNum = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const resetForm = () => {
  Object.assign(formData, {
    id: null, employeeId: null, month: currentMonth.value,
    baseSalary: 0, performanceBonus: 0, positionAllowance: 0,
    transportAllowance: 0, communicationAllowance: 0, otherAllowance: 0,
    otherDeduction: 0, status: 0, remark: ''
  })
}

const handleAdd = () => {
  isEdit.value = false
  isView.value = false
  dialogTitle.value = '新增薪资记录'
  resetForm()
  dialogVisible.value = true
}

const handleView = (row) => {
  isEdit.value = false
  isView.value = true
  dialogTitle.value = '查看薪资详情'
  resetForm()
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  isView.value = false
  dialogTitle.value = '编辑薪资记录'
  resetForm()
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 选择员工后自动填充基本工资
const handleEmployeeChange = (empId) => {
  if (!isEdit.value && !isView.value && empId) {
    const emp = employeeOptions.value.find(e => e.id === empId)
    if (emp && emp.baseSalary) {
      formData.baseSalary = emp.baseSalary
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    // 将计算结果合并到提交数据中
    const submitData = {
      ...formData,
      grossSalary: Number(calcResult.value.grossSalary),
      socialInsurance: Number(calcResult.value.socialInsurance),
      housingFund: Number(calcResult.value.housingFund),
      personalTax: Number(calcResult.value.personalTax),
      netSalary: Number(calcResult.value.netSalary)
    }
    if (isEdit.value) {
      await updateSalary(submitData.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await addSalary(submitData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 校验失败由 Element-Plus 内联显示；API 失败由 request.js 拦截器已弹提示
    console.error('薪资提交失败:', e)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除 ${row.employeeName} 的薪资记录吗？`, '删除确认', { type: 'warning' })
    await deleteSalary(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '批量删除', { type: 'warning' })
    const ids = selectedRows.value.map(r => r.id)
    await batchDeleteSalaries(ids)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const handleCalculate = async () => {
  try {
    await ElMessageBox.confirm(`确定要核算 ${currentMonth.value} 月份的员工薪资吗？`, '核算确认', { type: 'warning' })
    await calculateMonthlySalary(currentMonth.value)
    ElMessage.success('核算成功')
    loadData()
  } catch {}
}

const getStatusType = (status) => ({ 0: 'warning', 1: 'success', 2: 'info' }[status] || 'info')
const getStatusText = (status) => ({ 0: '待核算', 1: '已核算', 2: '已发放' }[status] || '未知')

onMounted(() => {
  loadDepartments()
  loadEmployees()
  loadData()
})
</script>

<style scoped lang="scss">
.salary-list {
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    
    .stat-title {
      font-size: 14px;
      opacity: 0.9;
    }
    
    .stat-value {
      font-size: 28px;
      font-weight: 600;
      margin-top: 8px;
    }
    
    &.total-salary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    &.avg-salary {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }
    
    &.count-info {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
  }
  
  .toolbar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  
  .amount {
    font-weight: 500;
    
    &.highlight {
      color: #F56C6C;
      font-weight: 600;
    }
  }
  
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
  
  .calc-result {
    margin-top: 16px;
  }
}
</style>
