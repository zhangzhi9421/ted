package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Link;
import com.ted.entity.ListRequestParam;

import java.util.List;
import java.util.Map;

/**
 * LinkService接口类
 *
 * @author Ted
 */
public interface LinkService {
    /**
     * 通过id获取对象
     *
     * @param linkId linkId
     * @return Link对象
     */
    Link getLink(long linkId);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Link集合
     */
    List<Link> listLink(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Link> pageLink(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param linkId linkId
     */
    void deleteLink(long linkId);

    /**
     * 批量删除
     *
     * @param array linkId列表
     */
    void deleteBatchLink(String[] array);

    /**
     * 新增
     *
     * @param link Link对象
     */
    void insertLink(Link link);

    /**
     * 更新
     *
     * @param link Link对象
     */
    void updateLink(Link link);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countLink(Map<String, Object> params);

    /**
     * 缩略图完整路径
     *
     * @param link Link对象
     * @return 缩略图
     */
    String getPhotoPath(Link link);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsLink(List<Link> list);

    /**
     * 修改状态
     *
     * @param link Link对象
     * @return 状态
     */
    int toggleStatus(Link link);

    /**
     * 详情
     *
     * @param link Link对象
     */
    void detailLink(Link link);

    /**
     * 处理请求参数
     *
     * @param link Link对象
     */
    void handlerRequest(Link link);
}
