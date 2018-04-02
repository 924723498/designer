package com.fr.design.utils;

import com.fr.base.BaseUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.stable.StringUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * 设计器部分的图片处理工具类
 * Created by zack on 2018/3/8.
 */
public class ImageUtils {

    /**
     * 默认压缩算法,采用75%质量压缩
     *
     * @param imageFile 原文件
     * @return 压缩后的BufferedImage对象
     */
    public static BufferedImage defaultImageCompress(File imageFile) {
        try {
            return imageCompress(imageFile, 0.75f);//默认75%质量
        } catch (IOException e) {
            FRLogger.getLogger().info("image compress failed!");

        }
        return BaseUtils.readImage(imageFile.getPath());
    }

    /**
     * 图片压缩
     *
     * @param imageFile 原文件
     * @param quality   质量系数(0-1)
     * @return 压缩后的图片
     * @throws IOException
     */
    public static BufferedImage imageCompress(File imageFile, float quality) throws IOException {
        BufferedImage result = BaseUtils.readImage(imageFile.getPath());
        if (imageFile == null) {
            return null;
        }
        if (canbeCompressedToJPEG(imageFile)) {
            return jpegCompress(result, quality);
        }
        return result;
    }

    public static boolean canbeCompressedToJPEG(File imageFile) {
        if (ComparatorUtils.equals(getImageType(imageFile), "JPEG")) {//JPEG大写
            return true;
        }
        if (ComparatorUtils.equals(getImageType(imageFile), "png")) {//png小写
            return !isAlphaAreaOverload(imageFile);//少量透明度系数的png直接压缩jpg
        }
        return false;
    }

    /**
     * JPEG格式图片压缩
     *
     * @param image   压缩源图片
     * @param quality 压缩质量，在0-1之间，
     * @return 返回的字节数组
     */
    public static BufferedImage jpegCompress(BufferedImage image, float quality) throws IOException {
        if (image == null) {
            return null;
        }
        // 得到指定Format图片的writer
        Iterator<ImageWriter> iter = ImageIO
                .getImageWritersByFormatName("jpeg");// 得到迭代器
        ImageWriter writer = iter.next();

        // 得到指定writer的输出参数设置(ImageWriteParam )
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // 设置可否压缩
        iwp.setCompressionQuality(quality); // 设置压缩质量参数
        iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);

        // 开始打包图片，写入byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream ios = null;
        ByteArrayInputStream in = null;
        try {
            image = copy(image, BufferedImage.TYPE_INT_RGB);
            ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), iwp);
            in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            if (ios == null || in == null) {
                throw new IOException("The image file cannot be compressed");
            }
            return ImageIO.read(in);
        } finally {
            writer.dispose();
            ios.close();
            byteArrayOutputStream.close();
            in.close();
        }
    }

    /**
     * 判断图片中是否包含多于5%的透明区域,这个5%随便定的
     *
     * @param imageFile 目标图片
     * @return 含透明度像素占比
     */
    public static boolean isAlphaAreaOverload(File imageFile) {
        if (imageFile == null || !imageFile.isFile()) {
            return false;
        }
        BufferedImage bufferedImage = BaseUtils.readImage(imageFile.getPath());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        long alphaCount = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int a = (0xff & (rgb >> 24));
                if (a != 0xff) {
                    alphaCount++;
                }
            }
        }
        return alphaCount / (double) (width * height) > 0.05;
    }

    /**
     * 获取图片类型
     *
     * @param imageFile 图片文件
     * @return 图片类型(JPEG, PNG, GIF)
     */
    public static String getImageType(File imageFile) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) {
                return StringUtils.EMPTY;
            }
            ImageReader reader = iter.next();
            iis.close();
            return reader.getFormatName();
        } catch (IOException ignore) {
        }
        return StringUtils.EMPTY;
    }

    private static BufferedImage copy(BufferedImage img, int imageType) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, imageType);
        Graphics g = newImage.createGraphics();

        g.drawImage(img, 0, 0, null);

        g.dispose();

        return newImage;
    }
}