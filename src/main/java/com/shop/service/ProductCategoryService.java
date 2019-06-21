package com.shop.service;

import java.util.List;

import com.shop.dto.ProductCategoryExecution;
import com.shop.entity.ProductCategory;

public interface ProductCategoryService {
    /**
     * 查询指定某个店铺下的所有商品类别信息
     *
     * @param long shopId
     * @return List<ProductCategory>
     */
    List<ProductCategory> getByShopId(long shopId);

    /**
     * @param productCategory
     * @return
     * @throws RuntimeException
     */
    ProductCategoryExecution batchAddProductCategory(
            List<ProductCategory> productCategoryList) throws RuntimeException;

    /**
     * @param productCategoryId
     * @param shopId
     * @return
     * @throws RuntimeException
     */
    ProductCategoryExecution deleteProductCategory(long productCategoryId,
                                                   long shopId) throws RuntimeException;
}
