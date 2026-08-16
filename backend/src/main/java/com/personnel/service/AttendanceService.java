package com.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personnel.common.PageResult;
import com.personnel.dto.AttendanceQueryDTO;
import com.personnel.entity.Attendance;

import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService extends IService<Attendance> {

    /**
     * 分页查询考勤
     */
    PageResult<Attendance> getAttendancePage(AttendanceQueryDTO queryDTO);

    /**
     * 根据ID获取考勤
     */
    Attendance getAttendanceById(Long id);

    /**
     * 新增考勤
     */
    Attendance addAttendance(Attendance attendance);

    /**
     * 修改考勤
     */
    Attendance updateAttendance(Attendance attendance);

    /**
     * 删除考勤
     */
    void deleteAttendance(Long id);

    /**
     * 补录考勤
     */
    void supplementaryAttendance(Attendance attendance);

    /**
     * 获取考勤统计
     */
    Map<String, Object> getAttendanceStats(String startDate, String endDate);

    /**
     * 获取异常考勤记录
     */
    List<Attendance> getAbnormalRecords(Integer limit);

    /**
     * 月度考勤汇总
     */
    Map<String, Object> getMonthlySummary(String month);
}
