package com.ddxx.es7.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/3
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String name;
    private String img;
    private String price;
}
