package com.yiird.spring.boot.autoconfigure.data.filestorage.search;

import java.nio.file.Path;
import lombok.Getter;

@Getter
public class NamedFilter implements SearchFilter {

    private final String text;
    private final TextStrategy strategy;

    public NamedFilter(String text, TextStrategy strategy) {
        this.text = text;
        this.strategy = strategy;
    }

    @Override
    public boolean filter(Object path) {
        if (path instanceof Path _path) {
            String fileName = _path.getFileName().toString();
            return switch (strategy) {
                case END_WITH -> fileName.endsWith(text);
                case START_WITH -> fileName.startsWith(text);
                case CONTAINS -> fileName.contains(text);
            };
        }
        return false;
    }
}
