package com.ted.mapper;

import com.ted.entity.Ad;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AdMapper
 *
 * @author Ted
 */
@Mapper
@Component("AdMapper")
public interface AdMapper {
    /**
     * 通过id获取对象
     *
     * @param adId adId
     * @return Ad对象
     */
    Ad getAd(@Param(value = "adId") long adId);

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
     * 删除
     *
     * @param adId adId
     */
    void deleteAd(long adId);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchAd(String[] array);

    /**
     * 新增
     *
     * @param ad Ad对象
     */
    int insertAd(Ad ad);

    /**
     * 修改
     *
     * @param ad Ad对象
     */
    int updateAd(Ad ad);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countAd(Map<String, Object> params);
}
