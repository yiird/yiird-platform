package com.yiird.spring.boot.autoconfigure.data.filestorage.ftp;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Getter
@Setter
public class FtpConfig {

    private boolean enable = false;

    /**
     * 主机地址
     */
    private String host;
    /**
     * 端口
     */
    private Integer port = 21;
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 密码
     */
    private String password = "";
    /**
     * 协议
     */
    private Protocol protocol;
    /**
     * 编码
     */
    private String encoding = "UTF-8";

    /**
     * 连接超时时间(秒)
     */
    private Integer connectTimeout = 0;
    /**
     * 缓冲大小
     */
    private Integer bufferSize = 1024;

    /**
     * 被动模式
     * <p>
     * 主动模式：本地开新端口，告知ftp服务端，服务端连接此端口进行数据传输
     * <p>
     * 被动模式：与ftp固定端口(默认：21)数据传输
     */
    private boolean passiveMode = true;

    /**
     * 远程Ftp服务器上针对当前项目的根目录
     * <p>
     * 默认："/" Ftp服务的根目录
     */
    private String root = "/";

    /**
     * 连接池配置
     */
    private GenericObjectPoolConfig<FTPClient> pool;

}
