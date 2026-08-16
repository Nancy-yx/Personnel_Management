<template>
  <div class="department-list">
    <el-row :gutter="20">
      <!-- 左侧树形结构 -->
      <el-col :span="8">
        <div class="card-container tree-panel">
          <div class="panel-header">
            <span class="panel-title">部门结构</span>
            <el-button type="primary" size="small" @click="handleAdd(0)">
              <el-icon><Plus /></el-icon>新增根部门
            </el-button>
          </div>
          <el-input
            v-model="filterText"
            placeholder="搜索部门"
            size="small"
            clearable
            style="margin-bottom: 12px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="{ label: 'name', children: 'children' }"
            :filter-node-method="filterNode"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span class="node-label">{{ data.name }}</span>
                <span class="node-info" v-if="data.employeeCount">
                  <el-tag size="small" type="info">{{ data.employeeCount }}人</el-tag>
                </span>
              </span>
            </template>
          </el-tree>
        </div>
      </el-col>
      
      <!-- 右侧详情 -->
      <el-col :span="16">
        <!-- 部门详情/编辑 -->
        <div class="card-container" v-if="selectedDepartment">
          <div class="panel-header">
            <span class="panel-title">部门详情</span>
            <div>
              <el-button size="small" @click="handleAdd(selectedDepartment.id)">
                <el-icon><Plus /></el-icon>新增子部门
              </el-button>
              <el-button type="primary" size="small" @click="handleEdit(selectedDepartment)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(selectedDepartment)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </div>
          </div>
          
          <el-descriptions :column="2" border v-if="!isEditing">
            <el-descriptions-item label="部门名称">{{ selectedDepartment.name }}</el-descriptions-item>
            <el-descriptions-item label="部门编码">{{ selectedDepartment.code || '-' }}</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ selectedDepartment.leader || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ selectedDepartment.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ selectedDepartment.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="员工数量">
              <el-tag>{{ selectedDepartment.employeeCount || 0 }} 人</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="selectedDepartment.status === 1 ? 'success' : 'danger'">
                {{ selectedDepartment.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="排序">{{ selectedDepartment.sort || 0 }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ selectedDepartment.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
          
          <el-form :model="editForm" :rules="formRules" ref="formRef" label-width="100px" v-else>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="部门名称" prop="name">
                  <el-input v-model="editForm.name" placeholder="请输入部门名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="部门编码">
                  <el-input v-model="editForm.code" placeholder="请输入部门编码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="负责人">
                  <el-input v-model="editForm.leader" placeholder="请输入负责人" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话">
                  <el-input v-model="editForm.phone" placeholder="请输入联系电话" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="editForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="排序">
                  <el-input-number v-model="editForm.sort" :min="0" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注">
                  <el-input v-model="editForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" @click="handleSubmit">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 部门员工列表 -->
        <div class="card-container" v-if="selectedDepartment">
          <div class="panel-header">
            <span class="panel-title">部门员工</span>
          </div>
          <el-table :data="deptEmployees" stripe v-loading="employeesLoading">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="employeeNo" label="工号" width="120" />
            <el-table-column prop="position" label="职位" width="120" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'warning'">
                  {{ row.status === 1 ? '在职' : row.status === 2 ? '离职' : '试用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!deptEmployees.length && !employeesLoading" description="该部门暂无员工" />
        </div>
        
        <div class="card-container" v-else>
          <el-empty description="请从左侧选择一个部门" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { getDepartmentTree, addDepartment, updateDepartment, deleteDepartment, getDepartmentEmployees } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'

const treeRef = ref(null)
const filterText = ref('')
const treeData = ref([])
const selectedDepartment = ref(null)
const isEditing = ref(false)
const formRef = ref(null)
const isNew = ref(false)
const employeesLoading = ref(false)
const deptEmployees = ref([])

const editForm = reactive({
  id: null,
  name: '',
  parentId: null,
  code: '',
  leader: '',
  phone: '',
  email: '',
  sort: 0,
  status: 1,
  remark: ''
})

const formRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

const loadDepartmentTree = async () => {
  try {
    const res = await getDepartmentTree()
    treeData.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleNodeClick = async (data) => {
  selectedDepartment.value = data
  isEditing.value = false
  loadDeptEmployees(data.id)
}

const loadDeptEmployees = async (deptId) => {
  employeesLoading.value = true
  try {
    const res = await getDepartmentEmployees(deptId)
    deptEmployees.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    employeesLoading.value = false
  }
}

const resetEditForm = () => {
  Object.assign(editForm, {
    id: null,
    name: '',
    parentId: null,
    code: '',
    leader: '',
    phone: '',
    email: '',
    sort: 0,
    status: 1,
    remark: ''
  })
}

const handleAdd = (parentId) => {
  isNew.value = true
  isEditing.value = true
  resetEditForm()
  editForm.parentId = parentId
  selectedDepartment.value = null
}

const handleEdit = (row) => {
  isNew.value = false
  isEditing.value = true
  Object.assign(editForm, row)
}

const cancelEdit = () => {
  isEditing.value = false
  if (!isNew.value) {
    // 恢复选中状态
    const id = selectedDepartment.value?.id
    if (id) {
      selectedDepartment.value = treeData.value[0] // 重新加载
      loadDepartmentTree()
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isNew.value) {
      await addDepartment(editForm)
      ElMessage.success('新增成功')
    } else {
      await updateDepartment(editForm.id, editForm)
      ElMessage.success('更新成功')
    }
    isEditing.value = false
    loadDepartmentTree()
    // 如果有选中的部门，重新加载员工列表
    if (editForm.id) {
      selectedDepartment.value = { ...editForm }
      loadDeptEmployees(editForm.id)
    }
  } catch {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除部门 "${row.name}" 吗？`, '删除确认', { type: 'warning' })
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    selectedDepartment.value = null
    loadDepartmentTree()
  } catch {}
}

onMounted(() => {
  loadDepartmentTree()
})
</script>

<style scoped lang="scss">
.department-list {
  .tree-panel {
    height: calc(100vh - 140px);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    
    .el-tree {
      flex: 1;
      overflow-y: auto;
    }
    
    .tree-node {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      
      .node-label {
        flex: 1;
      }
    }
  }
  
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
    
    .panel-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
    
    .el-button {
      margin-left: 8px;
    }
  }
}
</style>
