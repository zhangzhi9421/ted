package com.ted.service;

import com.ted.entity.Content;

/**
 * ContentService接口类
 *
 * @author Ted
 */
public interface ContentService {
    /**
     * 通过id和表明获取对象
     *
     * @param tableId   表id
     * @param tableName 表名
     * @return Content对象
     */
    Content getContent(long tableId, String tableName);

    /**
     * 删除
     *
     * @param tableId   表id
     * @param tableName 表名
     */
    void deleteContent(long tableId, String tableName);

    /**
     * 新增
     *
     * @param content Content对象
     */
    void insertContent(Content content);

    /**
     * 更新
     *
     * @param content Content对象
     */
    void updateContent(Content content);
}
