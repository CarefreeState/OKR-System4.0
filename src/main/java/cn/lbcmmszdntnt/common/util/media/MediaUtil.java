package cn.lbcmmszdntnt.common.util.media;

import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.config.WebMvcConfiguration;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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

    public static final String SUFFIX = "png";

    public static final String UTF_8 = StandardCharsets.UTF_8.toString();

    private static final Pattern HTTP_PATTERN = Pattern.compile("^(http|https)://.*$");

    private final static Tika TIKA = new Tika();

    public final static String COMPRESS_FORMAT_NAME = "jpg"; // 压缩图片格式
    public final static String COMPRESS_FORMAT_SUFFIX = "." + COMPRESS_FORMAT_NAME; // 压缩图片格式
    public final static float COMPRESS_SCALE = 1.0f; // 压缩图片大小
    public final static float COMPRESS_QUALITY = 0.5f; // 压缩图片质量

    // 获取UUID
    public static String getUUID_32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

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

    public static String getUniqueImageName() {
        //拼接
        return String.format("%s.%s", getUUID_32(), SUFFIX);
    }

    public static String getLocalFilePath(String mapPath) {
        return WebMvcConfiguration.ROOT + mapPath;
    }

    public static String getLocalFileName(String mapPath) {
        return mapPath.substring(mapPath.lastIndexOf("/") + 1);
    }

    public static void tryCreateFile(String savePath, String filePath) {
        File directory = new File(savePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
    }

    /**
     * 输入流转字节流
     */
    public static byte[] inputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int ch;
        while ((ch = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, ch);
        }
        byte data[] = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return data;
    }

    public static String saveImage(byte[] imageData) {
        String savePath = WebMvcConfiguration.ROOT + WebMvcConfiguration.MAP_ROOT;
        String fileName = getUniqueImageName();
        String filePath = savePath + fileName;
        String mapPath = WebMvcConfiguration.MAP_ROOT + fileName;
        saveFile(savePath, filePath, imageData);
        log.info("图片保存成功 {}", filePath);
        return mapPath;
    }

    public static String saveImage(byte[] imageData, String extraPath) {
        if(!StringUtils.hasText(extraPath)) {
            return saveImage(imageData);
        }
        String mapBasePath = WebMvcConfiguration.MAP_ROOT + extraPath;
        String savePath = WebMvcConfiguration.ROOT + mapBasePath;
        String fileName = getUniqueImageName();
        String filePath = savePath + fileName;
        String mapPath = mapBasePath + fileName;
        saveFile(savePath, filePath, imageData);
        log.info("图片保存成功 {}", filePath);
        return mapPath;
    }

    public static void saveFile(String savePath, String filePath, String url) {
        MediaUtil.tryCreateFile(savePath, filePath);
        try(InputStream inputStream = HttpUtil.getFileInputStream(url);
            OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            byte[] data  = MediaUtil.inputStreamToByte(inputStream);
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void saveFile(String savePath, String filePath, byte[] data) {
        MediaUtil.tryCreateFile(savePath, filePath);
        try(OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            log.warn("删除文件 {}", path);
        }
    }

    public static byte[] getCustomColorQRCodeByteArray(String url, int width, int height) {
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

    public static InputStream getCustomColorQRCodeInputStream(String url, int width, int height) {
        return new ByteArrayInputStream(getCustomColorQRCodeByteArray(url, width, height));
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
}
