package com.yiird.spring.boot.autoconfigure.data.filestorage;

@FunctionalInterface
public interface FileFilter {

    boolean accept(FileImpl file);
}
