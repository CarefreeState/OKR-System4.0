package cn.lbcmmszdntnt.domain.user.service;


import cn.lbcmmszdntnt.domain.user.model.dto.detail.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:02
 */
public interface UserRecordService {

    Optional<LoginUser> getRecord(HttpServletRequest request);

    void deleteRecord(HttpServletRequest request);
}