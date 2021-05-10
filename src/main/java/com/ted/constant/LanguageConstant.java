package com.ted.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 语言包
 *
 * @author Ted
 */
@Data
@Component
@PropertySource(value = {"classpath:config/language.properties"})
@ConfigurationProperties(prefix = "language")
public class LanguageConstant {
    private String unknown;
    private String unfounded;
    private String success;
    private String fail;
    private String duplicated;
    private String dataNull;
}
