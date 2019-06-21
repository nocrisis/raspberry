package com.shop.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;

import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
//MultipartFile

/**
 * @author Ryu
 */

public class

ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    private static final String waterMarkImageUrl="static/image/earring.PNG";


    //  处理缩略图                    spring自带的文件处理对象          (shopImg, dest) /A my pic/prorject/brothers.jpg,  upload\item\shop\18
    public static String generateThumbnail(MultipartFile thumbnail, String targetAddr) {
        String realFileName =FileUtil.getRandomFileName();
        String extension = FileUtil.getFileExtension(thumbnail);
        FileUtil.makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;
        logger.debug("current relativeAddr is:" + relativeAddr);
        File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
        logger.debug("current complete addr is:" + FileUtil.getImgBasePath() + relativeAddr);
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
            Thumbnails.of(thumbnail.getInputStream()).size(300, 300)
                    .watermark(Positions.BOTTOM_RIGHT,getImageByUrl(waterMarkImageUrl) , 0.25f)
                    .outputQuality(0.8f).toFile(dest);
        } catch (IOException e) {
            throw new RuntimeException("创建缩略图失败：" + e.toString());
        }
        return relativeAddr;//upload\item\shop\18\2018051512414137334.jpg
    }//upload\item\shop\19\2018051512431867799.jpg



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
    }



    public static String generateNormalImg(MultipartFile thumbnail, String targetAddr) {
        String realFileName = FileUtil.getRandomFileName();
        String extension = FileUtil.getFileExtension(thumbnail);
        FileUtil.makeDirPath(targetAddr);
        String relativeAddr = targetAddr + realFileName + extension;
        File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
        try {
            Thumbnails.of(thumbnail.getInputStream()).size(337, 640).outputQuality(0.5f).toFile(dest);
        } catch (IOException e) {
            throw new RuntimeException("创建缩略图失败：" + e.toString());
        }
        return relativeAddr;
    }

    public static List<String> generateNormalImgs(List<MultipartFile> imgs, String targetAddr) {
        int count = 0;
        List<String> relativeAddrList = new ArrayList<String>();
        if (imgs != null && imgs.size() > 0) {
            FileUtil.makeDirPath(targetAddr);
            for (MultipartFile img : imgs) {
                String realFileName = FileUtil.getRandomFileName();
                String extension = FileUtil.getFileExtension(img);
                String relativeAddr = targetAddr + realFileName + count + extension;
                File dest = new File(FileUtil.getImgBasePath() + relativeAddr);
                count++;
                try {
                    Thumbnails.of(img.getInputStream()).size(600, 300).outputQuality(0.5f).toFile(dest);
                } catch (IOException e) {
                    throw new RuntimeException("创建图片失败：" + e.toString());
                }
                relativeAddrList.add(relativeAddr);
            }
        }
        return relativeAddrList;
    }


	/*public static void main(String[] args) throws IOException {
		String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		Thumbnails.of(new File("/A my pic/lhmsd/brothers.jpg")).size(300, 300)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/earring.png")), 0.25f)
				.outputQuality(0.8f).toFile("/A my pic/lhmsd/brothers_new.jpg");
				
		MultipartFile thumbnail;
		String targetAddr="D:/A my pic/prorject/";
		ImageUtil.generateThumbnail(thumbnail, targetAddr);
	}*/
}
