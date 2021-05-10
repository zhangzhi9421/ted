package com.ted.service.impl;

import com.ted.entity.Content;
import com.ted.mapper.ContentMapper;
import com.ted.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ContentService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("ContentService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ContentServiceImpl implements ContentService {

    private ContentMapper contentMapper;


    @Autowired
    public void setContentMapper(@Qualifier("ContentMapper") ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public Content getContent(long tableId, String tableName) {
        return contentMapper.getContent(tableId, tableName);
    }

    @Override
    @Transactional
    public void deleteContent(long tableId, String tableName) {
        // 删除
        contentMapper.deleteContent(tableId, tableName);
    }

    @Override
    public void insertContent(Content content) {
        int i = contentMapper.insertContent(content);
        log.info("添加[内容]==="+i+"条记录");
    }

    @Override
    public void updateContent(Content content) {
        int i = contentMapper.updateContent(content);
        log.info("添加[内容]==="+i+"条记录");
    }
}
