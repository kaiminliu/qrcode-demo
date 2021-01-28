# qrcode-demo
##zxing生成带logo二维码

### 使用java生成QRCode二维码

#### 需要引入`zxing`依赖：

```xml
<!--   google zxing qrcode生成依赖  -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.3.0</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.3.0</version>
</dependency>
```



#### 生成二维码

```java
/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/1/27 15:02
 * @decription zxing生成
 **/
public class CreateQRCode {
    public static void main(String[] args) {
        // vCard2.1
        String content = "BEGIN:VCARD"+"\n"
                +"VERSION:2.1"+"\n"
                +"N:姓;名"+"\n"
                +"FN:姓名"+"\n"
                +"NICKNAME:nickName"+"\n"
                +"ORG:公司;部门"+"\n"
                +"TITLE:职位"+"\n"
                +"TEL;WORK;VOICE:电话1"+"\n"
                +"TEL;WORK;VOICE:电话2"+"\n"
                +"TEL;HOME;VOICE:电话1"+"\n"
                +"TEL;HOME;VOICE:电话2"+"\n"
                +"TEL;CELL;VOICE:13590342862"+"\n"
                +"TEL;PAGER;VOICE:0755"+"\n"
                +"TEL;WORK;FAX:传真"+"\n"
                +"TEL;HOME;FAX:传真"+"\n"
                +"ADR;WORK:;;单位地址;深圳;广东;433000;国家"+"\n"
                +"ADR;HOME;POSTAL; PARCEL:;;街道地址;深圳;广东;433330;中国"+"\n"
                +"URL:网址"+"\n"
                +"URL:单位主页"+"\n"
                +"EMAIL;PREF;INTERNET:邮箱地址"+"\n"
                +"X-QQ:11111111"+"\n"
                +"END:VCARD" ;
        createQRCode(content);
    }
    public static void createQRCode(String content){
        createQRCode(content, "QRCode.png");
    }
    public static void createQRCode(String content, String fileName){
        createQRCode(content, 500, 500, "png", fileName);
    }
    /**
     * 二维码生成
     * @date 2021/1/27 15:34
     * @param content 内容
     * @param width 宽
     * @param height 高
     * @param format 图片格式
     * @param fileName 生成图片的文件名
     * @return void
     **/
    public static void createQRCode(String content, int width, int height, String format, String fileName){
        // 二维码配置
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 二维码编码utf-8
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 纠错级别（L < M < Q < H） 级别越高，存储数据越少
        hints.put(EncodeHintType.MARGIN, 1); // 外边框大小
        // 生成二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path file = new File(fileName).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

运行程序，会在项目根目录下生成 `QRCode.png`图片

![](https://liuyou-images.oss-cn-hangzhou.aliyuncs.com/markdown/image-20210128130326318.png)

#### 解析二维码

```java
/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/1/27 15:46
 * @decription 解析二维码
 **/
public class ReadQRCode {
    public static void main(String[] args) throws IOException, NotFoundException {
        System.out.println("解析的结果: \n " + readQRcode());
    }

    /**
     * 解析二维码
     * @date 2021/1/27 15:51
     * @return java.lang.String
     **/
    public static String readQRcode() throws NotFoundException, IOException {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Map hints = new HashMap();
        File file = new File("QRCode.png");
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        BufferedImage bufferedImage = ImageIO.read(file);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        Result result = multiFormatReader.decode(binaryBitmap, hints);
        System.out.println(result);
        System.out.println("内容 : " + result.getText());
        System.out.println("编码格式 : " + result.getBarcodeFormat());
        return result.toString();
    }
}

```

运行程序，控制台输出解析结果：

![](https://liuyou-images.oss-cn-hangzhou.aliyuncs.com/markdown/image-20210128130438454.png)

#### 二维码工具类

将上述二维码生成和解析进行封装，并添加带logo的二维码生成

```java
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
```

**测试结果展示：**

![](https://liuyou-images.oss-cn-hangzhou.aliyuncs.com/markdown/image-20210128131044268.png)





















