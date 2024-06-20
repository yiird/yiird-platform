package com.yiird.spring.boot.autoconfigure.data.filestorage;

import com.yiird.spring.boot.autoconfigure.data.filestorage.ftp.FtpConfig;
import com.yiird.spring.boot.autoconfigure.data.filestorage.ftp.FtpStorage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
class FtpStorageTest {

    private static FtpStorage storage;

    @BeforeAll
    public static void setup() {
        FtpConfig config = new FtpConfig();
        config.setHost("192.168.110.200");
        config.setPort(21);
        config.setUsername("yiird");
        config.setPassword("123456");
        config.setRoot("/test");

        storage = new FtpStorage(config);
    }

    @Test
    public void store() throws IOException {
        String file = "/Users/loufei/works/temp/01.jpg";
        Path path = Paths.get(file);
        InputStream stream = Files.newInputStream(path);
        boolean flag = storage.store(stream, Paths.get("/abc11"), "01.jpg", Files.size(path), (percentage) -> {
            log.info("进度：{}%", String.format("%.2f%%", percentage));
        });
        log.info("成功：{}", flag);
    }

    @Test
    public void get() {
        FileImpl file = storage.get(Paths.get("/01.jpg"));
        System.out.println(file.getPath());
        System.out.println(file.getSize());
    }

    @Test
    public void remove() {
        boolean remove = storage.remove(Paths.get("/2.png"));
        log.info("删除成功：{}", remove);
    }

    @Test
    public void write() throws IOException {
        FileOutputStream os = new FileOutputStream("/Users/loufei/works/temp/000.jpg");
        storage.write(Paths.get("/01.jpg"), os, (percentage -> {
            log.info("进度：{}%", String.format("%.2f%%", percentage));
        }));
    }

    @Test
    public void move() throws IOException {
        boolean move = storage.move(Paths.get("/abc/01.jpg"), Paths.get("/abc2/02.jpg"));
        log.info("移动成功：{}", move);
    }

    @Test
    public void find() throws IOException {
        storage.find(Paths.get("/"), null).forEach(it -> log.info("path -> {}", it.getPath()));
    }

    @Test
    public void stream() {

        // 分片大小，例如10MB
        final long sliceSize = 10 * 1024 * 1024;

        try (FileInputStream fis = new FileInputStream("/Users/loufei/works/temp/26号ppt.zip");
            FileChannel fileChannel = fis.getChannel()) {

            MultipartFile file = null;

            // 文件总大小
            long fileSize = fileChannel.size();
            // 计算分片数量
            long numberOfSlices = (fileSize + sliceSize - 1) / sliceSize;

            for (long slice = 0; slice < numberOfSlices; slice++) {
                // 计算当前分片的起始位置和大小
                long position = slice * sliceSize;
                long size = Math.min(sliceSize, fileSize - position);

                // 映射当前分片到内存
                ByteBuffer buffer = fileChannel.map(MapMode.READ_ONLY, position, size);

                log.info("分片:{}", buffer.capacity());
                // 在这里处理分片中的数据
                // 例如，可以读取、处理并输出分片中的数据
                while (buffer.hasRemaining()) {
                    // 准备从缓冲区读取数据
                    buffer.get();
                    // 在这里处理缓冲区中的数据
                    // 例如，可以将缓冲区中的数据写入另一个通道

                    // 读取完成后，清空缓冲区，为下一次读取做准备
                }
                // 分片处理完毕，可以继续下一个分片
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

