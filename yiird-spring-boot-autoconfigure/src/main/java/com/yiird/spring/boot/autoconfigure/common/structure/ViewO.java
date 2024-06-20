package com.yiird.spring.boot.autoconfigure.common.structure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewO<D> {

    private Integer code;
    private String message;
    private D data;

}
