package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.AdPosition;
import com.ted.entity.ListRequestParam;
import com.ted.mapper.AdPositionMapper;
import com.ted.service.AdPositionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * AdPositionService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("AdPositionService")
@PropertySource(value = {"classpath:config/general.properties", "classpath:config/content.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AdPositionServiceImpl implements AdPositionService {

    private AdPositionMapper adPositionMapper;
    private DateConverter dateConverter;
    private DateConstant dateConstant;

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setAdPositionMapper(@Qualifier("AdPositionMapper") AdPositionMapper adPositionMapper) {
        this.adPositionMapper = adPositionMapper;
    }

    @Override
    public AdPosition getAdPosition(long positionId) {
        return adPositionMapper.getAdPosition(positionId);
    }

    @Override
    public List<AdPosition> listAdPosition(ListRequestParam requestParam) {
        return adPositionMapper.listAdPosition(requestParam);
    }

    @Override
    public PageInfo<AdPosition> pageAdPosition(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<AdPosition> list = this.listAdPosition(requestParam);
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
    public void deleteAdPosition(long positionId) {
        // 删除
        adPositionMapper.deleteAdPosition(positionId);
    }

    @Override
    @Transactional
    public void deleteBatchAdPosition(String[] array) {
        // 删除
        adPositionMapper.deleteBatchAdPosition(array);
    }

    @Override
    @Transactional
    public void insertAdPosition(AdPosition adPosition) {
        int i = adPositionMapper.insertAdPosition(adPosition);
        if (i > 0) {
            this.handlerAssociated(adPosition);
        }
    }

    @Override
    @Transactional
    public void updateAdPosition(AdPosition adPosition) {
        int i = adPositionMapper.updateAdPosition(adPosition);
        if (i > 0) {
            this.handlerAssociated(adPosition);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param adPosition adPosition对象
     */
    private void handlerAssociated(AdPosition adPosition) {
        // 没啥需要处理的
        log.info("adPosition======" + adPosition.toString());
    }

    @Override
    public int countAdPosition(Map<String, Object> params) {
        return adPositionMapper.countAdPosition(params);
    }

    @Override
    public void itemsAdPosition(List<AdPosition> list) {
        // 遍历
        for (AdPosition adPosition : list) {
            // 发布时间
            adPosition.setCreateDate(dateConverter.dateFormat(adPosition.getCreateTime()));
        }
    }

    @Override
    public void detailAdPosition(AdPosition adPosition) {
        // 发布时间
        adPosition.setCreateDate(dateConverter.dateFormat(adPosition.getCreateTime()));
    }

    @Override
    public void handlerRequest(AdPosition adPosition) {
        // 发布时间
        if (StringUtils.isNotBlank(adPosition.getCreateDate())) {
            // 日期转时间戳
            adPosition.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(adPosition.getCreateDate())));
        } else {
            adPosition.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }
}
