package cn.bitterfree.api.domain.okr.service.impl;


import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.model.dto.OkrOperateDTO;
import cn.bitterfree.api.domain.core.model.vo.OKRCreateVO;
import cn.bitterfree.api.domain.core.model.vo.OkrCoreVO;
import cn.bitterfree.api.domain.core.model.vo.inner.UserStatusFlagsVO;
import cn.bitterfree.api.domain.core.service.OkrCoreService;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.okr.constants.OkrConstants;
import cn.bitterfree.api.domain.okr.model.entity.PersonalOkr;
import cn.bitterfree.api.domain.okr.model.mapper.PersonalOkrMapper;
import cn.bitterfree.api.domain.okr.model.vo.PersonalOkrVO;
import cn.bitterfree.api.domain.okr.service.PersonalOkrService;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalOkrServiceImpl extends ServiceImpl<PersonalOkrMapper, PersonalOkr>
    implements PersonalOkrService, OkrOperateService {

    private final OkrCoreService okrCoreService;

    private final PersonalOkrMapper personalOkrMapper;

    private final RedisCache redisCache;

    @Override
    @Transactional
    public OKRCreateVO createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        Long userId = user.getId();
        // 查看当前用户是否有未完成的 OKR
        Long count = personalOkrMapper.getNotCompletedCount(userId);
        if(OkrConstants.ALLOW_NOT_COMPLETED_PERSONAL_OKR_COUNT.compareTo(count) <= 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_PERMISSION);
        }
        // 创建 OKR 内核
        Long coreId = okrCoreService.createOkrCore();
        // 创建 个人 OKR
        PersonalOkr personalOkr = new PersonalOkr();
        personalOkr.setCoreId(coreId);
        personalOkr.setUserId(userId);
        personalOkrMapper.insert(personalOkr);
        Long id = personalOkr.getId();
        log.info("用户 {} 个人 OKR {}  内核 {}", userId, id, coreId);
        return OKRCreateVO.builder().id(id).coreId(coreId).build();
    }

    @Override
    public OkrCoreVO selectAllOfCore(User user, Long coreId) {
        if(Boolean.TRUE.equals(canVisit(user, coreId))) {
            // 调用服务查询详细信息
            return okrCoreService.searchOkrCore(coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }

    @Override
    public Boolean canVisit(User user, Long coreId) {
        // 根据 coreId 获取 coreId 使用者（个人 OKR 只能由使用者观看）
        return user.getId().equals(getCoreUser(coreId));
    }

    @Override
    public Long getCoreUser(Long coreId) {
        String redisKey = OkrConstants.USER_CORE_MAP + coreId;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            Long userId = Db.lambdaQuery(PersonalOkr.class)
                    .eq(PersonalOkr::getCoreId, coreId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getUserId();
            redisCache.setObject(redisKey, userId, OkrConstants.USER_CORE_MAP_TTL, OkrConstants.USER_CORE_MAP_TTL_UNIT);
            return userId;
        });
    }

    @Override
    public List<UserStatusFlagsVO> getStatusFlagsByUserId(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return personalOkrMapper.getStatusFlagsByUserId(ids);
    }

    @Override
    public List<String> mergeUserOkr(Long mainUserId, Long userId) {
        List<String> redisKeys = this.lambdaQuery()
                .eq(PersonalOkr::getUserId, userId)
                .list()
                .stream()
                .map(PersonalOkr::getCoreId)
                .map(uid -> OkrConstants.USER_CORE_MAP + uid)
                .toList();
        // 更新
        this.lambdaUpdate()
                .eq(PersonalOkr::getUserId, userId)
                .set(PersonalOkr::getUserId, mainUserId)
                .update();
        return redisKeys;
    }

    @Override
    public List<PersonalOkrVO> getPersonalOkrList(User user) {
        // 根据用户 ID 查询
        Long id = user.getId();
        List<PersonalOkrVO> personalOkrList = personalOkrMapper.getPersonalOkrList(id);
        log.info("查询用户 {} 的个人 OKR 列表 : {} 行", id, personalOkrList.size());
        return personalOkrList;
    }
}




