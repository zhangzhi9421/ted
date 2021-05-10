package com.ted.controller;

import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Category;
import com.ted.service.CategoryService;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Category控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/category")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private JsonUtil<HashMap<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public CategoryController(@Qualifier("CategoryService") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<HashMap<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * 列表
     *
     * @param categoryId 分类id
     * @return 列表
     */
    @AuthRequired
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listCategory(@RequestParam(value = "categoryId") long categoryId) {
        log.error("categoryId======" + categoryId);
        if (categoryId <= 0) {
            return jsonUtil.error("参数错误：无效的分类ID", null);
        }
        // 根据栏目id获取分类列表
        Category category = categoryService.treeCategory(categoryId);
        List<Category> list = categoryService.tableCategory(category);
        log.error("category为" + category.toString());
        log.error("list为" + list.toString());
        HashMap<String, Object> data = new HashMap<>();
        data.put("items", list);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param categoryId 分类id
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET)
    public String getCategory(@PathVariable long categoryId) {
        // 获取对象
        Category category = categoryService.getCategory(categoryId);
        if (!ObjectUtils.isEmpty(category)) {
            categoryService.detailCategory(category);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", category);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param category Category对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_CATEGORY, type = LogConstant.TYPE_INSERT, description = "新增分类")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertCategory(@RequestBody Category category) {

        if (!ObjectUtils.isEmpty(category)) {
            // 处理参数
            categoryService.handlerRequest(category);
            // 执行
            categoryService.insertCategory(category);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param category Category对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_CATEGORY, type = LogConstant.TYPE_UPDATE, description = "更新分类")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateCategory(@RequestBody Category category) {

        if (!ObjectUtils.isEmpty(category)) {
            // 处理参数
            categoryService.handlerRequest(category);
            // 执行
            categoryService.updateCategory(category);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 删除
     *
     * @param categoryId categoryId
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_CATEGORY, type = LogConstant.TYPE_DELETE, description = "删除分类")
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.DELETE)
    public String deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 分类下树形结构
     *
     * @param categoryId 分类id
     * @return 分类下树形结构
     */
    @AuthRequired
    @RequestMapping(value = "/tree/{categoryId}", method = RequestMethod.GET)
    public String getCategoryTree(@PathVariable long categoryId) {
        if (categoryId <= 0) {
            return jsonUtil.error("参数错误：无效的分类ID", null);
        }
        // 根据栏目id获取分类列表
        Category category = categoryService.treeCategory(categoryId);
        log.error("category========" + category.toString());
        if (!ObjectUtils.isEmpty(category)) {
            // 按照前端要求，放入数组
            ArrayList<Category> list = new ArrayList<>();
            list.add(category);
            // 返回数据
            HashMap<String, Object> data = new HashMap<>();
            data.put("categoryData", list);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }
}
