package com.nageoffer.shortlink.project.service.ShortlinkServiceImpl;

import com.nageoffer.shortlink.project.service.UrlTitleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class UrlTitleServiceImpl implements UrlTitleService {


    @Override
    public String getUrlTitle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(5000)                          // 超时时间 5s
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 Chrome/120.0 Safari/537.36")
                    .get();
            return doc.title();
        } catch (IOException e) {
            throw new RuntimeException("获取页面 Title 失败: " + e.getMessage(), e);
        }
    }
}
