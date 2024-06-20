package com.yiird.spring.boot.autoconfigure.data.filestorage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class StorageState {

    private final List<String> files = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger fileCount;
    private final AtomicInteger processedCount;

    public StorageState() {
        this(0);
    }

    public StorageState(Integer fileCount) {
        this.fileCount = new AtomicInteger(fileCount);
        this.processedCount = new AtomicInteger(0);
    }

    /**
     * 增加文件总数量
     *
     * @param deltaCount 增量
     */
    public void incrementFileCount(int deltaCount) {
        fileCount.addAndGet(deltaCount);
    }

    public void processedCount(Integer count) {
        processedCount.addAndGet(count);
    }

    public StateType type() {
        int file_count = fileCount.get();
        int processed_count = processedCount.get();

        if (file_count == 0) {
            return StateType.NONE;
        }

        StateType type;
        if (file_count > processed_count) {
            type = StateType.SOME;
        } else if (file_count == processed_count) {
            type = StateType.ALL;
        } else {
            type = StateType.NONE;
        }
        return type;
    }


    public enum StateType {
        ALL, NONE, SOME
    }
}
