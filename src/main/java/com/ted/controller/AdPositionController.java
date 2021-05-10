package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.AdPosition;
import com.ted.entity.ListRequestParam;
import com.ted.service.AdPositionService;
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
 * AdPosition控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/adPosition")
public class AdPositionController extends BaseController {

    private final AdPositionService adPositionService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public AdPositionController(@Qualifier("AdPositionService") AdPositionService adPositionService) {
        this.adPositionService = adPositionService;
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
    public String listAdPosition(ListRequestParam requestParam) {

        // 分页查询数据
        PageInfo<AdPosition> pageInfo = adPositionService.pageAdPosition(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<AdPosition> items = pageInfo.getList();
        adPositionService.itemsAdPosition(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param positionId positionId
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{positionId}", method = RequestMethod.GET)
    public String getAdPosition(@PathVariable long positionId) {
        // 获取对象
        AdPosition adPosition = adPositionService.getAdPosition(positionId);
        if (!ObjectUtils.isEmpty(adPosition)) {
            log.info("adPosition-------------" + adPosition);
            adPositionService.detailAdPosition(adPosition);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", adPosition);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param adPosition AdPosition对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_AD_POSITION, type = LogConstant.TYPE_INSERT, description = "新增广告位置")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertAdPosition(@RequestBody AdPosition adPosition) {
        if (!ObjectUtils.isEmpty(adPosition)) {
            // 处理参数
            adPositionService.handlerRequest(adPosition);
            // 执行
            adPositionService.insertAdPosition(adPosition);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param adPosition AdPosition对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_AD_POSITION, type = LogConstant.TYPE_UPDATE, description = "更新广告位置")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateAdPosition(@RequestBody AdPosition adPosition) {
        if (!ObjectUtils.isEmpty(adPosition)) {
            // 处理参数
            adPositionService.handlerRequest(adPosition);
            // 执行
            adPositionService.updateAdPosition(adPosition);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 删除
     *
     * @param positionId positionId
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_AD_POSITION, type = LogConstant.TYPE_DELETE, description = "删除广告位置")
    @RequestMapping(value = "/{positionId}", method = RequestMethod.DELETE)
    public String deleteAdPosition(@PathVariable long positionId) {
        adPositionService.deleteAdPosition(positionId);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_AD_POSITION, type = LogConstant.TYPE_DELETE, description = "批量删除广告位置")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchAdPosition(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        adPositionService.deleteBatchAdPosition(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
