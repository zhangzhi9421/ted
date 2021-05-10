package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Category;
import com.ted.entity.Link;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.mapper.AttachmentMapper;
import com.ted.mapper.LinkMapper;
import com.ted.service.CategoryService;
import com.ted.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LinkService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("LinkService")
@PropertySource(value = {"classpath:config/general.properties", "classpath:config/content.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LinkServiceImpl implements LinkService {

    /**
     * 默认头像
     */
    @Value("${defaultPhoto:/images/defaultPhoto.png}")
    private String defaultPhoto;

    /**
     * 状态-正常
     */
    @Value("${statusActive}")
    private int statusActive;

    @Value("${maxInt}")
    private int maxInt;

    private LinkMapper linkMapper;
    private DateConverter dateConverter;
    private LanguageConstant languageConstant;
    private UploadConstant uploadConstant;
    private AttachmentMapper attachmentMapper;
    private CategoryService categoryService;
    private DateConstant dateConstant;

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setCategoryService(@Qualifier("CategoryService") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setAttachmentMapper(@Qualifier("AttachmentMapper") AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setLinkMapper(@Qualifier("LinkMapper") LinkMapper linkMapper) {
        this.linkMapper = linkMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public Link getLink(long linkId) {
        return linkMapper.getLink(linkId);
    }

    @Override
    public List<Link> listLink(ListRequestParam requestParam) {
        return linkMapper.listLink(requestParam);
    }

    @Override
    public PageInfo<Link> pageLink(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Link> list = this.listLink(requestParam);
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
        // 查询分类下所有的子类集合
        List<Long> categoryIds = categoryService.childCategory(requestParam.getCategoryId());
        requestParam.setCategoryIds(categoryIds);
        // 排序
        if (StringUtils.isNotBlank(requestParam.getSort())) {
            String orderBy = requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteLink(long linkId) {
        // 删除
        linkMapper.deleteLink(linkId);
        // 清空头像
        this.handlerPhoto(linkId, null);
    }

    @Override
    @Transactional
    public void deleteBatchLink(String[] array) {
        // 删除
        linkMapper.deleteBatchLink(array);
        // 清空头像
        for (String linkId : array) {
            this.handlerPhoto(Long.parseLong(linkId), null);
        }
    }

    @Override
    @Transactional
    public void insertLink(Link link) {
        int i = linkMapper.insertLink(link);
        if (i > 0) {
            this.handlerAssociated(link);
        }
    }

    @Override
    @Transactional
    public void updateLink(Link link) {
        int i = linkMapper.updateLink(link);
        if (i > 0) {
            this.handlerAssociated(link);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param link link对象
     */
    private void handlerAssociated(Link link) {
        // 处理头像
        this.handlerPhoto(link.getLinkId(), link.getPhotoIds());
    }

    /**
     * 处理头像附件
     *
     * @param linkId  linkId
     * @param photoIds 头像附件列表
     */
    private void handlerPhoto(long linkId, String photoIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tableId", linkId);
        params.put("tableName", "link");
        params.put("tableField", "photo");
        // 如果字段为空，表示本次更新，需要清空所有符合条件的附件
        if (StringUtils.isNotBlank(photoIds)) {
            // 处理头像附件
            log.error("保留的附件id========" + photoIds);
            String[] photoArray = photoIds.split(",");
            params.put("array", photoArray);
            // 1.把原有表数据符合的附件记录，更新为status=0
            attachmentMapper.disableAttachmentExclude(params);
            // 2.把保留的附件设置成status=1，及其他表字段赋值
            attachmentMapper.disableAttachment(params);
        } else {
            // 1.把原有表数据符合的附件记录，更新为status=0
            attachmentMapper.disableAttachmentExclude(params);
        }
    }

    @Override
    public int countLink(Map<String, Object> params) {
        return linkMapper.countLink(params);
    }

    @Override
    public String getPhotoPath(Link link) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String photoPath = basePath + defaultPhoto;
        String photo = link.getPhoto();
        if (StringUtils.isNotBlank(photo)) {
            // 如果有头像，返回完整URL
            if (photo.startsWith("http")) {
                photoPath = photo;
            } else {
                photoPath = basePath + uploadConstant.getUploadDir() + photo;
            }
        }
        return photoPath;
    }

    @Override
    public void itemsLink(List<Link> list) {
        // 遍历
        for (Link link : list) {
            // 发布时间
            link.setCreateDate(dateConverter.dateFormat(link.getCreateTime()));
            // 枚举[状态]
            StatusEnum statusEnum = StatusEnum.getEnum(link.getStatus());
            link.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
            // 分类节点路径
            handlerCategory(link);
        }
    }

    /**
     * 获取分类节点路径信息
     *
     * @param link 对象
     */
    private void handlerCategory(Link link) {
        StringBuilder categoryNodePath = new StringBuilder();
        // 分类
        Category category = categoryService.getCategory(link.getCategoryId());
        link.setCategoryName(category.getCategoryName());
        // 分类节点路径
        List<Category> categoryList = categoryService.parentCategory(link.getCategoryId());
        Collections.reverse(categoryList); // 倒序
        log.error("categoryList为" + categoryList);
        // 遍历获得分类节点路径
        for (Category cate : categoryList) {
            if (cate.getParentId() <= 0) {
                // 顶级分类
                categoryNodePath.append("|");
            } else {
                categoryNodePath.append("»");
            }
            categoryNodePath.append(" ");
            categoryNodePath.append(cate.getCategoryName());
            categoryNodePath.append(" ");
        }
        link.setCategoryNodePath(categoryNodePath);
    }

    @Override
    public int toggleStatus(Link link) {
        int status = link.getStatus();
        if (status == statusActive) {
            // 去掉该标志
            link.setStatus(status & (~statusActive));
        } else {
            // 增加该标志
            link.setStatus(status | statusActive);
        }
        int updateLink = linkMapper.updateLink(link);

        int result = 0;
        if (updateLink > 0) {
            result = link.getStatus();
        }
        return result;
    }

    @Override
    public void detailLink(Link link) {
        // 发布时间
        link.setCreateDate(dateConverter.dateFormat(link.getCreateTime()));
        // 排序：如果是默认的最大值，设置为0，前端不显示
        if (link.getSort() >= maxInt) {
            link.setSort(0);
        }
    }

    @Override
    public void handlerRequest(Link link) {
        // 发布时间
        if (StringUtils.isNotBlank(link.getCreateDate())) {
            // 日期转时间戳
            link.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(link.getCreateDate())));
        } else {
            link.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }
}
