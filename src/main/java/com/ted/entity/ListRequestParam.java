package com.ted.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * RequestParam实体类
 * 主要用于列表的请求参数
 * 几乎所有列表的请求参数都差不多
 * 有个别的列表需要特殊的参数
 *
 * @author Ted
 */
@Data
@PropertySource(value = {"classpath:config/general.properties"})
public class ListRequestParam implements Serializable {
    private static final long serialVersionUID = 1964927535696180792L;

    // ===============通用参数===============
    @Value("${defaultStartPage:1}")
    private int page; // 页码
    @Value("${defaultPageSize:10}")
    private int limit; // 每页条数
    private int status; // 查询条件-状态
    private int categoryId; // 分类
    private String keywords; // 查询条件-关键词
    private String beginDate; // 查询条件-开始时间
    private String endDate; // 查询条件-结束时间
    private String sort; //排序-排序字段
    private int asc; //排序-排列顺序

    // ***************特殊参数***************
    // 附件列表
    private BigInteger tableId; // 用于附件列表-表ID
    private String tableName; // 用于附件列表-表名称
    private String tableField; // 用于附件列表-字段名

    // ***************特殊参数***************
    // 日志列表
    private String module; // 操作模块
    private String type; // 操作类型
    private int uid; // 操作用户

    // ***************特殊参数***************
    // 广告列表
    private long positionId; // 位置id

    // ---------------处理参数---------------
    private long beginTime; // 查询条件-开始时间
    private long endTime; // 查询条件-结束时间
    private String orderBy; // 排序
    private List<Long> categoryIds; // 分类（包含所有下级）
}
