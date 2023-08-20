package com.ccl.blog.controller;

import com.ccl.blog.utils.QiniuUtils;
import com.ccl.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadCtroller {

    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){
        // 原始文件名称
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");

        // 上传 七牛云
        boolean upload = qiniuUtils.upload(file,fileName);
        if(upload){
         return Result.success(QiniuUtils.url +fileName );
        }
        return Result.fail(2001,"图像上传失败");
    }
}
