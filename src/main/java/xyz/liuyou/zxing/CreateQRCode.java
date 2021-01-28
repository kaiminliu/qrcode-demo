package xyz.liuyou.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/1/27 15:02
 * @decription zxing生成
 **/
public class CreateQRCode {
    public static void main(String[] args) {
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
