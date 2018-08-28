package com.shop.web.shopadmin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/shopadmin", method = {RequestMethod.GET})
public class ShopAdminController {
    @RequestMapping(value = "/shopmanagement")
    public String shopManagement() {
        return "shop/shopmanagement";
    }
    @RequestMapping(value = "/shopoperation")
    public String shopOperation() {
        return "shop/shopoperation";/* bean id="viewResolver"的作用*/
    }//在spring-web中前缀后缀已定义可省略直接html的名字，返回/web-inf/html下哪一个htmlString路径路由转发
    @RequestMapping(value = "/shoplist")
    public String shopList() {
        return "shop/shoplist";
    }

}
