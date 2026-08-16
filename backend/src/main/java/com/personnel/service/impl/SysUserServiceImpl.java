package com.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personnel.common.ErrorCode;
import com.personnel.common.Result;
import com.personnel.config.JwtConfig;
import com.personnel.dto.LoginDTO;
import com.personnel.entity.SysUser;
import com.personnel.mapper.SysUserMapper;
import com.personnel.service.SysUserService;
import com.personnel.util.JwtUtil;
import com.personnel.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    
    @Override
    public Result<SysUser> login(LoginDTO loginDTO) {
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .one();
        
        if (user == null) {
            return Result.error(ErrorCode.USER_NOT_EXIST);
        }
        
        if (user.getStatus() == 0) {
            return Result.error(ErrorCode.USER_DISABLED);
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error(ErrorCode.USER_PASSWORD_ERROR);
        }
        
        return Result.success(user);
    }
    
    @Override
    public SysUser getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId != null) {
            return this.getById(userId);
        }
        return null;
    }
    
    /**
     * 生成登录响应
     */
    public Map<String, Object> generateLoginResponse(SysUser user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", jwtConfig.getPrefix().trim());
        result.put("expiresIn", jwtConfig.getExpiration() / 1000);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("employeeId", user.getEmployeeId());
        
        result.put("userInfo", userInfo);
        return result;
    }
    
    /**
     * 初始化管理员账号（确保admin用户存在且密码正确）
     */
    public void initAdminUser() {
        SysUser admin = this.lambdaQuery()
                .eq(SysUser::getUsername, "admin")
                .one();

        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            this.save(admin);
        } else if (!passwordEncoder.matches("admin123", admin.getPassword())) {
            // 密码不匹配时自动重置（修复schema.sql中可能不匹配的密码哈希）
            admin.setPassword(passwordEncoder.encode("admin123"));
            this.updateById(admin);
        }
    }
}
