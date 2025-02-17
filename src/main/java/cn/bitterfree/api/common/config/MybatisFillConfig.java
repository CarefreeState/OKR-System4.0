package cn.bitterfree.api.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

/**
 * <span>
 * Mybatis fill config
 * </span>
 *
 */
@Configuration
@EnableTransactionManagement
public class MybatisFillConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date current = new Date();
        this.strictInsertFill(metaObject, "version", Integer.class, 1);
        this.strictInsertFill(metaObject, "isDeleted", Boolean.class, Boolean.FALSE);
        this.strictInsertFill(metaObject, "createTime", Date.class, current);
        this.strictInsertFill(metaObject, "updateTime", Date.class, current);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date current = new Date();
        this.strictUpdateFill(metaObject, "updateTime", Date.class, current);
    }

    //乐观锁
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 如果配置多个插件, 切记分页最后添加
        return interceptor;
    }
}