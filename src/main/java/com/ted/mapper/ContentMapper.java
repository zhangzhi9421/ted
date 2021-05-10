package com.ted.mapper;

import com.ted.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * ContentMapper
 *
 * @author Ted
 */
@Mapper
@Component("ContentMapper")
public interface ContentMapper {
    /**
     * 通过id和表明获取对象
     *
     * @param tableId   表ID
     * @param tableName 表明
     * @return Content对象
     */
    Content getContent(long tableId, String tableName);

    /**
     * 删除
     *
     * @param tableId   表ID
     * @param tableName 表明
     */
    void deleteContent(long tableId, String tableName);

    /**
     * 新增
     *
     * @param content Content对象
     */
    int insertContent(Content content);

    /**
     * 修改
     *
     * @param content Content对象
     */
    int updateContent(Content content);
}
