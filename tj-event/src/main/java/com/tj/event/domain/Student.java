package com.tj.event.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Student {

    @Excel(name = "名字")
    private String name;


    @Excel(name = "年龄")
    private int age;



}
