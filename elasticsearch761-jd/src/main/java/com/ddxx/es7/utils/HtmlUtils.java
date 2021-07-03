package com.ddxx.es7.utils;

import com.ddxx.es7.bean.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/3
 */
@Component
public class HtmlUtils {
    public List<Content> parse(String keyWord)throws Exception{
        String url="https://search.jd.com/Search?keyword="+keyWord;
        final Document document = Jsoup.parse(new URL(url), 30000);
        //System.out.println(document.html());
        final Element element = document.getElementById("J_goodsList");
        final Elements elements = element.getElementsByTag("li");

        final List<Content> contents = new ArrayList<>();
        for (Element el : elements) {
            final String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            final String name = el.getElementsByClass("p-name").eq(0).text();
            final String price = el.getElementsByClass("p-price").eq(0).text();
            contents.add(new Content(name,img,price));
        }
        return contents;
    }
}
