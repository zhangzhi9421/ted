package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.ListRequestParam;
import com.ted.entity.Log;

import java.math.BigInteger;
import java.util.List;

/**
 * LogService接口类
 *
 * @author Ted
 */
public interface LogService {
    /**
     * 通过id获取对象
     *
     * @param id id
     * @return Log对象
     */
    Log getLog(BigInteger id);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Log集合
     */
    List<Log> listLog(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Log> pageLog(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param id id
     */
    void deleteLog(BigInteger id);

    /**
     * 批量删除
     *
     * @param array id列表
     */
    void deleteBatchLog(String[] array);

    /**
     * 新增
     *
     * @param log Log对象
     * @return 执行结果
     */
    int insertLog(Log log);

    /**
     * @param list 列表
     */
    void itemsLog(List<Log> list);

    /**
     * 详情
     *
     * @param log 日志对象
     */
    void detailLog(Log log);
}
