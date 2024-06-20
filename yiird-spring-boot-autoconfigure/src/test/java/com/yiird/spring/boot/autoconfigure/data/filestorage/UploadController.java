package com.yiird.spring.boot.autoconfigure.data.filestorage;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class UploadController {

    @RequestMapping("/upload")
    @CrossOrigin
    public String upload(MultipartFile file) {
        System.out.println(file);
        return "true";
    }

    @RequestMapping("/patch/{id}")
    @CrossOrigin
    public String patch(HttpServletRequest request) {
        System.out.println(request);
        return "true";
    }

}
