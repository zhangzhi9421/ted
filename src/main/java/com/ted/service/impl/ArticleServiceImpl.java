package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Article;
import com.ted.entity.Category;
import com.ted.entity.Content;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.mapper.ArticleMapper;
import com.ted.mapper.AttachmentMapper;
import com.ted.service.ArticleService;
import com.ted.service.CategoryService;
import com.ted.service.ContentService;
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
 * ArticleService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("ArticleService")
@PropertySource(value = {"classpath:config/general.properties", "classpath:config/content.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ArticleServiceImpl implements ArticleService {

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

    @Value("${content.type.article}")
    private String contentTypeArticle;

    private ArticleMapper articleMapper;
    private DateConverter dateConverter;
    private LanguageConstant languageConstant;
    private UploadConstant uploadConstant;
    private AttachmentMapper attachmentMapper;
    private CategoryService categoryService;
    private ContentService contentService;
    private DateConstant dateConstant;

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setContentService(@Qualifier("ContentService") ContentService contentService) {
        this.contentService = contentService;
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
    public void setArticleMapper(@Qualifier("ArticleMapper") ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public Article getArticle(long aid) {
        Article article = articleMapper.getArticle(aid);
        // 获取文章内容
        article.setContent(contentService.getContent(aid, contentTypeArticle));
        return article;
    }

    @Override
    public List<Article> listArticle(ListRequestParam requestParam) {
        return articleMapper.listArticle(requestParam);
    }

    @Override
    public PageInfo<Article> pageArticle(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Article> list = this.listArticle(requestParam);
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
        if (requestParam.getCategoryId() > 0) {
            List<Long> categoryIds = categoryService.childCategory(requestParam.getCategoryId());
            log.error("categoryIds=================================" + categoryIds);
            requestParam.setCategoryIds(categoryIds);
        }
        // 排序
        if (StringUtils.isNotBlank(requestParam.getSort())) {
            String orderBy = requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteArticle(long aid) {
        // 删除
        articleMapper.deleteArticle(aid);
        // 清空头像
        this.handlerPhoto(aid, null);
        // 删除内容
        contentService.deleteContent(aid, contentTypeArticle);
    }

    @Override
    @Transactional
    public void deleteBatchArticle(String[] array) {
        // 删除
        articleMapper.deleteBatchArticle(array);
        // 清空头像
        for (String articleId : array) {
            long aid = Long.parseLong(articleId);
            this.handlerPhoto(aid, null);
            // 删除内容
            contentService.deleteContent(aid, contentTypeArticle);
        }
    }

    @Override
    @Transactional
    public void insertArticle(Article article) {
        int i = articleMapper.insertArticle(article);
        if (i > 0) {
            this.handlerAssociated(article);
        }
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        int i = articleMapper.updateArticle(article);
        if (i > 0) {
            this.handlerAssociated(article);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param article article对象
     */
    private void handlerAssociated(Article article) {
        // 处理头像
        this.handlerPhoto(article.getAid(), article.getPhotoIds());
        // 处理内容
        this.handlerContent(article.getAid(), article.getContent());
    }

    /**
     * 处理内容
     *
     * @param aid 文章id
     * @param content 内容
     */
    private void handlerContent(long aid, Content content) {
        long tableId = content.getTableId();
        log.error("tableId--------------------" + tableId);
        if (tableId <= 0) {
            // 没有，新增
            content.setTableId(aid);
            content.setTableName(contentTypeArticle);
            contentService.insertContent(content);
        } else {
            // 有了，修改
            contentService.updateContent(content);
        }
    }

    /**
     * 处理头像附件
     *
     * @param aid      aid
     * @param photoIds 头像附件列表
     */
    private void handlerPhoto(long aid, String photoIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tableId", aid);
        params.put("tableName", "article");
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
    public int countArticle(Map<String, Object> params) {
        return articleMapper.countArticle(params);
    }

    @Override
    public String getPhotoPath(Article article) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String photoPath = basePath + defaultPhoto;
        String photo = article.getPhoto();
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
    public void itemsArticle(List<Article> list) {
        // 遍历
        for (Article article : list) {
            // 发布时间
            article.setCreateDate(dateConverter.dateFormat(article.getCreateTime()));
            // 枚举[状态]
            StatusEnum statusEnum = StatusEnum.getEnum(article.getStatus());
            article.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
            // 分类节点路径
            handlerCategory(article);
        }
    }

    /**
     * 获取分类节点路径信息
     *
     * @param article 对象
     */
    private void handlerCategory(Article article) {
        StringBuilder categoryNodePath = new StringBuilder();
        // 分类
        Category category = categoryService.getCategory(article.getCategoryId());
        article.setCategoryName(category.getCategoryName());
        // 分类节点路径
        List<Category> categoryList = categoryService.parentCategory(article.getCategoryId());
        Collections.reverse(categoryList); // 倒序
        //log.error("categoryList为" + categoryList.toString());
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
        article.setCategoryNodePath(categoryNodePath);
    }

    @Override
    public int toggleStatus(Article article) {
        int status = article.getStatus();
        if (status == statusActive) {
            // 去掉该标志
            article.setStatus(status & (~statusActive));
        } else {
            // 增加该标志
            article.setStatus(status | statusActive);
        }
        int updateArticle = articleMapper.updateArticle(article);

        int result = 0;
        if (updateArticle > 0) {
            result = article.getStatus();
        }
        return result;
    }

    @Override
    public void detailArticle(Article article) {
        // 发布时间
        article.setCreateDate(dateConverter.dateFormat(article.getCreateTime()));
        // 排序：如果是默认的最大值，设置为0，前端不显示
        if (article.getSort() >= maxInt) {
            article.setSort(0);
        }
    }

    @Override
    public void handlerRequest(Article article) {
        // 发布时间
        if (StringUtils.isNotBlank(article.getCreateDate())) {
            // 日期转时间戳
            article.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(article.getCreateDate())));
        } else {
            article.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }
}
