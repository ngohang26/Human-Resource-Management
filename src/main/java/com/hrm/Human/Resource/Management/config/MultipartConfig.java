package com.hrm.Human.Resource.Management.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
@Configuration

public class MultipartConfig {
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("1024MB"));
        factory.setMaxRequestSize(DataSize.parse("1024MB"));
        return factory.createMultipartConfig();
    }}
