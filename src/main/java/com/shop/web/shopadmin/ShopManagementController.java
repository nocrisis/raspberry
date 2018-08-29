package com.shop.web.shopadmin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.shop.dto.ShopExecution;
import com.shop.entity.Area;
import com.shop.entity.PersonInfo;
import com.shop.entity.Shop;
import com.shop.entity.ShopCategory;
import com.shop.enums.ShopStateEnum;
import com.shop.exceptions.ShopOperationException;
import com.shop.service.AreaService;
import com.shop.service.ShopCategoryService;
import com.shop.service.ShopService;
import com.shop.util.CodeUtil;
import com.shop.util.HttpServletRequestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/shop")
public class ShopManagementController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/getshopmanagementinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopManagementInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if (shopId <= 0) {
            Object currentShopObj = request.getSession().getAttribute("currentShop");
            if (currentShopObj == null) {
                modelMap.put("redirect", true);
                modelMap.put("url", "/shopadmin/shoplist");
            } else {
                Shop currentShop = (Shop) currentShopObj;
                modelMap.put("redirect", false);
                modelMap.put("shopId", currentShop.getShopId());
            }
        }else {
            Shop currentShop=new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop",currentShop);
            modelMap.put("shopId", currentShop.getShopId());
            modelMap.put("redirect", false);
        }
        return  modelMap;
    }

    @RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopList(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
//        PersonInfo user=(PersonInfo) request.getSession().getAttribute("user");
        //todo  session
        PersonInfo user = new PersonInfo();
        user.setUserId(1L);
        request.getSession().setAttribute("user", user);
        try {
            Shop shopCondition = new Shop();
            shopCondition.setUser(user);
            ShopExecution se = shopService.getShopList(shopCondition, 0, 15);
            modelMap.put("shopList", se.getShopList());
            modelMap.put("success", true);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        return modelMap;
    }

    @RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopById(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if (shopId > -1) {
            try {
                Shop shop = shopService.getByShopId(shopId);
                List<Area> areaList = areaService.getAreaList();
                modelMap.put("shop", shop);
                modelMap.put("areaList", areaList);
                modelMap.put("success", true);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", " empty shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getshopinitinfo", method = RequestMethod.GET)
    @ResponseBody//转化成json串String
    private Map<String, Object> getShopInitInfo() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
        List<Area> areaList = new ArrayList<Area>();
        try {
            shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
            areaList = areaService.getAreaList();
            modelMap.put("shopCategoryList", shopCategoryList);
            modelMap.put("areaList", areaList);
            modelMap.put("success", true);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        return modelMap;
    }


    @RequestMapping(value = "/registershop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> registerShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错的验证码");
            return modelMap;
        }
        // 1.接受并转换相应的参数，包括店铺信息和图片信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");// 与前端约定好接收"shopStr"
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try {
            shop = mapper.readValue(shopStr, Shop.class);
        } catch (Exception e) {//jackson-databind Json转实体类
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
            return modelMap;
        }
        MultipartFile shopImg = ((MultipartRequest) request).getFile("shopImg");
        // 接收图片
//        CommonsMultipartFile shopImg = null;
//        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        if (commonsMultipartResolver.isMultipart(request)) {
//            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
//            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");//前端传过来name=值
        if (shopImg == null) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "上传图片不能为空");
            return modelMap;
        }
        // 2.注册店铺
        if (shop != null && shopImg != null) {
//            PersonInfo user = new PersonInfo();//假定前端不可靠对上一层越少依赖越好，后台得到，后面会将session添加进去
//            user.setUserId(1L);
            PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
            shop.setUser(user);
//			File shopImgFile = new File(PathUtil.getImgBasePath() + ImageUtil.getRandomFileName());
//			//file 直接通过读取路径产生文件流传入
//			try {
//				shopImgFile.createNewFile();//每次生成新文件，产生垃圾文件，增加不稳定性
//			} catch (IOException e) {
//				modelMap.put("success", false);
//				modelMap.put("errMsg", "上传图片不能为空");
//				return modelMap;
//			}
//			try {
//				inputStreamToFile(shopImg.getInputStream(), shopImgFile);
//
//			} catch (IOException e) {
//				modelMap.put("success", false);
//				modelMap.put("errMsg", e.getMessage());
//				return modelMap;
//			}
            ShopExecution se;
            try {
                se = shopService.addShop(shop, shopImg.getInputStream(), shopImg.getOriginalFilename());
                if (se.getState() == ShopStateEnum.CHECK.getState()) {
                    modelMap.put("success", true);
                    List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
                    if (shopList == null || shopList.size() == 0) {
                        shopList = new ArrayList<Shop>();
                    }
                    shopList.add(se.getShop());
                    request.getSession().setAttribute("shopList", shopList);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", se.getStateInfo());
                }
            } catch (ShopOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
            } catch (IOException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
            }
            return modelMap;
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺信息");
        }
        return modelMap;
        // 3.返回结果
    }

    @RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错的验证码");
            return modelMap;
        }
        // 1.接受并转换相应的参数，包括店铺信息和图片信息
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
        // 与前端约定好接收"shopStr"
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = null;
        try {
            shop = mapper.readValue(shopStr, Shop.class);
        } catch (Exception e) {
            //jackson-databind Json转实体类
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
            return modelMap;
        }
        // 接收图片
        MultipartFile shopImg = ((MultipartRequest) request).getFile("shopImg");

        // 2.修改店铺信息
        if (shop != null && shop.getShopId() != null) {
//            PersonInfo user = new PersonInfo();
            //假定前端不可靠对上一层越少依赖越好，后台得到，后面会将session添加进去
//            user.setUserId(1L);
            PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
            shop.setUser(user);
            ShopExecution se;
            try {
                if (shopImg == null) {
                    se = shopService.modifyShop(shop, null, null);
                } else {
                    se = shopService.modifyShop(shop, shopImg.getInputStream(), shopImg.getOriginalFilename());
                }
                if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", se.getStateInfo());
                }
            } catch (ShopOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
            } catch (IOException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.getMessage());
            }
            return modelMap;
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入店铺Id");
        }
        return modelMap;
        // 3.返回结果
    }


}
//不用转换为File直接用InputStream
//	private static void inputStreamToFile(InputStream ins, File file) {
//		FileOutputStream ops = null;
//		try {
//			ops = new FileOutputStream(file);
//			int bytesRead = 0;
//			byte[] buffer = new byte[1024];
//			while ((bytesRead = ins.read(buffer)) != -1) {
//				ops.write(buffer, 0, bytesRead);//buffer读满一次1024个字节就往输出流里边写一次
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(" 调用inputStreamToFile产生异常" + e.getMessage());
//		} finally {
//			try {
//				if (ops != null) {
//					ops.close();
//				}
//				if (ins != null) {
//					ins.close();
//				}
//			} catch (IOException e) {
//				throw new RuntimeException(" inputStreamToFile关闭io产生异常" + e.getMessage());
//			}
//		}
//	}


