package com.yiird.spring.boot.autoconfigure.data.filestorage.ftp;

import com.yiird.spring.boot.autoconfigure.data.filestorage.Processor;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

public class ProcessorListener implements CopyStreamListener {

    private final long size;
    private final Processor processor;

    public ProcessorListener(long size, Processor processor) {
        this.size = size;
        this.processor = processor;
    }

    @Override
    public void bytesTransferred(CopyStreamEvent event) {

    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        processor.process(((double) totalBytesTransferred / size) * 100);
    }
}
