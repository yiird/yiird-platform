package com.yiird.spring.boot.autoconfigure.data.filestorage;

import java.io.IOException;

public class FileStorageException extends IOException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStorageException(Throwable cause) {
        super(cause);
    }
}
