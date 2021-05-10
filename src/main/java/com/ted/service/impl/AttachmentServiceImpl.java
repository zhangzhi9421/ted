package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.converter.SizeConverter;
import com.ted.entity.Attachment;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.mapper.AttachmentMapper;
import com.ted.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigInteger;
import java.util.List;

/**
 * AdminService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("AttachmentService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AttachmentServiceImpl implements AttachmentService {
    private AttachmentMapper attachmentMapper;
    private DateConverter dateConverter;
    private UploadConstant uploadConstant;
    private LanguageConstant languageConstant;
    private SizeConverter sizeConverter;
    private DateConstant dateConstant;

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setSizeConverter(SizeConverter sizeConverter) {
        this.sizeConverter = sizeConverter;
    }

    @Autowired
    public void setAttachmentMapper(@Qualifier("AttachmentMapper") AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Override
    public Attachment getAttachment(BigInteger file_id) {
        return attachmentMapper.getAttachment(file_id);
    }

    @Override
    public List<Attachment> listAttachment(ListRequestParam requestParam) {
        return attachmentMapper.listAttachment(requestParam);
    }

    @Override
    public PageInfo<Attachment> pageAttachment(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Attachment> list = this.listAttachment(requestParam);
        // 3.填装数据
        return new PageInfo<>(list);
    }

    /**
     * 处理请求参数
     *
     * @param requestParam 请求参数
     */
    private void handlerRequestParam(ListRequestParam requestParam) {
        // 时间段
        if (StringUtils.isNotBlank(requestParam.getBeginDate()) && StringUtils.isNotBlank(requestParam.getEndDate())) {
            requestParam.setBeginTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getBeginDate(), dateConstant.getPatternYmd())));
            requestParam.setEndTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getEndDate() + " " + dateConstant.getPatternEndOfDay(), dateConstant.getPatternYmdHis())));
        }
        // 排序
        if (StringUtils.isNotBlank(requestParam.getSort())) {
            String orderBy = requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteAttachment(BigInteger file_id) {
        // 清除文件
        this.deleteFile(file_id);
        // 删除附件
        attachmentMapper.deleteAttachment(file_id);
    }

    @Override
    @Transactional
    public void deleteBatchAttachment(String[] array) {
        // 清除文件
        for (String file_id : array) {
            this.deleteFile(new BigInteger(file_id));
        }
        // 删除附件
        attachmentMapper.deleteBatchAttachment(array);
    }

    /**
     * 删除文件
     *
     * @param file_id 附件id
     */
    private void deleteFile(BigInteger file_id) {
        // 获取文件
        Attachment attachment = attachmentMapper.getAttachment(file_id);
        if (!ObjectUtils.isEmpty(attachment)) {
            // 1.获取文件路径
            // 工程静态资源根目录
            String resourcePath = uploadConstant.getUploadPath() + uploadConstant.getUploadDir(); // ResourceUtils.getURL("classpath:").getPath();
            // String path = resourcePath + uploadConstant.getDir() + attachment.getFilePath();
            String path = resourcePath + attachment.getFilePath();
            log.error("文件路径path====================" + path);
            File file = new File(path);
            // 是否文件存在
            if (file.exists()) {
                // 如果存在，删除文件
                boolean delete = file.delete();
                if (delete) {
                    log.info("删除文件----" + path + "成功");
                }
            }
        }
    }

    @Override
    public int insertAttachment(Attachment attachment) {
        int i;
        try {
            i = attachmentMapper.insertAttachment(attachment);
        } catch (Exception e) {
            i = 0;
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int updateAttachment(Attachment attachment) {
        int i;
        try {
            i = attachmentMapper.updateAttachment(attachment);
        } catch (Exception e) {
            i = 0;
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public void itemsAttachment(List<Attachment> list) {
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
        // 遍历
        for (Attachment attachment : list) {
            attachment.setId(attachment.getFileId());
            attachment.setName(attachment.getFileName());
            attachment.setUrl(basePath + uploadConstant.getUploadDir() + attachment.getFilePath());
            attachment.setSizeLabel(sizeConverter.byteFormat(attachment.getFileSize()));
            // 枚举[状态]
            StatusEnum statusEnum = StatusEnum.getEnum(attachment.getStatus());
            attachment.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
            attachment.setCreateDate(dateConverter.dateFormat(attachment.getCreateTime()));
        }
    }
}
