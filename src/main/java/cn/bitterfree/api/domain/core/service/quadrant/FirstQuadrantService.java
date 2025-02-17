package cn.bitterfree.api.domain.core.service.quadrant;


import cn.bitterfree.api.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.bitterfree.api.domain.core.model.vo.quadrant.FirstQuadrantVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【first_quadrant(第一象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:21
*/
public interface FirstQuadrantService extends IService<FirstQuadrant> {

    void initFirstQuadrant(FirstQuadrant firstQuadrant);

    FirstQuadrantVO searchFirstQuadrant(Long coreId);

    Long getFirstQuadrantCoreId(Long id);

}
