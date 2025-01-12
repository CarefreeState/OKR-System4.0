package cn.lbcmmszdntnt.domain.core.model.converter;

import cn.lbcmmszdntnt.domain.core.model.entity.record.DayRecord;
import cn.lbcmmszdntnt.domain.core.model.vo.record.DayRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-12
 * Time: 0:10
 */
@Mapper
public interface DayRecordConverter {

    DayRecordConverter INSTANCE = Mappers.getMapper(DayRecordConverter.class);

    List<DayRecordVO> recordListToDayRecordVOList(List<DayRecord> recordList);

}
