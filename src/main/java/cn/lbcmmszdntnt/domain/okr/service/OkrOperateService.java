package cn.lbcmmszdntnt.domain.okr.service;


import cn.lbcmmszdntnt.domain.core.model.dto.OkrOperateDTO;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 18:36
 */
public interface OkrOperateService {

    @Transactional
    Map<String, Object> createOkrCore(User user, OkrOperateDTO okrOperateDTO);

    OkrCoreVO selectAllOfCore(User user, Long coreId);

    Boolean canVisit(User user, Long coreId);

    Long getCoreUser(Long coreId);

}
