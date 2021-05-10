package com.ted.mapper;

import com.ted.entity.AdPosition;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AdPositionMapper
 *
 * @author Ted
 */
@Mapper
@Component("AdPositionMapper")
public interface AdPositionMapper {
    /**
     * 通过id获取对象
     *
     * @param positionId positionId
     * @return AdPosition对象
     */
    AdPosition getAdPosition(@Param(value = "positionId") long positionId);

    /**
     * 列表
     *
     * @param requestParam 请求参数
     * @return 列表
     */
    List<AdPosition> listAdPosition(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param positionId positionId
     */
    void deleteAdPosition(long positionId);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchAdPosition(String[] array);

    /**
     * 新增
     *
     * @param adPosition AdPosition对象
     */
    int insertAdPosition(AdPosition adPosition);

    /**
     * 修改
     *
     * @param adPosition AdPosition对象
     */
    int updateAdPosition(AdPosition adPosition);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countAdPosition(Map<String, Object> params);
}
