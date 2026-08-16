package com.personnel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 企业人事管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.personnel.mapper")
public class PersonnelManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonnelManagementApplication.class, args);
    }
}
