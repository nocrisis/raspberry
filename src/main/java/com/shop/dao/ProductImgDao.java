package com.shop.dao;

import java.util.List;

import com.shop.entity.ProductImg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductImgDao {

    List<ProductImg> queryProductImgList(long productId);

    int batchInsertProductImg(List<ProductImg> productImgList);

    int deleteProductImgByProductId(long productId);
}
