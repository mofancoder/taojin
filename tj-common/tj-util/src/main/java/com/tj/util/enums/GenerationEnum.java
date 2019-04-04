package com.tj.util.enums;

import lombok.Getter;

@Getter
public enum GenerationEnum {
    king(1, "王者"),
    gold(2, "钻石"),
    sliver(3, "铂金"),
    bronze(4, "黄金");
    private String name;
    private Integer generation;

    GenerationEnum(Integer generation, String name) {
        this.generation = generation;
        this.name = name;
    }

    public static String nameOf(int code) {
        GenerationEnum[] values = values();
        for (GenerationEnum v : values) {
            if (v.generation == code) {
                return v.name;
            }
        }
        return bronze.name;
    }

}
