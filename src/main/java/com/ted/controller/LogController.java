package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.ListRequestParam;
import com.ted.entity.Log;
import com.ted.service.LogService;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Log控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@RequestMapping(value = "/log")
public class LogController extends BaseController {

    private final LogService logService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired
    public LogController(@Qualifier("LogService") LogService logService) {
        this.logService = logService;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<Map<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * 根据id获取对象
     *
     * @param id id
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getLog(@PathVariable BigInteger id) {
        // 获取对象
        Log log = logService.getLog(id);
        if (!ObjectUtils.isEmpty(log)) {
            logService.detailLog(log);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", log);
            return jsonUtil.success(data);

        }
        return jsonUtil.error();
    }

    /**
     * @param requestParam 请求参数
     * @return 列表
     */
    @AuthRequired
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listLog(ListRequestParam requestParam) {
        HashMap<String, Object> data = new HashMap<>();
        // 分页查询数据
        PageInfo<Log> pageInfo = logService.pageLog(requestParam);
        // 构造返回数据
        data.put("total", pageInfo.getTotal());
        List<Log> items = pageInfo.getList();
        logService.itemsLog(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 删除
     *
     * @param id id
     * @return 结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LOG, type = LogConstant.TYPE_DELETE, description = "删除日志")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String deleteLog(@PathVariable BigInteger id) {
        logService.deleteLog(id);
        return jsonUtil.success();
    }

    /**
     * 批量删除
     *
     * @param ids id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_LOG, type = LogConstant.TYPE_DELETE, description = "批量删除日志")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchLog(@RequestParam(value = "ids") String ids) {
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        logService.deleteBatchLog(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
