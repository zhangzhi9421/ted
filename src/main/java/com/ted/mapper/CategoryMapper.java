package com.ted.mapper;

import com.ted.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * CategoryMapper
 *
 * @author Ted
 */
@Mapper
@Component("CategoryMapper")
public interface CategoryMapper {
    /**
     * 通过id获取对象
     *
     * @param categoryId categoryId
     * @return Category对象
     */
    Category getCategory(@Param(value = "categoryId") long categoryId);

    /**
     * 获取列表
     *
     * @param params 查询参数
     * @return Category集合
     */
    List<Category> listCategory(Map<String, Object> params);

    /**
     * 删除
     *
     * @param categoryId categoryId
     */
    void deleteCategory(long categoryId);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchCategory(String[] array);

    /**
     * 新增
     *
     * @param category Category对象
     */
    int insertCategory(Category category);

    /**
     * 修改
     *
     * @param category Category对象
     */
    int updateCategory(Category category);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countCategory(Map<String, Object> params);
}
