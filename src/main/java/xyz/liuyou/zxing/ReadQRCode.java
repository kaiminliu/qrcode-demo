package xyz.liuyou.zxing;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
