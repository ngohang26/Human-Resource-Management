package com.hrm.Human.Resource.Management;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public abstract class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    // ...
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Optionally also set maxFileSize, maxRequestSize, fileSizeThreshold
        registration.setMultipartConfig(new MultipartConfigElement("/uploads"));
    }
}
