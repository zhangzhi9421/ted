package com.ted.mapper;

import com.github.pagehelper.PageInfo;
import com.ted.entity.ListRequestParam;
import com.ted.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

/**
 * LogMapper
 *
 * @author Ted
 */
@Mapper
@Component("LogMapper")
public interface LogMapper {
    /**
     * 通过id获取对象
     *
     * @param file_id file_id
     * @return Log对象
     */
    Log getLog(BigInteger file_id);

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
     * @param file_id file_id
     */
    void deleteLog(BigInteger file_id);

    /**
     * 批量删除
     *
     * @param array file_id列表
     */
    void deleteBatchLog(String[] array);

    /**
     * 新增
     *
     * @param admin Log对象
     * @return 执行结果
     */
    int insertLog(Log admin);
}
