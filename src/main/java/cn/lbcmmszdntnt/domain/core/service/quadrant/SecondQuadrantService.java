package cn.lbcmmszdntnt.domain.core.service.quadrant;


import cn.lbcmmszdntnt.domain.core.model.po.quadrant.SecondQuadrant;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto.InitQuadrantDTO;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.SecondQuadrantVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:21
*/
public interface SecondQuadrantService extends IService<SecondQuadrant> {

    @Transactional
    void initSecondQuadrant(InitQuadrantDTO initQuadrantDTO);

    SecondQuadrantVO searchSecondQuadrant(Long coreId);

    Long getSecondQuadrantCoreId(Long id);

}
