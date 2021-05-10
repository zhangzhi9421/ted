package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Article;
import com.ted.entity.ListRequestParam;

import java.util.List;
import java.util.Map;

/**
 * ArticleService接口类
 *
 * @author Ted
 */
public interface ArticleService {
    /**
     * 通过id获取对象
     *
     * @param aid aid
     * @return Article对象
     */
    Article getArticle(long aid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Article集合
     */
    List<Article> listArticle(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Article> pageArticle(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param aid aid
     */
    void deleteArticle(long aid);

    /**
     * 批量删除
     *
     * @param array aid列表
     */
    void deleteBatchArticle(String[] array);

    /**
     * 新增
     *
     * @param article Article对象
     */
    void insertArticle(Article article);

    /**
     * 更新
     *
     * @param article Article对象
     */
    void updateArticle(Article article);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countArticle(Map<String, Object> params);

    /**
     * 缩略图完整路径
     *
     * @param article Article对象
     * @return 缩略图
     */
    String getPhotoPath(Article article);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsArticle(List<Article> list);

    /**
     * 修改状态
     *
     * @param article Article对象
     * @return 状态
     */
    int toggleStatus(Article article);

    /**
     * 详情
     *
     * @param article Article对象
     */
    void detailArticle(Article article);

    /**
     * 处理请求参数
     *
     * @param article Article对象
     */
    void handlerRequest(Article article);
}
