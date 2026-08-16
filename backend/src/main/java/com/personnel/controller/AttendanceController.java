package com.personnel.controller;

import com.personnel.common.PageResult;
import com.personnel.common.Result;
import com.personnel.dto.AttendanceQueryDTO;
import com.personnel.entity.Attendance;
import com.personnel.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考勤控制器
 */
@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 分页查询考勤列表
     */
    @GetMapping
    public Result<PageResult<Attendance>> list(AttendanceQueryDTO queryDTO) {
        return Result.success(attendanceService.getAttendancePage(queryDTO));
    }

    /**
     * 获取考勤详情
     */
    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) {
        return Result.success(attendanceService.getAttendanceById(id));
    }

    /**
     * 新增考勤
     */
    @PostMapping
    public Result<Attendance> add(@RequestBody Attendance attendance) {
        return Result.success(attendanceService.addAttendance(attendance));
    }

    /**
     * 修改考勤
     */
    @PutMapping("/{id}")
    public Result<Attendance> update(@PathVariable Long id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        return Result.success(attendanceService.updateAttendance(attendance));
    }

    /**
     * 删除考勤
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success();
    }

    /**
     * 手动补录考勤
     */
    @PostMapping("/supplementary")
    public Result<Void> supplementary(@RequestBody Attendance attendance) {
        attendanceService.supplementaryAttendance(attendance);
        return Result.success();
    }

    /**
     * 考勤统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam String startDate, @RequestParam String endDate) {
        return Result.success(attendanceService.getAttendanceStats(startDate, endDate));
    }

    /**
     * 异常考勤
     */
    @GetMapping("/abnormal")
    public Result<List<Attendance>> abnormal(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(attendanceService.getAbnormalRecords(limit));
    }

    /**
     * 月度考勤汇总
     */
    @GetMapping("/summary/{month}")
    public Result<Map<String, Object>> summary(@PathVariable String month) {
        return Result.success(attendanceService.getMonthlySummary(month));
    }
}
