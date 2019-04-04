package com.tj.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 */
@Slf4j
public class QRCodeUtil {
    private static final int width = 300;// 默认二维码宽度
    private static final int height = 300;// 默认二维码高度
    private static final String format = "png";// 默认二维码文件格式
    private static final Map<EncodeHintType, Object> hints = new HashMap();// 二维码参数

    static {
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 字符编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 容错等级 L、M、Q、H 其中 L 为最低, H 为最高
        hints.put(EncodeHintType.MARGIN, 2);// 二维码与图片边距
    }

    /**
     * 返回一个 BufferedImage 对象
     *
     * @param content 二维码内容
     * @param width   宽
     * @param height  高
     */
    public static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 将二维码图片输出到一个流中
     *
     * @param content 二维码内容
     * @param stream  输出流
     * @param width   宽
     * @param height  高
     */
    public static void writeToStream(String content, OutputStream stream, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = createQrcodeMatrix(content, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
    }

    /**
     * 根据内容生成二维码数据
     *
     * @param content 二维码文字内容[为了信息安全性，一般都要先进行数据加密]
     */
    private static BitMatrix createQrcodeMatrix(String content, int width, int height) {
     /* Map<EncodeHintType, Object> hints = Maps.newEnumMap(EncodeHintType.class);
      // 设置字符编码
      hints.put(EncodeHintType.CHARACTER_SET, Charsets.UTF_8.name());
      // 指定纠错等级
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);*/
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (Exception e) {
            log.error("内容为：【" + content + "】的二维码生成失败！");
            return null;
        }

    }

    /**
     * 生成二维码图片文件
     *
     * @param content 二维码内容
     * @param path    文件保存路径
     * @param width   宽
     * @param height  高
     */
    public static void createQRCode(String content, String path, int width, int height) throws WriterException, IOException {
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        //toPath() 方法由 jdk1.7 及以上提供
        MatrixToImageWriter.writeToPath(bitMatrix, format, new File(path).toPath());
    }

    public static String recognizeQRCode(byte[] bytes) {

        try {
            QRCodeReader reader = new QRCodeReader();
            InputStream in = new ByteArrayInputStream(bytes);

            BufferedImage imageBuffer = ImageIO.read(in);
            LuminanceSource source = new BufferedImageLuminanceSource(imageBuffer);
            HybridBinarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap image = new BinaryBitmap(binarizer);
            Result rs = reader.decode(image);
            return rs.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String[] args) {

        try {
			/*File file = new File("D://5.png");
	    	InputStream in;
			in = new FileInputStream(file);
			byte[] imgbyte = new byte[in.available()];
			in.read(imgbyte);
			System.out.println(recognizeQRCode(imgbyte));;*/
            createQRCode("2N2JLUFJUFNa1XsCJevnvG49USyxDazLnzJ", "D://6.png", width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
