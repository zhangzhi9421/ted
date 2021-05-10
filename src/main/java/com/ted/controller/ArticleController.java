package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Article;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.service.ArticleService;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Article控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/article")
public class ArticleController extends BaseController {

    private final ArticleService articleService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public ArticleController(@Qualifier("ArticleService") ArticleService articleService) {
        this.articleService = articleService;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<Map<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * 列表
     *
     * @param requestParam 请求参数
     * @return 列表
     */
    @AuthRequired
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listArticle(ListRequestParam requestParam) {
        // 分页查询数据
        PageInfo<Article> pageInfo = articleService.pageArticle(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<Article> items = pageInfo.getList();
        articleService.itemsArticle(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param aid aid
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{aid}", method = RequestMethod.GET)
    public String getArticle(@PathVariable long aid) {
        // 获取对象
        Article article = articleService.getArticle(aid);
        if (!ObjectUtils.isEmpty(article)) {
            log.info("article-------------" + article);
            articleService.detailArticle(article);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", article);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param article Article对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ARTICLE, type = LogConstant.TYPE_INSERT, description = "新增文章")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertArticle(@RequestBody Article article) {
        if (!ObjectUtils.isEmpty(article)) {
            // 处理参数
            articleService.handlerRequest(article);
            // 执行
            articleService.insertArticle(article);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param article Article对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ARTICLE, type = LogConstant.TYPE_UPDATE, description = "更新文章")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateArticle(@RequestBody Article article) {
        if (!ObjectUtils.isEmpty(article)) {
            // 处理参数
            articleService.handlerRequest(article);
            // 执行
            articleService.updateArticle(article);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 切换属性值
     *
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ARTICLE, type = LogConstant.TYPE_TOGGLE, description = "修改文章状态")
    @RequestMapping(value = "/toggle", method = RequestMethod.GET)
    public String toggle(@RequestParam(value = "aid") long aid,
                         @RequestParam(value = "key") String key) {
        // 根据id获取对象
        Article article = articleService.getArticle(aid);
        if (ObjectUtils.isEmpty(article)) {
            return jsonUtil.error(languageConstant.getUnfounded(), null);
        }
        // 修改属性
        if ("status".equals(key)) {
            int status = articleService.toggleStatus(article);
            // 枚举[状态]
            StatusEnum statusEnum = StatusEnum.getEnum(status);

            Map<String, Object> data = new HashMap<>();
            data.put("status", status);
            data.put("statusLabel", (!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown()));
            return jsonUtil.success(languageConstant.getSuccess(), data);
        }
        return jsonUtil.error();
    }

    /**
     * 删除
     *
     * @param aid aid
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ARTICLE, type = LogConstant.TYPE_DELETE, description = "删除文章")
    @RequestMapping(value = "/{aid}", method = RequestMethod.DELETE)
    public String deleteArticle(@PathVariable long aid) {
        articleService.deleteArticle(aid);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ARTICLE, type = LogConstant.TYPE_DELETE, description = "批量删除文章")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchArticle(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        articleService.deleteBatchArticle(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
