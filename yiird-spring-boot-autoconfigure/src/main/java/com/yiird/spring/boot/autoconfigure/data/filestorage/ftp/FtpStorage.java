package com.yiird.spring.boot.autoconfigure.data.filestorage.ftp;


import com.yiird.spring.boot.autoconfigure.common.FileUtil;
import com.yiird.spring.boot.autoconfigure.common.OSKernel;
import com.yiird.spring.boot.autoconfigure.data.filestorage.FileFilter;
import com.yiird.spring.boot.autoconfigure.data.filestorage.FileImpl;
import com.yiird.spring.boot.autoconfigure.data.filestorage.FileStorage;
import com.yiird.spring.boot.autoconfigure.data.filestorage.Processor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Ftp存储
 * <p>
 * 支持：1、目录上传、2、多线程上传
 */
@Slf4j
public class FtpStorage implements FileStorage {

    private final GenericObjectPool<FTPClient> pool;

    private final FtpConfig config;

    public FtpStorage(FtpConfig config) {
        this.config = config;
        pool = new GenericObjectPool<>(new FtpClientFactory(config));
        GenericObjectPoolConfig<FTPClient> poolConfig = config.getPool();

        if (Objects.nonNull(poolConfig)) {
            pool.setConfig(poolConfig);
        }
    }


    @Override
    public boolean store(InputStream source, Path dir, String name, long size, Processor processor) {
        boolean flag = false;
        dir = Paths.get(config.getRoot(), dir.toString());
        String targetDir = FileUtil.pathStyle(dir, OSKernel.UNIX);
        String targetFile = FileUtil.pathStyle(dir.resolve(name), OSKernel.UNIX);
        FTPClient client = null;

        try {
            client = pool.borrowObject();

            // 设置上传进度监听器
            addProcessorListener(client, size, processor);

            if (client.makeDirectory(targetDir) && client.changeWorkingDirectory(targetDir) && client.storeFile(targetFile, source)) {
                flag = true;
            } else {
                log.error("在Ftp服务器上创建文件失败\n错误码:{}\n{}", client.getReplyCode(), client.getReplyString());
            }
            removeProcessorListener(client);
        } catch (Exception e) {
            log.error("在Ftp服务器上创建文件失败: {}", targetFile, e);
        } finally {
            if (Objects.nonNull(client)) {
                try {
                    client.changeWorkingDirectory(FileUtil.pathStyle(config.getRoot(), OSKernel.UNIX));
                } catch (IOException ignore) {
                } finally {
                    pool.returnObject(client);
                }
            }
        }
        return flag;
    }


    private void addProcessorListener(FTPClient client, long size, Processor processor) {
        client.setCopyStreamListener(new ProcessorListener(size, processor));
    }

    private void removeProcessorListener(FTPClient client) {
        client.setCopyStreamListener(null);
    }

    @Override
    public List<FileImpl> find(Path dir, FileFilter filter) {
        FTPClient client = null;
        try {
            dir = Paths.get(config.getRoot(), dir.toString());
            client = pool.borrowObject();
            FTPFile[] files = client.listFiles(FileUtil.pathStyle(dir, OSKernel.UNIX), (ftpFile) -> {
                FileImpl file = new FileImpl(ftpFile);
                relativizePath(file);
                return !Objects.nonNull(filter) || filter.accept(file);
            });
            return Arrays.stream(files).map(it -> {
                FileImpl file = new FileImpl(it);
                relativizePath(file);
                return file;
            }).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                pool.returnObject(client);
            }
        }
    }

    private void relativizePath(FileImpl file) {
        Path root = Paths.get(config.getRoot());
        Path path = file.getPath();
        if (path.startsWith(root)) {
            file.setPath(root.relativize(path));
        }
    }

    @Override
    public boolean move(Path from, Path to) {
        FTPClient client = null;
        boolean flag = false;
        try {
            from = Paths.get(config.getRoot(), from.toString());
            to = Paths.get(config.getRoot(), to.toString());
            client = pool.borrowObject();
            String toParent = FileUtil.pathStyle(to.getParent(), OSKernel.UNIX);
            if (client.makeDirectory(toParent) && client.changeWorkingDirectory(toParent) && client.rename(FileUtil.pathStyle(from, OSKernel.UNIX),
                FileUtil.pathStyle(to, OSKernel.UNIX))) {
                flag = true;
            } else {
                log.error("移动文件失败\n错误码:{}\n{}", client.getReplyCode(), client.getReplyString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                try {
                    client.changeWorkingDirectory(config.getRoot());
                    pool.returnObject(client);
                } catch (IOException e) {
                    log.error("切换到根目录失败\n错误码:{}\n{}", client.getReplyCode(), client.getReplyString(), e);
                }
            }
        }
        return flag;
    }

    @Override
    public FileImpl get(Path path) {
        FTPClient client = null;
        try {
            path = Paths.get(config.getRoot(), path.toString());
            client = pool.borrowObject();
            FTPFile ftpFile = client.mlistFile(FileUtil.pathStyle(path, OSKernel.UNIX));
            FileImpl file = new FileImpl(ftpFile);
            relativizePath(file);
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                pool.returnObject(client);
            }
        }
    }

    @Override
    public InputStream getStream(Path target) {
        FTPClient client = null;
        try {
            target = Paths.get(config.getRoot(), target.toString());
            client = pool.borrowObject();
            return client.retrieveFileStream(FileUtil.pathStyle(target, OSKernel.UNIX));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                pool.returnObject(client);
            }
        }
    }

    @Override
    public FileImpl write(Path target, OutputStream os, Processor processor) {
        FTPClient client = null;
        try {
            target = Paths.get(config.getRoot(), target.toString());
            client = pool.borrowObject();
            FTPFile ftpFile = client.mlistFile(target.toString());
            addProcessorListener(client, ftpFile.getSize(), processor);
            client.retrieveFile(FileUtil.pathStyle(target, OSKernel.UNIX), os);
            removeProcessorListener(client);
            FileImpl file = new FileImpl(ftpFile);
            relativizePath(file);
            return file;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                pool.returnObject(client);
            }
        }
    }

    @Override
    public boolean remove(Path target) {
        FTPClient client = null;
        try {
            target = Paths.get(config.getRoot(), target.toString());
            client = pool.borrowObject();
            return client.deleteFile(FileUtil.pathStyle(target, OSKernel.UNIX));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(client)) {
                pool.returnObject(client);
            }
        }
    }

}
