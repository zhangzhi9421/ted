package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * CategoryService接口类
 *
 * @author Ted
 */
public interface CategoryService {
    /**
     * 通过id获取对象
     *
     * @param categoryId 分类id
     * @return Category对象
     */
    Category getCategory(long categoryId);

    /**
     * 获取列表
     *
     * @param params 查询参数
     * @return Category集合
     */
    List<Category> listCategory(Map<String, Object> params);

    /**
     * 分页查询
     *
     * @param params   查询参数
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return PageInfo
     */
    PageInfo<Category> pageCategory(Map<String, Object> params, int pageNum, int pageSize);

    /**
     * 删除
     *
     * @param categoryId 分类id
     */
    void deleteCategory(long categoryId);

    /**
     * 新增
     *
     * @param category Category对象
     */
    void insertCategory(Category category);

    /**
     * 更新
     *
     * @param category Category对象
     */
    void updateCategory(Category category);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countCategory(Map<String, Object> params);

    /**
     * 头像完整路径
     *
     * @param category Category对象
     * @return 头像
     */
    String getPhotoPath(Category category);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsCategory(List<Category> list);

    /**
     * 生成用于前端的详情
     *
     * @param category Category对象
     */
    void detailCategory(Category category);

    /**
     * 分类下子类（树形结构）
     *
     * @param categoryId 分类id
     * @return 分类下子类集合
     */
    Category treeCategory(long categoryId);

    /**
     * 所有子类（集合列表）
     *
     * @param categoryId 分类id
     * @return 所有子类集合
     */
    List<Long> childCategory(long categoryId);

    /**
     * 所有父类（集合）
     *
     * @param categoryId 分类id
     * @return 父级集合
     */
    List<Category> parentCategory(long categoryId);

    /**
     * 处理请求参数
     *
     * @param category Category对象
     */
    void handlerRequest(Category category);

    /**
     * 可用于TABLE展示的列表
     *
     * @param category Category对象
     * @return 分类列表
     */
    List<Category> tableCategory(Category category);
}
