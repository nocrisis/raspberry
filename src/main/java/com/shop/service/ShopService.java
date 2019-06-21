package com.shop.service;

import java.io.File;
import java.io.InputStream;

import com.shop.dto.ShopExecution;
import com.shop.entity.Shop;
import com.shop.exceptions.ShopOperationException;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Ryu
 */
public interface ShopService {

    ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);

    /**
     * @param shop
     * @param shopImg
     * @return 创建店铺，注册店铺信息，包括图片处理
     */
    ShopExecution addShop(Shop shop, MultipartFile shopImg) throws ShopOperationException;

    /**
     * @param shopId
     * @return 按shopId查询店铺
     */
    Shop getByShopId(long shopId);

    /**
     * 更新店铺信息，包括图片处理
     *
     * @param shop
     * @param shopImg

     * @return
     * @throws ShopOperationException
     */
    ShopExecution modifyShop(Shop shop, MultipartFile shopImg) throws ShopOperationException;
}
