package com.ted.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 日期常量
 *
 * @author Ted
 */
@Data
@Component
@PropertySource(value = {"classpath:config/date.properties"})
@ConfigurationProperties(prefix = "date")
public class DateConstant {
    private String patternYmdHis;
    private String patternYmd;
    private String patternEndOfDay;
}
