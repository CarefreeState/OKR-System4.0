package cn.bitterfree.api.common.util.media;

import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.convert.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2023-10-27
 * Time: 0:00
 */
@Slf4j
public class MediaUtil {

    private final static Tika TIKA = new Tika();

    public static InputStream getInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        return Objects.nonNull(inputStream) ? inputStream.readAllBytes() : null;
    }

    public static byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String getContentType(InputStream inputStream) throws IOException {
        return TIKA.detect(inputStream);
    }

    public static String getContentType(MultipartFile file) {
        try(InputStream inputStream = file.getInputStream()) {
            return getContentType(inputStream);
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static String getContentType(byte[] data) {
        try(InputStream inputStream = MediaUtil.getInputStream(data)) {
            return getContentType(inputStream);
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static Stream<File> getFiles(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            // 获取目录下的所有文件和子目录
            File[] files = dir.listFiles();
            return ObjectUtil.stream(files).filter(Objects::nonNull);
        }
        return Stream.empty();
    }

}
