package com.ted.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换器
 *
 * @author Ted
 */
@Component
@PropertySource(value = "classpath:config/general.properties")
public class DateConverter {
    /**
     * 时间格式化模式
     */
    @Value("${dateFormat.pattern.YmdHis:yyyy-MM-dd HH:mm:ss}")
    private String dateFormatPattern;

    /**
     * 格式化当前时间
     *
     * @return 格式化
     */
    public String dateFormat() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);
        return format.format(date);
    }

    /**
     * 按默认模式格式化日期
     *
     * @param date 日期
     * @return 格式化
     */
    public String dateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);
        return format.format(date);
    }

    /**
     * 自定义格式化模式，格式化当前日期
     *
     * @param pattern 格式化模式
     * @return 格式化
     */
    public String dateFormatDate(String pattern) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 自定义格式化模式，格式化指定日期
     *
     * @param date    日期
     * @param pattern 格式化模式
     * @return 格式化
     */
    public String dateFormat(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 按默认模式格式化时间戳
     *
     * @param timestamp 时间戳
     * @return 格式化
     */
    public String dateFormat(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatPattern);
        return format.format(new Date(timeMillis(timestamp)));
    }

    /**
     * 自定义格式化模式，格式化当前时间戳
     *
     * @param pattern 格式化模式
     * @return 格式化
     */
    public String dateFormat(String pattern) {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(timestamp));
    }

    /**
     * 自定义格式化模式，格式化指定时间戳
     *
     * @param timestamp 时间戳
     * @param pattern   格式化模式
     * @return 格式化
     */
    public String dateFormat(long timestamp, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(timeMillis(timestamp)));
    }

    /**
     * 当前日期的时间戳
     *
     * @return 格式化当前时间戳
     */
    public long timestampFormat() {
        String dateFormat = dateFormat();
        return getTime(dateFormat, dateFormatPattern);
    }

    /**
     * 指定日期的时间戳
     *
     * @param dateFormat 模式
     * @return 时间戳
     */
    public long timestampFormat(String dateFormat) {
        return getTime(dateFormat, dateFormatPattern);
    }

    /**
     * 按指定模式，指定日期的时间戳
     *
     * @param dateFormat 模式
     * @param pattern    模式
     * @return 时间戳
     */
    public long timestampFormat(String dateFormat, String pattern) {
        return getTime(dateFormat, pattern);
    }

    /**
     * @param dateFormat 时间字符串
     * @param pattern    模式
     * @return 时间戳
     */
    public long getTime(String dateFormat, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Date date = null;
        try {
            date = simpleDateFormat.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }

    /**
     * 将指定时间戳转换为毫秒级时间戳（13位）
     *
     * @param timestamp 时间戳
     * @return 毫秒级时间戳
     */
    public long timeMillis(long timestamp) {
        long time;
        if (Long.valueOf(timestamp).toString().length() == 10) {
            time = timestamp * 1000L;
        } else {
            time = timestamp;
        }
        return time;
    }

    /**
     * 将指定时间戳转换为秒级时间戳（10位）
     *
     * @param timestamp 时间戳
     * @return 秒级时间戳
     */
    public long timeSecond(long timestamp) {
        long time;
        if (Long.valueOf(timestamp).toString().length() == 10) {
            time = timestamp;
        } else {
            String format = String.format("%010d", timestamp / 1000);
            time = Long.parseLong(format);
        }
        return time;
    }
}
