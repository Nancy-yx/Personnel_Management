package com.personnel.controller;

import com.personnel.common.Result;
import com.personnel.service.AttendanceService;
import com.personnel.service.EmployeeService;
import com.personnel.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final SalaryService salaryService;
    
    /**
     * 获取仪表盘数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        
        // 员工数据
        Map<String, Object> employeeData = employeeService.getDashboardData();
        data.putAll(employeeData);
        
        // 当月考勤异常
        String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Map<String, Object> attendanceStats = attendanceService.getAttendanceStats(
                currentMonth + "-01", currentMonth + "-31"
        );
        data.put("attendanceStats", attendanceStats);
        
        // 考勤异常记录（最近5条）
        data.put("abnormalRecords", attendanceService.getAbnormalRecords(5));
        
        // 薪资趋势（最近6个月）
        data.put("salaryTrend", salaryService.getSalaryTrend(6));
        
        // 部门薪资分布
        data.put("salaryByDepartment", salaryService.getSalaryByDepartment(currentMonth));
        
        return Result.success(data);
    }
    
    /**
     * 按时间范围获取统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @RequestParam(defaultValue = "month") String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> data = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        // 考勤统计
        data.put("attendanceStats", attendanceService.getAttendanceStats(startDate, endDate));
        
        // 薪资数据
        String month = startDate.substring(0, 7);
        data.put("totalSalary", salaryService.getTotalSalaryByMonth(month));
        
        return Result.success(data);
    }
}
