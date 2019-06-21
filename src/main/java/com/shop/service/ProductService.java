package com.shop.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ProductExecution;
import com.shop.entity.Product;

public interface ProductService {
    ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

    Product getProductById(long productId);

    ProductExecution addProduct(Product product, MultipartFile thumbnail, List<MultipartFile> productImgs)
            throws RuntimeException;

    ProductExecution modifyProduct(Product product, MultipartFile thumbnail,
                                   List<MultipartFile> productImgs) throws RuntimeException;
}
