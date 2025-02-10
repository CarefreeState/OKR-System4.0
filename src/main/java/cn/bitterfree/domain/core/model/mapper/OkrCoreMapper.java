package cn.bitterfree.domain.core.model.mapper;


import cn.bitterfree.domain.core.model.entity.OkrCore;
import cn.bitterfree.domain.coredeadline.handler.event.DeadlineEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 马拉圈
 * @description 针对表【okr_core(OKR 内核表)】的数据库操作Mapper
 * @createDate 2024-01-19 21:19:05
 */
public interface OkrCoreMapper extends BaseMapper<OkrCore> {

    List<DeadlineEvent> getDeadlineEvents();

}



