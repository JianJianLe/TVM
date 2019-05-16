package com.tvm.tvm.util.device;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
    private static QRCodeUtil instance;

    public synchronized static QRCodeUtil getInstance(){
        if (instance==null) {
            instance = new QRCodeUtil();
        }
        return instance;
    }

    public String getQRCode(Context context, String content){
        String path_QRCode = getDiskCacheDir(context,"QrImg.bmp").getPath();
        qrCodeCreate(content,path_QRCode);
        Log.i("Test","QR Code File Path:"+path_QRCode);
        return path_QRCode;
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
//
//
//    private void qrCodeConfig(int money) {
////        // 获取当前系统时间，保存在TimeData里
////        String TimeData = TimeUtil.dateFormat.format(new java.util.Date());
////
////        // 处理顺序号（从整型1处理为String型001）
////        OrderNumber = OrderDispose(OrderTestNumber);
////        Log.i(TAG, "OrderNumber = " + OrderNumber);
////
////        // 处理金额（从整型变为String）
////        String MoneyS = String.valueOf(money);
////        // 组合需要加密的数据
////        MD5DataSource = MachineNumber + MoneyS + TimeData + MD5Key;
////        Log.i(TAG, "MD5TestData = " + MD5DataSource);
////
////        // 对MD5原始数据进行加密
////        char[] MD5DataGet = mPrinterUnit.Md5encrypt(MD5DataSource);
////
////        // 获取从第11个字节开始的8个字节
////        char[] MD5dataCut = new char[8];
////        for (int i = 0; i < 8; i++) {
////            MD5dataCut[i] = MD5DataGet[10 + i];
////        }
////
////        // 将这8个字节转换为String型
////        MD5Data = new String(MD5dataCut);
////        Log.i(TAG, "MD5Data = " + MD5Data);
////
////        // 组合制作二维码的全部数据
////        QrCodeData = "http://wx.uuudian.com/device/LDPoint.do?device="
////                + MachineNumber + "&time=" + TimeData + "&ticket=" + TestMoney
////                + "&sign=" + MD5Data;
////        Log.i(TAG, "QrCodeData = " + QrCodeData);
////
////        // 获取保存二维码图片的路径
////        QrCodeDataPath = getDiskCacheDir(this) + File.separator + "QrImg.bmp";
////
////        // 调用打印机工具类，将数据传入制作二维码
////        mPrinterUnit.qrCodeCreate(QrCodeData, QrCodeDataPath);
////
////        // 制作完毕后获取二维码图片的流数据，转换成打印用的字节
////        try {
////            fis = new FileInputStream(new File(QrCodeDataPath));
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
//    }



//    // 打印二维码
//    private void qrCodePrint(InputStream QrData) {
//
////        jPrinterDataSend(lineSizeZeroCMD, lineSizeZeroCMD.length);
////
////        Bitmap mBitmap = BitmapFactory.decodeStream(QrData);
////
////        compressPic = compressPic(mBitmap);
////
////        qrData = draw2PxPoint(compressPic);
////
////        jPrinterDataSend(qrData, qrData.length);
//
//    }



    // 创建二维码
    private void qrCodeCreate(String QrData, String QrImgPath) {

        boolean success = createQRImage(QrData, 200, 200,
                null, QrImgPath);
        if (success)
            Log.i("Test", "Create QR Code Successful.");
        else
            Log.i("Test","Create QR Code Failed.");
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content 内容
     * @param widthPix 图片宽度
     * @param heightPix 图片高度
     * @param logoBm 二维码中心的Logo图标（可以为null）
     * @param filePath 用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    private boolean createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm,
                                        String filePath) {
        try {
            if (content == null || "".equals(content)) {
                return false;
            }

            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            // Map<encodehinttype, object=""> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 1); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,
                    widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            // 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap != null
                    && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(
                    filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /********************************二维码***************************************************/

    /**
     * 把一张Bitmap图片转化为打印机可以打印的bit(将图片压缩为360*360) 效率很高（相对于下面）
     *
     * @param bit
     * @return
     */
    private byte[] draw2PxPoint(Bitmap bit) {
        byte[] data = new byte[8811];
        int k = 0;
        for (int j = 0; j < 11; j++) {
            // 打印居中指令
            data[k++] = 0x1b;
            data[k++] = 0x61;
            data[k++] = 0x01;

            // 位图打印指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 0x21; // hex:0x21 = dec:33 m=33时，选择24点双密度打印，分辨率达到200DPI。
            data[k++] = 0x08;
            data[k++] = 0x01;

            for (int i = 0; i < 264; i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bit);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;
        }
        return data;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @param bit 位图
     * @return
     */
    private byte px2Byte(int x, int y, Bitmap bit) {
        byte b;
        int pixel = bit.getPixel(x, y);
        int red = (pixel & 0x00ff0000) >> 16; // 取高两位
        int green = (pixel & 0x0000ff00) >> 8; // 取中两位
        int blue = pixel & 0x000000ff; // 取低两位
        int gray = RGB2Gray(red, green, blue);
        if (gray < 128) {
            b = 1;
        } else {
            b = 0;
        }
        return b;
    }

    /**
     * 图片灰度的转化
     *
     * @param r
     * @param g
     * @param b
     * @return
     */
    private int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmapOrg
     */
    private Bitmap compressPic(Bitmap bitmapOrg) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = 264;
        int newHeight = 264;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight,
                Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height),
                new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }

    //进行MD5加密
    public final static char[] Md5encrypt(String plaintext) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = plaintext.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return str;
        } catch (Exception e) {
            return null;
        }
    }
    /***********************************************************************************/


}

