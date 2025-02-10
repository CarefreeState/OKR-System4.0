package cn.bitterfree.domain.core.service.impl;


import cn.bitterfree.common.enums.EmailTemplate;
import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.domain.core.config.QuadrantCycleConfig;
import cn.bitterfree.domain.core.constants.OkrCoreConstants;
import cn.bitterfree.domain.core.model.converter.OkrCoreConverter;
import cn.bitterfree.domain.core.model.entity.OkrCore;
import cn.bitterfree.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.bitterfree.domain.core.model.entity.quadrant.FourthQuadrant;
import cn.bitterfree.domain.core.model.entity.quadrant.SecondQuadrant;
import cn.bitterfree.domain.core.model.entity.quadrant.ThirdQuadrant;
import cn.bitterfree.domain.core.model.mapper.OkrCoreMapper;
import cn.bitterfree.domain.core.model.vo.OkrCoreVO;
import cn.bitterfree.domain.core.model.vo.OkrNoticeTemplateVO;
import cn.bitterfree.domain.core.model.vo.quadrant.FirstQuadrantVO;
import cn.bitterfree.domain.core.model.vo.quadrant.FourthQuadrantVO;
import cn.bitterfree.domain.core.model.vo.quadrant.SecondQuadrantVO;
import cn.bitterfree.domain.core.model.vo.quadrant.ThirdQuadrantVO;
import cn.bitterfree.domain.core.service.OkrCoreService;
import cn.bitterfree.domain.core.service.quadrant.FirstQuadrantService;
import cn.bitterfree.domain.core.service.quadrant.FourthQuadrantService;
import cn.bitterfree.domain.core.service.quadrant.SecondQuadrantService;
import cn.bitterfree.domain.core.service.quadrant.ThirdQuadrantService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.email.model.po.EmailMessage;
import cn.bitterfree.email.sender.EmailSender;
import cn.bitterfree.redis.cache.RedisCache;
import cn.bitterfree.template.engine.HtmlEngine;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service实现
* @createDate 2024-01-22 13:42:11
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class OkrCoreServiceImpl extends ServiceImpl<OkrCoreMapper, OkrCore>
    implements OkrCoreService {

    private final QuadrantCycleConfig quadrantCycleConfig;

    private final FirstQuadrantService firstQuadrantService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final RedisCache redisCache;

    private final EmailSender emailSender;

    private final HtmlEngine htmlEngine;

    @Transactional
    public Long createOkrCore() {
        // 1. 创建一个内核
        OkrCore okrCore = new OkrCore();
        okrCore.setIsOver(Boolean.FALSE);
        this.save(okrCore);
        Long coreID = okrCore.getId();
        log.info("新增 OKR 内核：  okr core id : {}", coreID);
        // 2. 创建一二三四象限（datetime对象）
        // 第一象限
        FirstQuadrant firstQuadrant = new FirstQuadrant();
        firstQuadrant.setCoreId(coreID);
        firstQuadrantService.save(firstQuadrant);
        // 第二象限
        SecondQuadrant secondQuadrant = new SecondQuadrant();
        secondQuadrant.setCoreId(coreID);
        secondQuadrantService.save(secondQuadrant);
        // 第三象限
        ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
        thirdQuadrant.setCoreId(coreID);
        thirdQuadrantService.save(thirdQuadrant);
        // 第四象限
        FourthQuadrant fourthQuadrant = new FourthQuadrant();
        fourthQuadrant.setCoreId(coreID);
        fourthQuadrantService.save(fourthQuadrant);
        return coreID;
    }

    @Override
    public OkrCore getOkrCore(Long coreId) {
        String redisKey = OkrCoreConstants.OKR_CORE_ID_MAP + coreId;
        return redisCache.getObject(redisKey, OkrCore.class).orElseGet(() -> {
            OkrCore okrCore = this.lambdaQuery().eq(OkrCore::getId, coreId).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS));
            redisCache.setObject(redisKey, okrCore, OkrCoreConstants.OKR_CORE_MAP_TTL, OkrCoreConstants.OKR_CORE_MAP_UNIT);
            return okrCore;
        });
    }

    @Override
    public void checkOverThrows(Long coreId) {
        if(Boolean.TRUE.equals(getOkrCore(coreId).getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
    }

    @Override
    public void checkNonOverThrows(Long coreId) {
        if(Boolean.FALSE.equals(getOkrCore(coreId).getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_NOT_OVER);
        }
    }

    @Override
    public void removeOkrCoreCache(Long coreId) {
        redisCache.deleteObject(OkrCoreConstants.OKR_CORE_ID_MAP + coreId);
    }

    @Override
    public OkrCoreVO searchOkrCore(Long id) {
        // 查询基本的 OKR 内核
        OkrCore okrCore = getOkrCore(id);
        OkrCoreVO okrCoreVO = OkrCoreConverter.INSTANCE.okrCoreToOkrCoreVO(okrCore);
        // 查询四象限
        Future<FirstQuadrantVO> task1 = IOThreadPool.submit(() -> firstQuadrantService.searchFirstQuadrant(id));
        Future<SecondQuadrantVO> task2 = IOThreadPool.submit(() -> secondQuadrantService.searchSecondQuadrant(id));
        Future<ThirdQuadrantVO> task3 = IOThreadPool.submit(() -> thirdQuadrantService.searchThirdQuadrant(id));
        Future<FourthQuadrantVO> task4 = IOThreadPool.submit(() -> fourthQuadrantService.searchFourthQuadrant(id));
        try {
            okrCoreVO.setFirstQuadrantVO(task1.get());
            okrCoreVO.setSecondQuadrantVO(task2.get());
            okrCoreVO.setThirdQuadrantVO(task3.get());
            okrCoreVO.setFourthQuadrantVO(task4.get());
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
        // 返回
        return okrCoreVO;
    }

    @Override
    public void confirmCelebrateDate(Long id, Integer celebrateDay) {
        OkrCore okrCore = getOkrCore(id);
        if(Boolean.TRUE.equals(okrCore.getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        if(Objects.nonNull(okrCore.getCelebrateDay())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.CELEBRATE_DAY_CANNOT_CHANGE);
        }
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setCelebrateDay(celebrateDay);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        removeOkrCoreCache(id);
    }

    @Override
    public Date summaryOKR(Long id, String summary, Integer degree) {
        OkrCore okrCore = getOkrCore(id);
        if(Boolean.FALSE.equals(okrCore.getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_NOT_OVER);
        }
        // 根据业务，这必然是结束时间
        Date endTime = okrCore.getUpdateTime();
        if(Objects.nonNull(okrCore.getSummary()) || Objects.nonNull(okrCore.getDegree())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_SUMMARIZED);
        }
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setSummary(summary);
        updateOkrCore.setDegree(degree);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        removeOkrCoreCache(id);
        return endTime;
    }

    @Override
    public void noticeOkr(User user, OkrCoreVO okrCoreVO, Date nextDeadline, EmailTemplate emailTemplate) {
        Optional.ofNullable(user).filter(u -> StringUtils.hasText(u.getEmail())).ifPresentOrElse(u -> {
            // 构造模板内容
            OkrNoticeTemplateVO noticeTemplateVO = OkrNoticeTemplateVO.builder()
                    .nickname(u.getNickname())
                    .objective(okrCoreVO.getFirstQuadrantVO().getObjective())
                    .keyResultList(okrCoreVO.getFirstQuadrantVO().getKeyResults())
                    .priorityOneList(okrCoreVO.getSecondQuadrantVO().getPriorityNumberOnes())
                    .priorityTwoList(okrCoreVO.getSecondQuadrantVO().getPriorityNumberTwos())
                    .actionList(okrCoreVO.getThirdQuadrantVO().getActions())
                    .statusList(okrCoreVO.getFourthQuadrantVO().getStatusFlags())
                    .nextDeadline(nextDeadline)
                    .build();
            // 构造邮件
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setTitle(emailTemplate.getTitle());
            emailMessage.setRecipient(u.getEmail());
            emailMessage.setContent(htmlEngine.builder().append(emailTemplate.getTemplate(), noticeTemplateVO).build());
            emailSender.send(emailMessage);
        }, () -> {
            log.warn("无法发送 OKR 通知 -> {}", user);
        });
    }

    @Override
    public void complete(Long id) {
        checkOverThrows(id);
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setIsOver(true);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        log.info("OKR 结束！ {}", new Date());
        removeOkrCoreCache(id);
    }

    @Override
    public void checkThirdCycle(Long id, Integer quadrantCycle) {
        Integer secondQuadrantCycle = this.lambdaQuery()
                .eq(OkrCore::getId, id)
                .oneOpt().orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                ).getSecondQuadrantCycle();
        Optional.ofNullable(secondQuadrantCycle).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.SECOND_FIRST_QUADRANT_NOT_INIT));
        if(quadrantCycle.compareTo(quadrantCycleConfig.getMultiple() * secondQuadrantCycle) < 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.THIRD_CYCLE_TOO_SHORT);
        }
    }
}




