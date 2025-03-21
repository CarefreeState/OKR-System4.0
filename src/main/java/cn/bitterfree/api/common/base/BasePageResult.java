package cn.bitterfree.api.common.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-21
 * Time: 13:36
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Schema(description = "分页查询结果集")
public class BasePageResult<T> {

    @Schema(description = "当前页")
    private Long current;

    @Schema(description = "页内大小")
    private Long pageSize;

    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "页内元素集合")
    private List<T> list;

    /**
     * 返回空分页结果
     * @param p MybatisPlus的分页结果
     * @param <V> 目标VO类型
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <V, P> BasePageResult<V> empty(IPage<P> p){
        return new BasePageResult<>(p.getCurrent(), p.getSize(), p.getTotal(), p.getPages(), Collections.emptyList());
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果
     * @param p MybatisPlus的分页结果
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <P> BasePageResult<P> of(IPage<P> p) {
        return new BasePageResult<>(p.getCurrent(), p.getSize(), p.getTotal(), p.getPages(), p.getRecords());
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果
     * @param p MybatisPlus的分页结果
     * @param voClass 目标VO类型的字节码
     * @param <V> 目标VO类型
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <V, P> BasePageResult<V> of(IPage<P> p, Class<V> voClass) {
        // 1.非空校验
        List<P> records = p.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            // 无数据，返回空结果
            return empty(p);
        }
        // 2.数据转换
        List<V> vos = BeanUtil.copyToList(records, voClass);
        // 3.封装返回
        return new BasePageResult<>(p.getCurrent(), p.getSize(), p.getTotal(), p.getPages(), vos);
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果，允许用户自定义PO到VO的转换方式
     * @param p MybatisPlus的分页结果
     * @param convertor PO到VO的转换函数
     * @param <V> 目标VO类型
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <V, P> BasePageResult<V> of(IPage<P> p, Function<P, V> convertor) {
        // 1.非空校验
        List<P> records = p.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            // 无数据，返回空结果
            return empty(p);
        }
        // 2.数据转换
        List<V> vos = records.stream().map(convertor).toList();
        // 3.封装返回
        return new BasePageResult<>(p.getCurrent(), p.getSize(), p.getTotal(), p.getPages(), vos);
    }
}
