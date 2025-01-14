package cn.lbcmmszdntnt.common.util.media;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ImageUtil {

    private final static String DEFAULT_FONT = "宋体";
    private final static String FONT_PATH = SpringUtil.getProperty("font.path");
    private final static String BOARD_PATH = SpringUtil.getProperty("font.board");
    private final static double MAX_PX_RATE = 0.213;
    private final static double REFER_WIDTH_RATE = 0.800;
    private final static double REFER_HEIGHT_RATE = 0.333;
    private final static double SHIN_BACK_RATE = 0.965;
    private final static double IMAGE_SIZE = 750.000;
    private final static double MAX_PX = MAX_PX_RATE * IMAGE_SIZE;
    private final static double REFER_WIDTH = REFER_WIDTH_RATE * IMAGE_SIZE;
    private final static double REFER_HEIGHT = REFER_HEIGHT_RATE * IMAGE_SIZE;

    public static Font getFont(float fontSize){
        Font font = new Font(DEFAULT_FONT, Font.BOLD, (int)fontSize); // 默认字体
        ClassPathResource classPathResource = new ClassPathResource(FONT_PATH);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            Font tempFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            //当参数为 float 类型，才是设置文字大小
            font = tempFont.deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            throw new GlobalServiceException(e.getMessage());
        }
        return font;
    }

    public static byte[] pressText(String text, byte[] bytes, Color color, Font font, int x, int y) throws IOException {
        Image src = ImageIO.read(MediaUtil.getInputStream(bytes));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(src, 0, 0, width, height, null);
        graphics.setColor(color);
        graphics.setFont(font);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        // 在指定坐标（图片居中）绘制水印文字
        graphics.drawString(text, x, y);
        graphics.dispose();
        // 输出到文件流
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, MediaUtil.SUFFIX, outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }
    }

    public static double getLength(String text) {
        double length = 0.0;
        char[] charArray = text.toCharArray();
        for(char ch : charArray) {
            if (ch == ' ' || (String.valueOf(ch)).getBytes().length > 1) {
                length += 0.725;
            } else if(ch == 'i' || ch == 'I' || ch == 'l') {
                length += 0.225;
            } else if(ch == 'A' || ch == 'E' || ch == 'G') {
                length += 0.500;
            } else {
                length += 0.475;
            }
        }
        return length;
    }

    public static int calculateFontSize(double len) {
        double px = Math.min(REFER_WIDTH / len, MAX_PX);
        return (int) px;
    }

    public static int calculateLeftSize(double len) {
        double px = REFER_WIDTH / len;
        px = px > MAX_PX ? MAX_PX : Math.min(px / SHIN_BACK_RATE, MAX_PX);
        return (int) ((IMAGE_SIZE - len * px) / 2);
    }

    public static int calculateTopSize(double len) {
        double px = Math.min(REFER_WIDTH / len, MAX_PX);
        return (int) ((REFER_HEIGHT + px) / 2);
    }

    public static byte[] writeFancy(String text, Color color, byte[] bytes) throws IOException {
        double len = getLength(text);
        Font systemFont = getFont(calculateFontSize(len));
        return pressText(text, bytes, color, systemFont, calculateLeftSize(len), calculateTopSize(len));
    }

    public static byte[] signatureFancy(String text, Color color, byte[] bytes) throws IOException {
        Font systemFont = getFont(60);
        return pressText(text, bytes, color, systemFont, 25, 300);
    }

    public static byte[] mergeImage(byte[] bytes, byte[] board, int x, int y, int width, int height) throws Exception {
        try (InputStream inputStream = MediaUtil.getInputStream(board)) {
            BufferedImage boardImager = ImageIO.read(inputStream);
            //合成器和背景图（整个图片的宽高和相关计算依赖于背景图，所以背景图的大小是个基准）
            ImageCombiner combiner = new ImageCombiner(boardImager, OutputFormat.PNG);
            combiner.setBackgroundBlur(0);     //设置背景高斯模糊（毛玻璃效果）
            combiner.setCanvasRoundCorner(0); //设置整图圆角（输出格式必须为PNG）
            //二维码（强制按指定宽度、高度缩放）
            combiner.addImageElement(ImageIO.read(MediaUtil.getInputStream(bytes)),
                    x, y, width, height, ZoomMode.WidthHeight);
            //执行图片合并
            combiner.combine();
            //保存文件
            try(InputStream mergedInputStream = combiner.getCombinedImageStream()) {
                return MediaUtil.getBytes(mergedInputStream);
            }
        }
    }

    public static byte[] signatureWrite(byte[] bytes, String text, String flag, Color textColor, Color flagColor) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(BOARD_PATH);
            byte[] mergedImage = mergeImage(bytes, MediaUtil.getBytes(classPathResource.getInputStream()),
                    125, 250, 500, 500);
            mergedImage = signatureFancy(flag, flagColor, mergedImage);
            return writeFancy(text, textColor, mergedImage);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public static byte[] signatureWrite(byte[] bytes, String text, String flag, Color textColor) {
        return signatureWrite(bytes, text, flag, textColor, Color.BLACK);
    }

}
