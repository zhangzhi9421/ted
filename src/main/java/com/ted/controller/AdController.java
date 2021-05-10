package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Ad;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.service.AdService;
import com.ted.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
 * Ad控制器
 *
 * @author Ted
 */
@Slf4j
@Api(tags = {"广告位"})
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/ad")
public class AdController extends BaseController {

    private final AdService adService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public AdController(@Qualifier("AdService") AdService adService) {
        this.adService = adService;
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
    @ApiOperation(value = "获取广告位列表", notes = "获取广告位列表")
    @ApiImplicitParam(value = "requestParam", required = true)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listAd(ListRequestParam requestParam) {
        // 分页查询数据
        PageInfo<Ad> pageInfo = adService.pageAdAssociationPosition(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<Ad> items = pageInfo.getList();
        log.error(items.toString());
        adService.itemsAd(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param adId adId
     * @return 详情
     */
    @AuthRequired
    @ApiOperation(value = "获取广告位", notes = "获取指定广告")
    @ApiImplicitParam(value = "adId", required = true)
    @RequestMapping(value = "/{adId}", method = RequestMethod.GET)
    public String getAd(@PathVariable long adId) {
        // 获取对象
        Ad ad = adService.getAd(adId);
        if (!ObjectUtils.isEmpty(ad)) {
            log.info("ad-------------" + ad);
            adService.detailAd(ad);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", ad);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param ad Ad对象
     * @return 执行结果
     */
    @AuthRequired
    @ApiOperation(value = "新增广告", notes = "新增一条广告")
    @ApiImplicitParam(value = "ad", required = true)
    @Logging(module = LogConstant.MODULE_AD, type = LogConstant.TYPE_INSERT, description = "新增广告")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertAd(@RequestBody Ad ad) {
        if (!ObjectUtils.isEmpty(ad)) {
            // 处理参数
            adService.handlerRequest(ad);
            // 执行
            adService.insertAd(ad);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param ad Ad对象
     * @return 执行结果
     */
    @AuthRequired
    @ApiOperation(value = "修改广告", notes = "修改一条广告")
    @ApiImplicitParam(value = "ad", required = true)
    @Logging(module = LogConstant.MODULE_AD, type = LogConstant.TYPE_UPDATE, description = "更新广告")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateAd(@RequestBody Ad ad) {
        if (!ObjectUtils.isEmpty(ad)) {
            // 处理参数
            adService.handlerRequest(ad);
            // 执行
            adService.updateAd(ad);
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
    @ApiOperation(value = "修改广告状态", notes = "修改广告状态")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "adId", required = true),
            @ApiImplicitParam(value = "key", required = true)
    })
    @Logging(module = LogConstant.MODULE_AD, type = LogConstant.TYPE_TOGGLE, description = "修改广告状态")
    @RequestMapping(value = "/toggle", method = RequestMethod.GET)
    public String toggle(@RequestParam(value = "adId") long adId,
                         @RequestParam(value = "key") String key) {
        // 根据id获取对象
        Ad ad = adService.getAd(adId);
        if (ObjectUtils.isEmpty(ad)) {
            return jsonUtil.error(languageConstant.getUnfounded(), null);
        }
        // 修改属性
        if ("status".equals(key)) {
            int status = adService.toggleStatus(ad);
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
     * @param adId adId
     * @return 执行结果
     */
    @AuthRequired
    @ApiOperation(value = "删除广告", notes = "删除广告")
    @ApiImplicitParam(value = "adId", required = true)
    @Logging(module = LogConstant.MODULE_AD, type = LogConstant.TYPE_DELETE, description = "删除广告")
    @RequestMapping(value = "/{adId}", method = RequestMethod.DELETE)
    public String deleteAd(@PathVariable long adId) {
        adService.deleteAd(adId);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @ApiOperation(value = "批量删除广告", notes = "批量删除广告")
    @ApiImplicitParam(value = "ids", required = true)
    @Logging(module = LogConstant.MODULE_AD, type = LogConstant.TYPE_DELETE, description = "批量删除广告")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchAd(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        adService.deleteBatchAd(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
