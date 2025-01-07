package cn.lbcmmszdntnt.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <span>
 * Mybatis fill config
 * </span>
 *
 */
@Component
@EnableTransactionManagement
public class MybatisFillConfig implements MetaObjectHandler {

    @Value("${mybatis-plus.db-type}")
    private DbType dbType;

    @Override
    public void insertFill(MetaObject metaObject) {
        long currentTimeMillis = System.currentTimeMillis();
        this.strictInsertFill(metaObject, "version", Integer.class, 1);
        this.strictInsertFill(metaObject, "isDeleted", Boolean.class, Boolean.FALSE);
        this.strictInsertFill(metaObject, "createTime", Long.class, currentTimeMillis);
        this.strictUpdateFill(metaObject, "updateTime", Long.class, currentTimeMillis);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long currentTimeMillis = System.currentTimeMillis();
        this.strictUpdateFill(metaObject, "updateTime", Long.class, currentTimeMillis);
    }

    //乐观锁
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType)); // 如果配置多个插件, 切记分页最后添加
        return interceptor;
    }
}