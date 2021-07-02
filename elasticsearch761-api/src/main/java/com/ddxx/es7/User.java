package com.ddxx.es7;

import lombok.Data;
import lombok.ToString;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/2
 */
@Data
@ToString
public class User {
    private String name;
    private String address;
    private int age;

    public User(String name, String address, int age) {
        this.name = name;
        this.address = address;
        this.age = age;
    }
}
