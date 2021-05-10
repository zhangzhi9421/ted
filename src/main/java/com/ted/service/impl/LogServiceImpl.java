package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.converter.DateConverter;
import com.ted.converter.IpConverter;
import com.ted.entity.ListRequestParam;
import com.ted.entity.Log;
import com.ted.mapper.LogMapper;
import com.ted.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

/**
 * LogService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("LogService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LogServiceImpl implements LogService {
    private LogMapper logMapper;
    private DateConverter dateConverter;
    private IpConverter ipConverter;
    private DateConstant dateConstant;

    @Autowired
    public void setIpConverter(IpConverter ipConverter) {
        this.ipConverter = ipConverter;
    }

    @Autowired
    public void setLogMapper(@Qualifier("LogMapper") LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Override
    public Log getLog(BigInteger id) {
        return logMapper.getLog(id);
    }

    @Override
    public List<Log> listLog(ListRequestParam requestParam) {
        return logMapper.listLog(requestParam);
    }

    @Override
    public PageInfo<Log> pageLog(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Log> list = this.listLog(requestParam);
        // 3.填装数据
        return new PageInfo<>(list);
    }

    /**
     * 处理请求参数
     *
     * @param requestParam 请求参数
     */
    private void handlerRequestParam(ListRequestParam requestParam) {
        // 时间段
        if (StringUtils.isNotBlank(requestParam.getBeginDate()) && StringUtils.isNotBlank(requestParam.getEndDate())) {
            requestParam.setBeginTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getBeginDate(), dateConstant.getPatternYmd())));
            requestParam.setEndTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getEndDate() + " " + dateConstant.getPatternEndOfDay(), dateConstant.getPatternYmdHis())));
        }
        // 排序
        if (StringUtils.isNotBlank(requestParam.getSort())) {
            String orderBy = requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteLog(BigInteger id) {
        logMapper.deleteLog(id);
    }

    @Override
    @Transactional
    public void deleteBatchLog(String[] array) {
        logMapper.deleteBatchLog(array);
    }

    @Override
    public int insertLog(Log log) {
        int i;
        try {
            i = logMapper.insertLog(log);
        } catch (Exception e) {
            i = 0;
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public void itemsLog(List<Log> list) {
        // 遍历
        for (Log log : list) {
            handlerDetail(log);
        }
    }

    @Override
    public void detailLog(Log log) {
        handlerDetail(log);
    }

    /**
     * @param log log对象
     */
    private void handlerDetail(Log log){
        log.setCreateDate(dateConverter.dateFormat(log.getCreateTime()));
        log.setIpAddress(ipConverter.longToIP(log.getIp()));
    }
}
