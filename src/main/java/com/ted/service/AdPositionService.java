package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.AdPosition;
import com.ted.entity.ListRequestParam;

import java.util.List;
import java.util.Map;

/**
 * AdPositionService接口类
 *
 * @author Ted
 */
public interface AdPositionService {
    /**
     * 通过id获取对象
     *
     * @param positionId positionId
     * @return AdPosition对象
     */
    AdPosition getAdPosition(long positionId);

    /**
     * 列表
     *
     * @param requestParam 请求参数
     * @return 列表
     */
    List<AdPosition> listAdPosition(ListRequestParam requestParam);

    /**
     * 分页列表
     *
     * @param requestParam 请求参数
     * @return 分页列表
     */
    PageInfo<AdPosition> pageAdPosition(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param positionId positionId
     */
    void deleteAdPosition(long positionId);

    /**
     * 批量删除
     *
     * @param array positionId列表
     */
    void deleteBatchAdPosition(String[] array);

    /**
     * 新增
     *
     * @param adPosition AdPosition对象
     */
    void insertAdPosition(AdPosition adPosition);

    /**
     * 更新
     *
     * @param adPosition AdPosition对象
     */
    void updateAdPosition(AdPosition adPosition);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countAdPosition(Map<String, Object> params);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsAdPosition(List<AdPosition> list);

    /**
     * 详情
     *
     * @param adPosition AdPosition对象
     */
    void detailAdPosition(AdPosition adPosition);

    /**
     * 处理请求参数
     *
     * @param adPosition AdPosition对象
     */
    void handlerRequest(AdPosition adPosition);
}
