package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Category;
import com.ted.mapper.AttachmentMapper;
import com.ted.mapper.CategoryMapper;
import com.ted.service.CategoryService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("CategoryService")
@PropertySource(value = {"classpath:config/general.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CategoryServiceImpl implements CategoryService {

    /**
     * 默认头像
     */
    @Value("${defaultPhoto:/images/defaultPhoto.png}")
    private String defaultPhoto;

    @Value("${maxInt}")
    private int maxInt;

    private CategoryMapper categoryMapper;
    private DateConverter dateConverter;
    private UploadConstant uploadConstant;
    private AttachmentMapper attachmentMapper;

    @Autowired
    public void setAttachmentMapper(@Qualifier("AttachmentMapper") AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setCategoryMapper(@Qualifier("CategoryMapper") CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public Category getCategory(long categoryId) {
        return categoryMapper.getCategory(categoryId);
    }

    @Override
    public List<Category> listCategory(Map<String, Object> params) {
        return categoryMapper.listCategory(params);
    }

    @Override
    public PageInfo<Category> pageCategory(Map<String, Object> params, int pageNum, int pageSize) {
        // 1.开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 2.查询数据
        List<Category> list = this.listCategory(params);
        // 3.填装数据
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        // 删除
        categoryMapper.deleteCategory(categoryId);
        // 清空头像
        this.handlerPhoto(categoryId, null);
    }

    @Override
    @Transactional
    public void insertCategory(Category category) {
        int i = categoryMapper.insertCategory(category);
        if (i > 0) {
            this.handlerAssociated(category);
        }
    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        int i = categoryMapper.updateCategory(category);
        if (i > 0) {
            this.handlerAssociated(category);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param category Category对象
     */
    private void handlerAssociated(Category category) {
        // 处理头像
        this.handlerPhoto(category.getCategoryId(), category.getPhotoIds());
    }

    /**
     * 处理头像附件
     *
     * @param categoryId 分类id
     * @param photoIds   头像附件列表
     */
    private void handlerPhoto(long categoryId, String photoIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tableId", categoryId);
        params.put("tableName", "category");
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
    public int countCategory(Map<String, Object> params) {
        return categoryMapper.countCategory(params);
    }

    @Override
    public String getPhotoPath(Category category) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String photoPath = basePath + defaultPhoto;
        String photo = category.getPhoto();
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
    public void itemsCategory(List<Category> list) {
        for (Category category : list) {
            // 发布时间
            category.setCreateDate(dateConverter.dateFormat(category.getCreateTime()));
        }
    }

    @Override
    public void detailCategory(Category category) {
        // 发布时间
        category.setCreateDate(dateConverter.dateFormat(category.getCreateTime()));
        // 排序：如果是默认的最大值，设置为0，前端不显示
        if (category.getSort() >= maxInt) {
            category.setSort(0);
        }
    }

    @Override
    public Category treeCategory(long categoryId) {
        // 1.获取对应分类信息
        Category category = categoryMapper.getCategory(categoryId);
        if (ObjectUtils.isEmpty(category)) {
            return null;
        }
        category.setChildren(this.getChildren(categoryId));

        return category;
    }

    @Override
    public List<Long> childCategory(long categoryId) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(categoryId);
        // 根据分类获取子类
        List<Category> categoryList = getChildren(categoryId);
        return this.getChildrenList(categoryList, list);
    }

    @Override
    public List<Category> tableCategory(Category category) {
        int deep = 1;
        ArrayList<Category> list = new ArrayList<>();
        category.setDeep(deep);
        category.setPrefix("");
        list.add(category);
        // 根据分类获取子类
        return this.getChildrenTable(category, list);
    }

    @Override
    public List<Category> parentCategory(long categoryId) {
        List<Category> list = new ArrayList<>();
        return this.getParentList(categoryId, list);
    }

    /**
     * 返回子类的可用于TABLE展示的列表
     *
     * @param category Category对象
     * @param list     子类的可用于TABLE展示的列表
     * @return 子类的可用于TABLE展示的列表
     */
    private List<Category> getChildrenTable(Category category, ArrayList<Category> list) {
        // 分类树形结构前缀
        String categoryTreePrefix = "　　";
        for (Category categoryModel : category.getChildren()) {
            int deep = category.getDeep();
            String prefix = category.getPrefix();
            categoryModel.setDeep(deep + 1);
            categoryModel.setPrefix(prefix + categoryTreePrefix);
            list.add(categoryModel);
            getChildrenTable(categoryModel, list);
        }
        return list;
    }

    /**
     * @param categoryId 分类id
     * @param list       父类集合
     * @return 父类集合
     */
    private List<Category> getParentList(long categoryId, List<Category> list) {
        Category category = categoryMapper.getCategory(categoryId);
        list.add(category);
        if (category.getParentId() > 0) {
            // 父级大于0
            getParentList(category.getParentId(), list);
        }
        return list;
    }

    /**
     * @param categoryList 分类集合
     * @param list         结果集合
     * @return 结果集合
     */
    private List<Long> getChildrenList(List<Category> categoryList, ArrayList<Long> list) {
        for (Category category : categoryList) {
            list.add(category.getCategoryId());
            getChildrenList(category.getChildren(), list);
        }
        return list;
    }

    /**
     * 子类集合
     *
     * @param categoryId 分类ID
     * @return 子类集合
     */
    private List<Category> getChildren(Long categoryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", categoryId);
        List<Category> listCategory = categoryMapper.listCategory(params);
        if (listCategory.size() == 0) {
            return listCategory;
        }
        for (Category category : listCategory) {
            category.setChildren(getChildren(category.getCategoryId()));
        }
        return listCategory;
    }

    @Override
    public void handlerRequest(Category category) {
        // 发布时间
        if (StringUtils.isNotBlank(category.getCreateDate())) {
            // 日期转时间戳
            category.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(category.getCreateDate())));
        } else {
            category.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }
}
