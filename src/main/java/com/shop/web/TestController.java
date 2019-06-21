package com.shop.web;

import com.shop.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public static Map<String, Object> testUpload(HttpServletRequest request, String Code, String YPBH) {
        logger.debug("YPBH + Code = ", YPBH, Code);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        MultipartFile multipartFile = ((MultipartRequest) request).getFile("file");
        if (multipartFile == null) {
            resultMap.put("Result", false);
            resultMap.put("MessageInfo", "上传文件为空");
            return resultMap;
        }
        String fileName = multipartFile.getOriginalFilename();
        String path = FileUtil.getImgBasePath() + "test";
        //获取指定文件或文件夹在工程中真实路径，getRequest()这个方法是返回一个HttpServletRequest，封装这个方法为了处理编码问题
        FileOutputStream fos = null;//打开FileOutStrean流
        try {
            fos = FileUtils.openOutputStream(new File(path + "/" + fileName));
            //将MultipartFile file转成二进制流并输入到FileOutStrean
            IOUtils.copy(multipartFile.getInputStream(), fos);
        } catch (IOException e) {
            logger.error("upload error", e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                logger.error("close fos error", e.getMessage());
            }
        }
        resultMap.put("Result", true);
        resultMap.put("MessageInfo", "上传成功");
        return resultMap;
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> export(String Code, String YPBH) throws IOException {
        logger.debug("YPBH + Code = ", YPBH, Code);
        HttpHeaders headers = new HttpHeaders();
        String filePath = "Q:\\A my pic\\project\\shop\\test\\2019_06_21_14_37_42.jpg";
        File file = new File(filePath);

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "耳机收纳器");

        return new ResponseEntity<>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

}
