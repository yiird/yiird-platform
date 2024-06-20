package com.yiird.spring.boot.autoconfigure.common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static String extension(Path path) {
        String filename = path.getFileName().toString();
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static String extension(String path) {
        return extension(Paths.get(path));
    }


    public static String pathStyle(String path, OSKernel kernel) {
        return pathStyle(Paths.get(path), kernel);
    }

    public static String pathStyle(Path path, OSKernel kernel) {
        String separator = File.separator;
        String pathStr = path.toString();
        return switch (kernel) {
            case UNIX -> pathStr.replace(separator, "/");
            case WINDOWS -> pathStr.replace(separator, "\\");
            default -> pathStr;
        };
    }

    public static void main(String[] args) {

    }

}
