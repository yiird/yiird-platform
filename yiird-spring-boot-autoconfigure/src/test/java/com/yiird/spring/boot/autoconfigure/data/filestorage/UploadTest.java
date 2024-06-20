package com.yiird.spring.boot.autoconfigure.data.filestorage;

import java.io.FileInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UploadController.class)
public class UploadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFileUpload() throws Exception {
        FileInputStream fis = new FileInputStream("/Users/loufei/works/temp/01.jpg");



        MockMultipartFile file = new MockMultipartFile(
            "file",
            "01.jpg",
            "image/jpeg",
            fis
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("文件上传成功"));
    }
}
