package com.gao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BlogConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器并反向排除一些不需要拦截的页面
        registry.addInterceptor((new LoginInterceptor()))
                .excludePathPatterns("/css/**").excludePathPatterns("/fonts/**")
                .excludePathPatterns("/images/**").excludePathPatterns("/js/**")
                .excludePathPatterns("plugins/editor/**").excludePathPatterns("/")
                .excludePathPatterns("/login").excludePathPatterns("a/*");
    }
}
