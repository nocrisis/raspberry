package com.shop.dao;

import com.shop.entity.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopDao {
    //	新增店铺
    int insertShop(Shop shop);

    int updateShop(Shop shop);

    Shop queryByShopId(long shopId);
/*
* rowIndex 从第几行开始取数据
* pageSize 返回的条数
* */
    List<Shop> queryShopList(@Param("shopCondition") Shop shopCondition,
                             @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    Integer queryShopCount(@Param("shopCondition")Shop shopCondition);
}
