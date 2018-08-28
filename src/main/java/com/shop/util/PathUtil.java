package com.shop.util;

public class PathUtil {/*系统的分隔符*/
    private static String sperator = System.getProperty("file.separator");

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
}
