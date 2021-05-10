package com.ted.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * APP配置
 *
 * @author Ted
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
