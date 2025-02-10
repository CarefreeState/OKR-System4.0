package cn.bitterfree.common.util.media;

import cn.bitterfree.common.exception.GlobalServiceException;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 17:07
 */
public class ClassPathResourceUtil {

    public static InputStream getInputStream(String path) throws IOException {
        return new ClassPathResource(path).getInputStream();
    }

    public static byte[] getBytes(String path) {
        try (InputStream inputStream = getInputStream(path)) {
            return MediaUtil.getBytes(inputStream);
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

}
