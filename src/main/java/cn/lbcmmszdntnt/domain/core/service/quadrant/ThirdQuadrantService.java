package cn.lbcmmszdntnt.domain.core.service.quadrant;


import cn.lbcmmszdntnt.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.ThirdQuadrant;
import cn.lbcmmszdntnt.domain.core.model.vo.ThirdQuadrantVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:20
*/
public interface ThirdQuadrantService extends IService<ThirdQuadrant> {

    @Transactional
    void initThirdQuadrant(InitQuadrantDTO initQuadrantDTO);

    ThirdQuadrantVO searchThirdQuadrant(Long coreId);

    Long getThirdQuadrantCoreId(Long id);
}
