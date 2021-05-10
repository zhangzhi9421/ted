package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Attachment;
import com.ted.entity.ListRequestParam;

import java.math.BigInteger;
import java.util.List;

/**
 * AttachmentService接口类
 *
 * @author Ted
 */
public interface AttachmentService {
    /**
     * 通过id获取对象
     *
     * @param file_id file_id
     * @return Attachment对象
     */
    Attachment getAttachment(BigInteger file_id);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Attachment集合
     */
    List<Attachment> listAttachment(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Attachment> pageAttachment(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param file_id file_id
     */
    void deleteAttachment(BigInteger file_id);

    /**
     * 批量删除
     *
     * @param array file_id列表
     */
    void deleteBatchAttachment(String[] array);

    /**
     * 新增
     *
     * @param attachment Attachment对象
     * @return 执行结果
     */
    int insertAttachment(Attachment attachment);

    /**
     * 更新
     *
     * @param attachment Attachment对象
     * @return 执行结果
     */
    int updateAttachment(Attachment attachment);

    /**
     * @param list 列表
     */
    void itemsAttachment(List<Attachment> list);
}
