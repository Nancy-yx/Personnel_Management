package com.personnel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personnel.common.BusinessException;
import com.personnel.common.ErrorCode;
import com.personnel.common.PageResult;
import com.personnel.dto.AttendanceQueryDTO;
import com.personnel.entity.Attendance;
import com.personnel.mapper.AttendanceMapper;
import com.personnel.service.AttendanceService;
import com.personnel.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    @Override
    public PageResult<Attendance> getAttendancePage(AttendanceQueryDTO queryDTO) {
        Page<Attendance> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Attendance> result = baseMapper.selectAttendancePage(page, queryDTO);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        Attendance attendance = this.getById(id);
        if (attendance == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_EXIST);
        }
        return attendance;
    }

    @Override
    public Attendance addAttendance(Attendance attendance) {
        fillCreateBy(attendance);
        this.save(attendance);
        return attendance;
    }

    @Override
    public Attendance updateAttendance(Attendance attendance) {
        if (this.getById(attendance.getId()) == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_EXIST);
        }
        this.updateById(attendance);
        return attendance;
    }

    @Override
    public void deleteAttendance(Long id) {
        if (this.getById(id) == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public void supplementaryAttendance(Attendance attendance) {
        attendance.setIsSupplementary(1);
        fillCreateBy(attendance);
        this.save(attendance);
    }

    /**
     * 填充创建人
     */
    private void fillCreateBy(Attendance attendance) {
        if (attendance.getCreateBy() == null) {
            String username = SecurityUtil.getCurrentUsername();
            attendance.setCreateBy(username);
        }
    }

    @Override
    public Map<String, Object> getAttendanceStats(String startDate, String endDate) {
        Map<String, Object> result = baseMapper.selectAttendanceStats(startDate, endDate);
        if (result == null) {
            result = new HashMap<>();
            result.put("total", 0);
            result.put("normal", 0);
            result.put("late", 0);
            result.put("early", 0);
            result.put("absent", 0);
            result.put("leave", 0);
            result.put("overtime", 0);
        }
        return result;
    }

    @Override
    public List<Attendance> getAbnormalRecords(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return baseMapper.selectAbnormalRecords(limit);
    }

    @Override
    public Map<String, Object> getMonthlySummary(String month) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> list = baseMapper.selectMonthlySummary(month);
        data.put("month", month);
        data.put("records", list);
        return data;
    }
}
