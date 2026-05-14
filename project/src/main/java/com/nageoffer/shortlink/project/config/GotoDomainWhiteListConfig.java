package com.nageoffer.shortlink.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Component
@ConfigurationProperties(prefix = "short-link.goto-domain.whilte-list")
public class GotoDomainWhiteListConfig {

    private Boolean enable;



    private String names;


    private List<String> details;
}
