package com.shop.web.shopadmin;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.dto.ProductExecution;
import com.shop.entity.Product;
import com.shop.entity.ProductCategory;
import com.shop.entity.Shop;
import com.shop.enums.ProductStateEnum;
import com.shop.service.ProductCategoryService;
import com.shop.service.ProductService;
import com.shop.util.CodeUtil;
import com.shop.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shop")
public class ProductManagementController {
    private static final Logger logger = LoggerFactory.getLogger(ProductManagementController.class);
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    private static final int IMAGEMAXCOUNT = 6;

    @RequestMapping(value = "/listproductsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
                && (currentShop.getShopId() != null)) {
            long productCategoryId = HttpServletRequestUtil.getLong(request,
                    "productCategoryId");
            String productName = HttpServletRequestUtil.getString(request,
                    "productName");
            Product productCondition = compactProductCondition4Search(
                    currentShop.getShopId(), productCategoryId, productName);
            ProductExecution pe = productService.getProductList(
                    productCondition, pageIndex, pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getProductById(@RequestParam Long productId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (productId > -1) {
            Product product = productService.getProductById(productId);
            List<ProductCategory> productCategoryList = productCategoryService
                    .getByShopId(product.getShop().getShopId());
            modelMap.put("product", product);
            modelMap.put("productCategoryList", productCategoryList);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getproductcategorylistbyshopId", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getProductCategoryListByShopId(
            HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Shop currentShop = (Shop) request.getSession().getAttribute(
                "currentShop");
        if ((currentShop != null) && (currentShop.getShopId() != null)) {
            List<ProductCategory> productCategoryList = productCategoryService
                    .getByShopId(currentShop.getShopId());
            modelMap.put("productCategoryList", productCategoryList);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/addproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addProduct(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        ObjectMapper mapper = new ObjectMapper();
        MultipartFile thumbnail = ((MultipartRequest) request).getFile("thumbnail");
        Product product = null;
        String productStr = HttpServletRequestUtil.getString(request, "productStr");
        List<MultipartFile> productImgs = new ArrayList<MultipartFile>();
        for (int i = 0; i < IMAGEMAXCOUNT; i++) {
            MultipartFile productImg = ((MultipartRequest) request).getFile("productImg" + i);
            if (productImg != null) {
                productImgs.add(productImg);
            }
            if(CollectionUtils.isEmpty(productImgs)) {
                modelMap.put("success", false);
                modelMap.put("errMsg", "上传图片不能为空");
                return modelMap;
            }
        }
        try {
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            logger.error(e.toString());
            return modelMap;
        }
        if (product != null && thumbnail != null && productImgs.size() > 0) {
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                Shop shop = new Shop();
                shop.setShopId(currentShop.getShopId());
                product.setShop(shop);
                ProductExecution pe = productService.addProduct(product,
                        thumbnail, productImgs);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入商品信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyProduct(HttpServletRequest request) {
        boolean statusChange = HttpServletRequestUtil.getBoolean(request,
                "statusChange");
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        ObjectMapper mapper = new ObjectMapper();
        MultipartFile thumbnail = ((MultipartRequest) request).getFile("thumbnail");
        Product product = null;
        String productStr = HttpServletRequestUtil.getString(request, "productStr");
        List<MultipartFile> productImgs = new ArrayList<MultipartFile>();
        for (int i = 0; i < IMAGEMAXCOUNT; i++) {
            MultipartFile productImg = ((MultipartRequest) request).getFile("productImg" + i);
            if (productImg != null) {
                productImgs.add(productImg);
            }
        }
        try {
            product = mapper.readValue(productStr, Product.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        if (product != null) {
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute(
                        "currentShop");
                Shop shop = new Shop();
                shop.setShopId(currentShop.getShopId());
                product.setShop(shop);
                ProductExecution pe = productService.modifyProduct(product,
                        thumbnail, productImgs);
                if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", pe.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入商品信息");
        }
        return modelMap;
    }

    private Product compactProductCondition4Search(long shopId,
                                                   long productCategoryId, String productName) {
        Product productCondition = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        if (productCategoryId != -1L) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName != null) {
            productCondition.setProductName(productName);
        }
        return productCondition;
    }
}
