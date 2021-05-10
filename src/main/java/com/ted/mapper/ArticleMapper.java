package com.ted.mapper;

import com.ted.entity.Article;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ArticleMapper
 *
 * @author Ted
 */
@Mapper
@Component("ArticleMapper")
public interface ArticleMapper {
    /**
     * 通过id获取对象
     *
     * @param aid aid
     * @return Article对象
     */
    Article getArticle(@Param(value = "aid") long aid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Article集合
     */
    List<Article> listArticle(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param aid aid
     */
    void deleteArticle(long aid);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchArticle(String[] array);

    /**
     * 新增
     *
     * @param article Article对象
     */
    int insertArticle(Article article);

    /**
     * 修改
     *
     * @param article Article对象
     */
    int updateArticle(Article article);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countArticle(Map<String, Object> params);
}
