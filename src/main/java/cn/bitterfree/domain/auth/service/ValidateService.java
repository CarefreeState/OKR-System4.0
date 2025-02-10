package cn.bitterfree.domain.auth.service;


import cn.bitterfree.common.enums.GlobalServiceStatusCode;

import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 0:45
 */
public interface ValidateService {

    void validate(String key, Supplier<Boolean> isValid, GlobalServiceStatusCode statusCode);

}
