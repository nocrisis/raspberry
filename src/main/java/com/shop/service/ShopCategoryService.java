package com.shop.service;

import java.util.List;

import com.shop.entity.ShopCategory;

public interface ShopCategoryService {

    List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);

}
