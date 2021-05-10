package com.ted.converter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 文件大小转换器
 *
 * @author Ted
 */
@Component
public class SizeConverter {
    /**
     * 文件大小格式化
     * 将文件大小由Byte转为MB或者KB
     *
     * @return 转换结果
     */
    public String byteFormat(Long size) {

        BigDecimal fileSize = new BigDecimal(size);
        BigDecimal param = new BigDecimal(1024);
        int count = 0;
        while(fileSize.compareTo(param) > 0 && count < 5)
        {
            fileSize = fileSize.divide(param, BigDecimal.ROUND_HALF_UP);
            count++;
        }
        return this.format(fileSize, count);
    }

    /**
     * 文件大小格式化
     * 将文件大小由Byte转为MB或者KB
     * 取小数
     *
     * @return 转换结果
     */
    public String byteFormat(Long size, int scale) {

        BigDecimal fileSize = new BigDecimal(size);
        BigDecimal param = new BigDecimal(1024);
        int count = 0;
        while(fileSize.compareTo(param) > 0 && count < 5)
        {
            fileSize = fileSize.divide(param, scale, BigDecimal.ROUND_HALF_UP);
            count++;
        }
        return this.format(fileSize, count);
    }

    /**
     * @param fileSize 文件大小
     * @param count 转换格式
     * @return 结果
     */
    private String format(BigDecimal fileSize, int count) {
        DecimalFormat df = new DecimalFormat("#.##");
        String result = df.format(fileSize) + "";
        switch (count) {
            case 0:
                result += "B";
                break;
            case 1:
                result += "KB";
                break;
            case 2:
                result += "MB";
                break;
            case 3:
                result += "GB";
                break;
            case 4:
                result += "TB";
                break;
            case 5:
                result += "PB";
                break;
        }
        return result;
    }
}
