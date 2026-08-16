package com.personnel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personnel.common.BusinessException;
import com.personnel.common.ErrorCode;
import com.personnel.entity.Department;
import com.personnel.entity.Employee;
import com.personnel.mapper.DepartmentMapper;
import com.personnel.mapper.EmployeeMapper;
import com.personnel.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final EmployeeMapper employeeMapper;

    @Override
    public List<Map<String, Object>> getDepartmentTree() {
        List<Department> allDepartments = this.lambdaQuery().orderByAsc(Department::getSort).list();
        // 找出顶级部门
        List<Department> rootDepartments = allDepartments.stream()
                .filter(d -> d.getParentId() == null || d.getParentId() == 0)
                .collect(Collectors.toList());
        // 递归构建树
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Department root : rootDepartments) {
            tree.add(buildTreeNode(root, allDepartments));
        }
        return tree;
    }

    /**
     * 递归构建部门树节点
     */
    private Map<String, Object> buildTreeNode(Department department, List<Department> allDepartments) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", department.getId());
        node.put("name", department.getName());
        node.put("parentId", department.getParentId());
        node.put("code", department.getCode());
        node.put("leader", department.getLeader());
        node.put("phone", department.getPhone());
        node.put("email", department.getEmail());
        node.put("sort", department.getSort());
        node.put("status", department.getStatus());
        node.put("remark", department.getRemark());
        node.put("createTime", department.getCreateTime());

        // 查找子部门
        List<Map<String, Object>> children = new ArrayList<>();
        for (Department child : allDepartments) {
            if (child.getParentId() != null && child.getParentId().equals(department.getId())) {
                children.add(buildTreeNode(child, allDepartments));
            }
        }
        node.put("children", children);
        return node;
    }

    @Override
    public List<Department> getList() {
        return this.lambdaQuery().orderByAsc(Department::getSort).list();
    }

    @Override
    public List<Department> getAll() {
        return this.lambdaQuery().eq(Department::getStatus, 1).orderByAsc(Department::getSort).list();
    }

    @Override
    public Department addDepartment(Department department) {
        this.save(department);
        return department;
    }

    @Override
    public Department updateDepartment(Department department) {
        if (this.getById(department.getId()) == null) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_EXIST);
        }
        this.updateById(department);
        return department;
    }

    @Override
    public void deleteDepartment(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_EXIST);
        }
        // 检查是否有子部门
        long childCount = this.lambdaQuery().eq(Department::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.DEPARTMENT_HAS_CHILDREN);
        }
        // 检查是否有员工
        long employeeCount = employeeMapper.selectByDepartmentId(id).size();
        if (employeeCount > 0) {
            throw new BusinessException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
        }
        this.removeById(id);
    }

    @Override
    public List<Employee> getDepartmentEmployees(Long id) {
        return employeeMapper.selectByDepartmentId(id);
    }
}
