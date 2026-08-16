package com.personnel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.personnel.dto.AttendanceQueryDTO;
import com.personnel.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 考勤Mapper
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 分页查询考勤（带员工姓名）
     */
    IPage<Attendance> selectAttendancePage(IPage<Attendance> page, @Param("query") AttendanceQueryDTO queryDTO);

    /**
     * 考勤统计
     */
    Map<String, Object> selectAttendanceStats(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询异常考勤记录
     */
    List<Attendance> selectAbnormalRecords(@Param("limit") Integer limit);

    /**
     * 月度考勤汇总
     */
    List<Map<String, Object>> selectMonthlySummary(@Param("month") String month);
}
