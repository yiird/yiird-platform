package com.yiird.spring.boot.autoconfigure.common.structure;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Page<O> {

    private Integer current;
    private Integer total;
    private List<O> content;
}
