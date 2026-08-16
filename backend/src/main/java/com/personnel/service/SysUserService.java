package com.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personnel.common.Result;
import com.personnel.dto.LoginDTO;
import com.personnel.entity.SysUser;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     */
    Result<SysUser> login(LoginDTO loginDTO);
    
    /**
     * 获取当前登录用户信息
     */
    SysUser getCurrentUser();
}
