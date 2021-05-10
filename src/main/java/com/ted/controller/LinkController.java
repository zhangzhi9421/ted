package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Link;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.service.LinkService;
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
 * Link控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/link")
public class LinkController extends BaseController {

    private final LinkService linkService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public LinkController(@Qualifier("LinkService") LinkService linkService) {
        this.linkService = linkService;
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
    public String listLink(ListRequestParam requestParam) {
        // 分页查询数据
        PageInfo<Link> pageInfo = linkService.pageLink(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<Link> items = pageInfo.getList();
        linkService.itemsLink(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param linkId linkId
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{linkId}", method = RequestMethod.GET)
    public String getLink(@PathVariable long linkId) {
        // 获取对象
        Link link = linkService.getLink(linkId);
        if (!ObjectUtils.isEmpty(link)) {
            log.info("link-------------" + link);
            linkService.detailLink(link);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", link);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param link Link对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LINK, type = LogConstant.TYPE_INSERT, description = "新增友情链接")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertLink(@RequestBody Link link) {
        if (!ObjectUtils.isEmpty(link)) {
            // 处理参数
            linkService.handlerRequest(link);
            // 执行
            linkService.insertLink(link);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param link Link对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LINK, type = LogConstant.TYPE_UPDATE, description = "更新友情链接")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateLink(@RequestBody Link link) {
        if (!ObjectUtils.isEmpty(link)) {
            // 处理参数
            linkService.handlerRequest(link);
            // 执行
            linkService.updateLink(link);
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
    @Logging(module = LogConstant.MODULE_LINK, type = LogConstant.TYPE_TOGGLE, description = "修改友情链接状态")
    @RequestMapping(value = "/toggle", method = RequestMethod.GET)
    public String toggle(@RequestParam(value = "linkId") long linkId,
                         @RequestParam(value = "key") String key) {
        // 根据id获取对象
        Link link = linkService.getLink(linkId);
        if (ObjectUtils.isEmpty(link)) {
            return jsonUtil.error(languageConstant.getUnfounded(), null);
        }
        // 修改属性
        if ("status".equals(key)) {
            int status = linkService.toggleStatus(link);
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
     * @param linkId linkId
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LINK, type = LogConstant.TYPE_DELETE, description = "删除友情链接")
    @RequestMapping(value = "/{linkId}", method = RequestMethod.DELETE)
    public String deleteLink(@PathVariable long linkId) {
        linkService.deleteLink(linkId);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LINK, type = LogConstant.TYPE_DELETE, description = "批量删除友情链接")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchLink(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        linkService.deleteBatchLink(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
