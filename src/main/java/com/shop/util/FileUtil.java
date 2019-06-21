package com.shop.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileUtil {/*系统的分隔符*/
    private static String sperator = System.getProperty("file.separator");
    private static final Random r = new Random();
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    public static String getImgBasePath() {//返回项目图片根路径

        String os = System.getProperty("os.name");
        String basePath = "";
        if (os.toLowerCase().startsWith("win")) {
            basePath = "Q:/A my pic/project/shop/";
        } else {
            basePath = "/home/xiangze/image/";/*win \\, linux /*/
        }
        basePath = basePath.replace("/", sperator);
        return basePath;
    }

    public static String getShopImagePath(long shopId) {//返回项目图片子路径
        String imagePath = "upload/item/shop/" + shopId + "/";
        return imagePath.replace("/", sperator);
    }

    //生成随机文件名时间+5位随机数
    public static String getRandomFileName() {
        // 获取随机的5位数(0-89999)+10000之间
        int rannum = r.nextInt(89999) + 10000;
        String nowTimeStr = sDateFormat.format(new Date());
        return nowTimeStr + rannum;
    }

    // 获取输入文件流的扩展名      cFile.getOriginalFilename();
    public static String getFileExtension(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    //创建目标路径所涉及到的目录，即upload/item/shop/{shopId}/xxx.jpg,这三个文件夹都得自动创建
    public static void makeDirPath(String targetAddr) {
        String realFileParentPath = FileUtil.getImgBasePath() + targetAddr;
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
        File fileOrPath = new File(FileUtil.getImgBasePath() + storePath);
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

}
