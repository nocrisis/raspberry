package com.shop.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.entity.Area;
import com.shop.service.AreaService;

import org.slf4j.Logger;

@Controller
@RequestMapping("/superadmin")
public class AreaController {
    private static final Logger logger = LoggerFactory.getLogger(AreaController.class);
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listArea", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listArea() {
        logger.info("===start===");
        long startTime = System.currentTimeMillis();
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<Area> list = new ArrayList<Area>();
        try {
            list = areaService.getAreaList();
            modelMap.put("rows", list);
            modelMap.put("total", list.size());
        } catch (Exception e) {
            e.printStackTrace();
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
        }
        logger.error("test Error!");
        long endTime = System.currentTimeMillis();

        logger.debug("costTime[{}ms]", endTime - startTime);
        logger.info("===end===");
        return modelMap;
    }
}
