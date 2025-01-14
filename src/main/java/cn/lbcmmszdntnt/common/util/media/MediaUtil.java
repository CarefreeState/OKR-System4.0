package cn.lbcmmszdntnt.common.util.media;

import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2023-10-27
 * Time: 0:00
 */
@Slf4j
public class MediaUtil {

    public final static String TEMP_RESOURCE_PATH = "./temp/"; // 临时文件夹的文件都是用完就删的，所以我就觉得这个变量没必要写在配置文件里了

    public static final String SUFFIX = "png";

    public static final String UTF_8 = StandardCharsets.UTF_8.toString();

    private static final Pattern HTTP_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)(:(\\{[^}]+\\}?|[^/?#]*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");

    private final static Tika TIKA = new Tika();

    public final static String COMPRESS_FORMAT_NAME = "jpg"; // 压缩图片格式
    public final static String COMPRESS_FORMAT_SUFFIX = "." + COMPRESS_FORMAT_NAME; // 压缩图片格式
    public final static float COMPRESS_SCALE = 1.0f; // 压缩图片大小
    public final static float COMPRESS_QUALITY = 0.5f; // 压缩图片质量

    public static byte[] compressImage(byte[] bytes) {
        try(InputStream inputStream = getInputStream(bytes);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // 使用 thumbnailator 进行压缩，指定输出格式
            Thumbnails.of(inputStream)
                    .outputFormat(COMPRESS_FORMAT_NAME)
                    .scale(COMPRESS_SCALE)
                    .outputQuality(COMPRESS_QUALITY)
                    .toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static byte[] getUrlQRCodeBytes(String url, int width, int height) {
        // 配置生成二维码的参数
        Map<EncodeHintType, String> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, UTF_8);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // 生成二维码矩阵
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hintMap);
            // 创建二维码图片
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            qrImage.createGraphics();
            // 将二维码矩阵渲染到图片上
            Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (bitMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            graphics.dispose();
            // 将二维码图片转换为输入流
            // 将BufferedImage转换为字节数组
            ImageIO.write(qrImage, SUFFIX, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | WriterException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static boolean isHttpUrl(String url) {
        return StringUtils.hasText(url) && HTTP_PATTERN.matcher(url).matches();
    }

    @Nullable
    public static HttpURLConnection openConnection(String url) throws IOException {
        try {
            HttpURLConnection connection = isHttpUrl(url) ? (HttpURLConnection) new URL(url).openConnection() : null;
            if(Objects.nonNull(connection) && connection.getResponseCode() / 100 == 3) {
                return openConnection(connection.getHeaderField("Location")); // Location 就是最深的那个地址了
            } else {
                return connection;
            }
        } catch (ProtocolException | UnknownHostException e) {
            // 处理重定向次数太多的情况
            log.warn(e.getMessage());
            return null;
        }
    }

    public static boolean isAccessible(HttpURLConnection connection) throws IOException {
        return Objects.nonNull(connection) && connection.getResponseCode() / 100 == 2;
    }

    public static boolean isAccessible(String url) throws IOException {
        return isAccessible(openConnection(url));
    }

    @Nullable
    public static InputStream getInputStream(String url) throws IOException {
        HttpURLConnection connection = openConnection(url);
        return isAccessible(connection) ? connection.getInputStream() : null;
    }

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

    private static FileOutputStream createAndGetFileOutputStream(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        return new FileOutputStream(file);
    }

    public static String getTempFilePath(String suffix) {
        String tempResourcePath = TEMP_RESOURCE_PATH;
        File tempDir = new File(tempResourcePath);
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }
        return tempResourcePath + FileResourceUtil.getSimpleFileName(suffix);
    }

    public static <T> T createTempFileGetSomething(String originalName, byte[] data, Function<File, T> converter) {
        String fileNameSuffix = FileResourceUtil.getSuffix(originalName);
        // 获取即将创建的临时文件的路径
        String tempFilePath = getTempFilePath(fileNameSuffix);
        File tempFile = new File(tempFilePath);
        // 创建并写入，应用后删除
        try (FileOutputStream outputStream = createAndGetFileOutputStream(tempFile)) {
            outputStream.write(data);
            outputStream.flush();
            return converter.apply(tempFile);
        }  catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            tempFile.delete();
        }
    }
}
