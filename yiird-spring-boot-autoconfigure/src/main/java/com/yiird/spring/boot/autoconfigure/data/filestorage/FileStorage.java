package com.yiird.spring.boot.autoconfigure.data.filestorage;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileStorage {

    boolean store(InputStream source, Path dir, String name, long size, Processor processor);

    List<FileImpl> find(Path dir, FileFilter filter);

    boolean move(Path from, Path to) ;

    FileImpl get(Path path);

    InputStream getStream(Path target);

    FileImpl write(Path target, OutputStream os, Processor processor);

    boolean remove(Path target);
}
