package cn.lbcmmszdntnt.domain.qrcode.model.convert;

import cn.lbcmmszdntnt.domain.qrcode.bloomfilter.SecretCodeBloomFilterProperties;
import cn.lbcmmszdntnt.redis.bloomfilter.BloomFilterProperties;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 19:52
 */
@Mapper
public interface SecretCodeConverter {

    SecretCodeConverter INSTANCE = Mappers.getMapper(SecretCodeConverter.class);

    BloomFilterProperties secretCodeBloomFilterPropertiesToBloomFilterProperties(SecretCodeBloomFilterProperties secretCodeBloomFilterProperties);


}
