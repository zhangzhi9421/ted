package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Ad;
import com.ted.entity.ListRequestParam;

import java.util.List;
import java.util.Map;

/**
 * AdService接口类
 *
 * @author Ted
 */
public interface AdService {
    /**
     * 通过id获取对象
     *
     * @param adId adId
     * @return Ad对象
     */
    Ad getAd(long adId);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Ad集合
     */
    List<Ad> listAd(ListRequestParam requestParam);

    /**
     * 获取列表
     * 关联位置信息
     *
     * @param requestParam 请求参数
     * @return Ad集合
     */
    List<Ad> listAdAssociationPosition(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Ad> pageAd(ListRequestParam requestParam);

    /**
     * 分页查询
     * 关联位置信息
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Ad> pageAdAssociationPosition(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param adId adId
     */
    void deleteAd(long adId);

    /**
     * 批量删除
     *
     * @param array adId列表
     */
    void deleteBatchAd(String[] array);

    /**
     * 新增
     *
     * @param ad Ad对象
     */
    void insertAd(Ad ad);

    /**
     * 更新
     *
     * @param ad Ad对象
     */
    void updateAd(Ad ad);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countAd(Map<String, Object> params);

    /**
     * 缩略图完整路径
     *
     * @param ad Ad对象
     * @return 缩略图
     */
    String getPhotoPath(Ad ad);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsAd(List<Ad> list);

    /**
     * 修改状态
     *
     * @param ad Ad对象
     * @return 状态
     */
    int toggleStatus(Ad ad);

    /**
     * 详情
     *
     * @param ad Ad对象
     */
    void detailAd(Ad ad);

    /**
     * 处理请求参数
     *
     * @param ad Ad对象
     */
    void handlerRequest(Ad ad);
}
