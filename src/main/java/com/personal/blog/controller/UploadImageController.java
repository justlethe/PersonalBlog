/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: UploadImageController
 * Author:   1056427550
 * Date:     2021-3-5 21:58
 * Description: 管理图片上传
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

/**
 * 〈一句话功能简述〉<br> 
 * 〈管理图片上传〉
 *
 * @author 1056427550
 * @create 2021-3-5
 * @since 1.0.0
 */
@Controller
@CrossOrigin
public class UploadImageController {

    //绑定文件上传路径到uploadPath
    @Value("${web.upload-path}")
    private String uploadPath;

    @PostMapping("/admin/saveContentImg")
    @ResponseBody
    public JSONObject saveContentImg(@RequestParam("editormd-image-file")MultipartFile file,
                                     HttpServletRequest request){
        JSONObject object = new JSONObject();
        try {
            //先创建文件夹
            //先获取在当前系统下的文件夹路径
            String osName = System.getProperty("os.name");
            String realPath;
            if (osName.toLowerCase().startsWith("win")) {
                realPath = uploadPath;
            } else {
                realPath = "/resources/community/upload/";
            }
            File myFlie = new File(realPath);
            if (!myFlie.exists()) {
                myFlie.mkdirs();
            }
            //获取上传文件的文件名
            String fileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            fileName = uuid + "_" + fileName;
            file.transferTo(new File(realPath, fileName));
            object.put("success", 1);
            object.put("message", "上传成功");
            object.put("url", request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + fileName);
        } catch (Exception e) {

            object.put("success", 0);
            object.put("message", "上传失败");
        }

        return object;
    }
}