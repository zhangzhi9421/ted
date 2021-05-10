package com.ted.mapper;

import com.ted.entity.Link;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * LinkMapper
 *
 * @author Ted
 */
@Mapper
@Component("LinkMapper")
public interface LinkMapper {
    /**
     * 通过id获取对象
     *
     * @param linkId linkId
     * @return Link对象
     */
    Link getLink(@Param(value = "linkId") long linkId);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Link集合
     */
    List<Link> listLink(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param linkId linkId
     */
    void deleteLink(long linkId);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchLink(String[] array);

    /**
     * 新增
     *
     * @param link Link对象
     */
    int insertLink(Link link);

    /**
     * 修改
     *
     * @param link Link对象
     */
    int updateLink(Link link);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countLink(Map<String, Object> params);
}
