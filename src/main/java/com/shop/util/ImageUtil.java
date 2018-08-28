package com.shop.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;

import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.util.ResourceUtils;
//CommonsMultipartFile

/**
 * @author Ryu
 */

public class

ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final Random r = new Random();
    private static final String waterMarkImageUrl="static/image/earring.PNG";


    //  处理缩略图                    spring自带的文件处理对象          (shopImg, dest) /A my pic/prorject/brothers.jpg,  upload\item\shop\18
    public static String generateThumbnail(InputStream thumbnailInputStream, String fileName, String targetAddr) {
        String realFileName = getRandomFileName();
        String extension = getFileExtension(fileName);
        makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;
        logger.debug("current relativeAddr is:" + relativeAddr);
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        logger.debug("current complete addr is:" + PathUtil.getImgBasePath() + relativeAddr);
        //打水印
        /*try {
            String path = ResourceUtils.getURL("classpath:").getPath();
            File waterMark = new File( path+"/image/earring.PNG");
            FileInputStream fis = new FileInputStream(waterMark);
            byte[] b = new byte[fis.available()];
            fis.read(b, 0, b.length);
            new String(b);
            logger.info("watermark {}", b);
        } catch (IOException e){
            throw new RuntimeException(e.toString());
        }*/
        try {//thumbnail.getInputStream()
            Thumbnails.of(thumbnailInputStream).size(300, 300)
                    .watermark(Positions.BOTTOM_RIGHT,getImageByUrl(waterMarkImageUrl) , 0.25f)
                    .outputQuality(0.8f).toFile(dest);
        } catch (IOException e) {
            throw new RuntimeException("创建缩略图失败：" + e.toString());
        }
        return relativeAddr;//upload\item\shop\18\2018051512414137334.jpg
    }//upload\item\shop\19\2018051512431867799.jpg

    //生成随机文件名时间+5位随机数
    public static String getRandomFileName() {
        // 获取随机的5位数(0-89999)+10000之间
        int rannum = r.nextInt(89999) + 10000;
        String nowTimeStr = sDateFormat.format(new Date());
        return nowTimeStr + rannum;
    }

    private static BufferedImage getImageByUrl(String url) {
        InputStream imageIS = ImageUtil.class.getClassLoader().getResourceAsStream(url);
        //InputStream is= Thread.currentThread().getContextClassLoader().getResourceAsStream(url)
        //结果为BufferedImge类型
        // 或者ImageIO.read(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + waterMarkImageUrl))
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageIS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    };

    // 获取输入文件流的扩展名      cFile.getOriginalFilename();
    private static String getFileExtension(String fileName) {
		/*String originalFileName = cFile.getName();
		return originalFileName.substring(originalFileName.lastIndexOf("."));*/
        return fileName.substring(fileName.lastIndexOf("."));
    }

    //创建目标路径所涉及到的目录，即upload/item/shop/{shopId}/xxx.jpg,这三个文件夹都得自动创建
    private static void makeDirPath(String targetAddr) {
        String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    /**
     * storePath是文件的路劲还是目录的路径，
     * 如果storePath是文件路径则删除该文件，
     * 如果storePath是目录路径则删除该目录下的所有文件
     *
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath) {
        File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
        if (fileOrPath.exists()) {//Q:\A my pic\project\shop\+upload\item\shop\26\2018071210142833726.jpg
            if (fileOrPath.isDirectory()) {//目录
                File files[] = fileOrPath.listFiles();//目录下的文件
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            fileOrPath.delete();//文件
        }
    }

	/*public static void main(String[] args) throws IOException {
		String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		Thumbnails.of(new File("/A my pic/lhmsd/brothers.jpg")).size(300, 300)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/earring.png")), 0.25f)
				.outputQuality(0.8f).toFile("/A my pic/lhmsd/brothers_new.jpg");
				
		CommonsMultipartFile thumbnail;
		String targetAddr="D:/A my pic/prorject/";
		ImageUtil.generateThumbnail(thumbnail, targetAddr);
	}*/
}
