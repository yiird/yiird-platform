package com.yiird.spring.boot.autoconfigure.common.structure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreeNode<O> {

    private String key;
    private String parentKey;
    private String text;
    private O obj;
}
