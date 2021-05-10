package com.ted.mapper;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Attachment;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttachmentMapper
 *
 * @author Ted
 */
@Mapper
@Component("AttachmentMapper")
public interface AttachmentMapper {
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
     * @param admin Attachment对象
     * @return 执行结果
     */
    int insertAttachment(Attachment admin);

    /**
     * 更新
     *
     * @param admin Attachment对象
     * @return 执行结果
     */
    int updateAttachment(Attachment admin);

    /**
     * 批量设置为：不可用
     *
     * @param params 其他参数
     */
    void disableAttachmentExclude(Map<String, Object> params);

    /**
     * 批量设置为：可用
     *
     * @param params 其他参数
     */
    void disableAttachment(HashMap<String, Object> params);
}
