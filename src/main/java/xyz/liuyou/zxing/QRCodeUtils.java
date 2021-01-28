package xyz.liuyou.zxing;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import xyz.liuyou.zxing.utils.QRCodeUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/1/27 22:35
 * @decription 二维码工具类
 **/
public class QRCodeUtils {
    /**
     * 字符集
     */
    private static final String CHARSET = "UTF-8";
    /**
     * 二维码尺寸 长宽
     */
    private static final int QRCODE_SIZE = 400;
    /**
     * 生成二维码图片的格式
     */
    private static final String FORMAT = "jpg";
    /**
     * logo宽
     */
    private static final int LOGO_WIDTH = QRCODE_SIZE/5;
    /**
     * logo高
     */
    private static final int LOGO_HEIGHT = QRCODE_SIZE/5;

    /**
     * 设置 二维码 1 代表色 : 默认 0xFF000000 黑
     */
    private static final int ONE_COLOR = 0xFF000001;
//    private static final int ONE_COLOR = 0xFFFF0000; // 红

    /**
     * 设置 二维码 0 代表色 : 默认 0xFFFFFFFF 白
     */
    private static final int ZERO_COLOR = 0xFFFFFFFF;


    public static void createQRCode(String content, String outputPath) throws IOException {
        createQRCode(content, new FileOutputStream(outputPath));
    }

    public static void createQRCode(String content, File outputFile) throws IOException {
        createQRCode(content, new FileOutputStream(outputFile));
    }

    /**
     * 创建原始二维码（不带logo）
     * @param content 二维码内容
     * @param output 二维码(文件)输出流
     * @return void
     **/
    public static void createQRCode(String content, OutputStream output) {
        try {
            ImageIO.write(createQRCodeImage(content), FORMAT, output); // 关键类 ImageIO
            output.close();
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    public static void createQRCodeWithLogo(String content, String outputPath, String logoPath) throws IOException {
        createQRCodeWithLogo(content, new FileOutputStream(outputPath), logoPath, true);
    }

    public static void createQRCodeWithLogo(String content, String outputPath, String logoPath, boolean needCompress) throws IOException {
        createQRCodeWithLogo(content, new FileOutputStream(outputPath), logoPath, needCompress);
    }

    public static void createQRCodeWithLogo(String content, File outputFile, String logoPath, boolean needCompress) throws IOException {
        createQRCodeWithLogo(content, new FileOutputStream(outputFile), logoPath, needCompress);
    }
    /**
     * 创建二维码（带logo）
     * @param content
     * @param output
     * @param logoPath logo路径(可以是resources路径)
     * @param needCompress 是否需要压缩logo
     * @return void
     **/
    public static void createQRCodeWithLogo(String content, OutputStream output, String logoPath, boolean needCompress) {
        try {
            // 创建image对象
            BufferedImage qrcodeImage = createQRCodeImage(content);
            BufferedImage logoImage = createLogoImage(logoPath);
            // 压缩logo
            if(needCompress) {
                logoImage = compressLogoImage(logoImage);
            }
            // 绘制logo二维码
            BufferedImage newImage = drawLogoQRCode(qrcodeImage, logoImage);
            // 生成
            ImageIO.write(newImage, FORMAT, output);
            output.close();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析qrcode二维码图片
     * @param qrcodePath 二维码路径
     * @return java.lang.String
     * @throw IOException, NotFoundException
     **/
    public static String parseQRCode(String qrcodePath) throws IOException, NotFoundException {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        BufferedImage image = ImageIO.read(new File(qrcodePath));
        MultiFormatReader multiFormatReader = new MultiFormatReader(); // 主要对象 ===========
        Result result = multiFormatReader.decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image))), hints);
        return result.toString();
    }



    /**
     * （私有）创建二维码图像对象
     * @param content 二维码内容
     * @return java.awt.image.BufferedImage
     * @throw WriterException
     **/
    private static BufferedImage createQRCodeImage(String content) throws WriterException {
        if (content == null) {
            throw new NullPointerException("createQRCodeImage(String content) : content不能为null");
        }
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET); // 字符集
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 纠错级别，级别越高存储数据越少
        hints.put(EncodeHintType.MARGIN, 1); // 外边框
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter(); // ====== 主要对象
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);// 编码 生成矩阵对象
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(ONE_COLOR, ZERO_COLOR); // 配置 二维码颜色
        return MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig); // 生成图像对象（注意要设置颜色，且不能是默认的，不然使用不了rgb绘制图像，也就是黑白色）
    }

    /**
     * （私有）创建logo图像对象
     * @param logoPath
     * @return java.awt.image.BufferedImage
     * @throw IOException
     **/
    private static BufferedImage createLogoImage(String logoPath) throws IOException {
        InputStream is = QRCodeUtil.class.getClassLoader().getResourceAsStream(logoPath);
        if (is == null) {
            is = new FileInputStream(logoPath);
        }
        BufferedImage logoImage = ImageIO.read(is);
        is.close();
        return logoImage; // 关键类 ImageIO
    }

    /**
     * （私有）压缩logo，返回新的logo图像对象 （防止图片过大缩放质量差）
     * @param logoImage
     * @return java.awt.image.BufferedImage
     **/
    private static BufferedImage compressLogoImage(BufferedImage logoImage) {
        // 获取logo原始图片宽高
        int originalHeight = logoImage.getHeight();
        int originalWidth = logoImage.getWidth();
        // 压缩logo (宽高都是选择最小的)
        int height = originalHeight > LOGO_HEIGHT ? LOGO_HEIGHT : originalHeight;
        int width = originalWidth > LOGO_WIDTH ? LOGO_WIDTH : originalWidth;
        // 获取缩放
        Image scaledInstance = logoImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 创建画笔
        Graphics2D graphics = newImage.createGraphics();
        // 绘制压缩后的logo
        graphics.drawImage(scaledInstance, 0, 0, width, height, null);
        graphics.dispose();
        return newImage;
    }

    /**
     * （私有）绘制logo二维码
     * @param qrcodeImage
     * @param logoImage
     * @return void
     **/
    private static BufferedImage drawLogoQRCode(BufferedImage qrcodeImage, BufferedImage logoImage) {
        // 获取画笔对象
        Graphics2D graphics = qrcodeImage.createGraphics();
        int logoX = (QRCODE_SIZE - LOGO_WIDTH) / 2;
        int logoY = (QRCODE_SIZE - LOGO_HEIGHT) / 2;
        // 绘制logo到qrcode上
        graphics.drawImage(logoImage, logoX, logoY, LOGO_WIDTH, LOGO_HEIGHT, null);
        // 绘制logo边框（圆弧框）
        Shape round = new RoundRectangle2D.Float(logoX-2, logoY-2, LOGO_WIDTH+2, LOGO_HEIGHT+2, 10, 10);
        graphics.setStroke(new BasicStroke(5f)); // 宽度
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.draw(round);
        // 销毁画笔
        graphics.dispose();
        return qrcodeImage;
    }

    public static void main(String[] args) throws IOException, WriterException, NotFoundException {
        String content = "中文。。。。。。。。。。。。。。。。。。。。。。。";
        QRCodeUtils.createQRCodeWithLogo(content, "logoqrcode.jpg", "author.jpg");
        QRCodeUtils.createQRCode(content, "qrcode.jpg");
        System.out.println("qrcode.jpg 被解析为：" + parseQRCode("qrcode.jpg"));
        System.out.println("logoqrcode.jpg 被解析为：" + parseQRCode("logoqrcode.jpg"));
    }


}
