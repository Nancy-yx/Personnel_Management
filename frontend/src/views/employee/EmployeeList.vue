<template>
  <div class="employee-list">
    <!-- 搜索筛选 -->
    <div class="search-form card-container">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/工号/手机号" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.departmentId" placeholder="全部部门" clearable>
            <el-option v-for="dept in departmentOptions" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="2" />
            <el-option label="试用" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期">
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
    
    <!-- 操作工具栏 -->
    <div class="table-toolbar card-container">
      <div class="toolbar-title">员工列表</div>
      <div class="toolbar-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增员工
        </el-button>
        <el-button :disabled="selectedRows.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="card-container">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">{{ row.name }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="genderText" label="性别" width="60" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="hireDate" label="入职日期" width="110" />
        <el-table-column prop="statusText" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
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
      
      <!-- 分页 -->
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
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="formData.employeeNo" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-select v-model="formData.departmentId" placeholder="请选择部门" style="width: 100%">
                <el-option v-for="dept in flatDepartments" :key="dept.id" :label="dept.name" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位">
              <el-input v-model="formData.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职级">
              <el-input v-model="formData.level" placeholder="请输入职级" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker v-model="formData.hireDate" type="date" placeholder="请选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="2" />
                <el-option label="试用" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资">
              <el-input-number v-model="formData.baseSalary" :min="0" :precision="2" :step="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址">
              <el-input v-model="formData.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { getEmployeeList, addEmployee, updateEmployee, deleteEmployee, batchDeleteEmployees } from '@/api/employee'
import { getAllDepartments } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = reactive({
  keyword: '',
  departmentId: null,
  status: null,
  dateRange: null,
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

const departmentOptions = ref([])
const flatDepartments = computed(() => flattenTree(departmentOptions.value))

const flattenTree = (tree) => {
  let result = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      result.push(node)
      if (node.children && node.children.length) {
        traverse(node.children)
      }
    })
  }
  traverse(tree)
  return result
}

const formData = reactive({
  id: null,
  employeeNo: '',
  name: '',
  gender: 1,
  phone: '',
  email: '',
  departmentId: null,
  position: '',
  level: '',
  hireDate: '',
  status: 3,
  baseSalary: 0,
  idCard: '',
  address: '',
  remark: ''
})

const formRules = {
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadDepartments = async () => {
  try {
    const res = await getAllDepartments()
    departmentOptions.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      searchForm.startDate = searchForm.dateRange[0]
      searchForm.endDate = searchForm.dateRange[1]
    } else {
      searchForm.startDate = ''
      searchForm.endDate = ''
    }
    
    const res = await getEmployeeList(searchForm)
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
  searchForm.status = null
  searchForm.dateRange = null
  searchForm.pageNum = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    employeeNo: '',
    name: '',
    gender: 1,
    phone: '',
    email: '',
    departmentId: null,
    position: '',
    level: '',
    hireDate: '',
    status: 3,
    baseSalary: 0,
    idCard: '',
    address: '',
    remark: ''
  })
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增员工'
  resetForm()
  dialogVisible.value = true
}

const handleView = (row) => {
  isEdit.value = true
  dialogTitle.value = '查看员工详情'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑员工'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateEmployee(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await addEmployee(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除员工 ${row.name} 吗？`, '删除确认', { type: 'warning' })
    await deleteEmployee(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条记录吗？`, '批量删除', { type: 'warning' })
    const ids = selectedRows.value.map(r => r.id)
    await batchDeleteEmployees(ids)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const getStatusType = (status) => {
  const map = { 1: 'success', 2: 'info', 3: 'warning' }
  return map[status] || 'info'
}

onMounted(() => {
  loadDepartments()
  loadData()
})
</script>

<style scoped lang="scss">
.employee-list {
  .toolbar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
