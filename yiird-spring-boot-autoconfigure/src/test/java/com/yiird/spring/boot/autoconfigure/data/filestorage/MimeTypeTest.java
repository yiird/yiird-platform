package com.yiird.spring.boot.autoconfigure.data.filestorage;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.Test;

@Slf4j
public class MimeTypeTest {

    @Test
    public void getType() throws IOException {
        String filename = "/Users/loufei/works/temp/会议.ofd";

        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);
        MediaType detect = new Tika().getDetector().detect(null, metadata);

        MediaType baseType = detect.getBaseType();
        Map<String, String> parameters = detect.getParameters();
        String subtype = detect.getSubtype();
        String type = detect.getType();

        log.info("detect:{}", detect);
        log.info("baseType:{}", baseType);
        log.info("parameters:{}", parameters);
        log.info("subtype:{}", subtype);
        log.info("type:{}", type);

    }

}
