package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
public class commonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

            log.info("文件上传：{}", file.getOriginalFilename());
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
        if (originalFilename != null) {
             suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName= UUID.randomUUID() +suffix;
        String filePath= null;
        try {
            filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("文件地址:{}",filePath);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
        }
}
