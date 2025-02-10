package cn.bitterfree.domain.core.service.quadrant;


import cn.bitterfree.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.bitterfree.domain.core.model.entity.quadrant.ThirdQuadrant;
import cn.bitterfree.domain.core.model.vo.quadrant.ThirdQuadrantVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:20
*/
public interface ThirdQuadrantService extends IService<ThirdQuadrant> {

    void initThirdQuadrant(InitQuadrantDTO initQuadrantDTO);
    void updateDeadline(Long id, Date date);

    ThirdQuadrantVO searchThirdQuadrant(Long coreId);

    Long getThirdQuadrantCoreId(Long id);
}
