package com.yiird.spring.boot.autoconfigure.common.structure;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValue<V> {

    private String key;
    private V value;
}
