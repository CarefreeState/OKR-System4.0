package cn.lbcmmszdntnt.domain.core.model.mapper;


import cn.lbcmmszdntnt.domain.core.handler.event.DeadlineEvent;
import cn.lbcmmszdntnt.domain.core.model.entity.OkrCore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 马拉圈
 * @description 针对表【okr_core(OKR 内核表)】的数据库操作Mapper
 * @createDate 2024-01-19 21:19:05
 * @Entity com.macaku.core.domain.po.OkrCore
 */
public interface OkrCoreMapper extends BaseMapper<OkrCore> {

    List<DeadlineEvent> getDeadlineEvents();

}



