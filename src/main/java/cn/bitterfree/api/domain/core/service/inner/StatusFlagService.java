package cn.bitterfree.api.domain.core.service.inner;


import cn.bitterfree.api.domain.core.model.entity.inner.StatusFlag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Service
* @createDate 2024-01-20 02:24:49
*/
public interface StatusFlagService extends IService<StatusFlag> {

    Long addStatusFlag(StatusFlag statusFlag);

    void removeStatusFlag(Long id);

    void updateStatusFlag(StatusFlag statusFlag);

    Long getFlagFourthQuadrantId(Long id);
}
