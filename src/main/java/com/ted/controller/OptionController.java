package com.ted.controller;

import com.ted.constant.LogConstant;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/option")
public class OptionController extends BaseController {
    /**
     * 搜索-状态
     */
    @Value("#{${searchStatus}}")
    private HashMap<Integer, String> statusOptions;

    private JsonUtil<Map<String, Object>> jsonUtil;
    private LogConstant logConstant;

    @Autowired
    public void setLogConstant(LogConstant logConstant) {
        this.logConstant = logConstant;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<Map<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * 状态选项列表
     *
     * @return 状态列表
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String statusOptions() {
        // 封装data
        Map<String, Object> data = new HashMap<>();
        data.put("options", statusOptions);
        return jsonUtil.success(data);
    }

    /**
     * 操作模块列表
     *
     * @return 操作模块列表
     */
    @RequestMapping(value = "/module", method = RequestMethod.GET)
    public String moduleOptions() {
        // 封装data
        Map<String, Object> data = new HashMap<>();
        data.put("options", logConstant.module());
        return jsonUtil.success(data);
    }

    /**
     * 操作类型列表
     *
     * @return 操作类型列表
     */
    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public String typeOptions() {
        // 封装data
        Map<String, Object> data = new HashMap<>();
        data.put("options", logConstant.type());
        return jsonUtil.success(data);
    }
}
