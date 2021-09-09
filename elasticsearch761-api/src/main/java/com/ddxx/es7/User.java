package com.ddxx.es7;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/2
 */
@Data
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = 1102496869250387411L;
    private String name;
    private String address;
    private int age;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date birthDay;

    public User(String name, String address, int age,Date birthDay) {
        this.name = name;
        this.address = address;
        this.age = age;
        this.birthDay = birthDay;
    }
}
