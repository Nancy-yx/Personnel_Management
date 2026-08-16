package com.personnel.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 用于自动填充 createTime 和 updateTime
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 如果实体类中有 createTime 字段，且当前值为空，则自动填充为当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 同理，填充 updateTime
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新数据时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时，只自动填充 updateTime 为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}