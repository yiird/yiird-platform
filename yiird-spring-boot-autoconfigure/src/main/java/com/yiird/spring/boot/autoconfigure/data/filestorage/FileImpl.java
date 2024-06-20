package com.yiird.spring.boot.autoconfigure.data.filestorage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.ftp.FTPFile;


@Getter
@Setter
public class FileImpl {

    private String root;
    private Path path;
    private long size;
    private Instant modifyTime;
    private String user;
    private boolean isDirectory;
    private boolean isFile;

    private FileImpl(Path path, long size, Instant modifyTime) {
        this.path = path;
        this.size = size;
        this.modifyTime = modifyTime;
    }

    public FileImpl(FTPFile file) {
        this(Paths.get(file.getName()), file.getSize(), file.getTimestampInstant());
        setDirectory(file.isDirectory());
        setFile(file.isFile());
        setUser(file.getUser());
    }
}
