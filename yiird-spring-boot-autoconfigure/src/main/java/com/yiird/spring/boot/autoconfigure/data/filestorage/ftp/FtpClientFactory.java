package com.yiird.spring.boot.autoconfigure.data.filestorage.ftp;

import com.yiird.spring.boot.autoconfigure.data.filestorage.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {

    private final FtpConfig config;

    public FtpClientFactory(FtpConfig config) {
        this.config = config;
    }

    @Override
    public FTPClient create() throws Exception {
        FTPClient client;

        if (Protocol.FTPS.equals(config.getProtocol())) {
            client = new FTPSClient();
        } else {
            client = new FTPClient();
        }

        client.setControlEncoding(config.getEncoding());
        client.setConnectTimeout(config.getConnectTimeout());

        client.connect(config.getHost(), config.getPort());
        int replyCode = client.getReplyCode();

        if (FTPReply.isPositiveCompletion(replyCode)) {
            if (client.login(config.getUsername(), config.getPassword())) {
                client.setBufferSize(config.getBufferSize());
                client.setFileType(FTP.BINARY_FILE_TYPE);
                if (config.isPassiveMode()) {
                    client.enterLocalPassiveMode();
                }
            } else {
                throw new FileStorageException(String.format("FTP 登录失败,用户名:%s 密码:%s\n%s", config.getUsername(), config.getPassword(), client.getReplyString()));
            }

        } else {
            client.disconnect();
            throw new FileStorageException(String.format("FTP 服务器拒绝连接\n错误码:%s\n%s", replyCode, client.getReplyString()));
        }
        return client;

    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> p, DestroyMode destroyMode) throws Exception {
        FTPClient object = p.getObject();
        if (object.isConnected()) {
            object.abort();
        }
        object.disconnect();
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        try {
            FTPClient object = p.getObject();
            return object.isAvailable() && object.isConnected() && object.sendNoOp();
        } catch (Exception e) {
            return false;
        }
    }
}
