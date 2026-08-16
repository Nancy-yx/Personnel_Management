package com.personnel.controller;

import com.personnel.common.Result;
import com.personnel.dto.LoginDTO;
import com.personnel.entity.SysUser;
import com.personnel.service.impl.SysUserServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final SysUserServiceImpl sysUserService;
    private final PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void init() {
        sysUserService.initAdminUser();
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Result<SysUser> result = sysUserService.login(loginDTO);
        if (result.getCode() == 200) {
            Map<String, Object> loginResponse = sysUserService.generateLoginResponse(result.getData());
            return Result.success(loginResponse);
        }
        return Result.error(result.getCode(), result.getMessage());
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/userInfo")
    public Result<SysUser> getUserInfo() {
        SysUser user = sysUserService.getCurrentUser();
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        SysUser user = sysUserService.getCurrentUser();
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserService.updateById(user);
        
        return Result.success();
    }
}
