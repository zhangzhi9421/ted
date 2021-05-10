package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.ListRequestParam;
import com.ted.entity.User;
import com.ted.enums.StatusEnum;
import com.ted.service.UserService;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private final UserService userService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public UserController(@Qualifier("UserService") UserService userService) {
        this.userService = userService;
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
    public String listUser(ListRequestParam requestParam) {
        // 分页查询数据
        PageInfo<User> pageInfo = userService.pageUser(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<User> items = pageInfo.getList();
        userService.itemsUser(items);
        data.put("items", items);
        return jsonUtil.success(data);
    }

    /**
     * 根据id获取对象
     *
     * @param uid uid
     * @return 详情
     */
    @AuthRequired
    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public String getUser(@PathVariable BigInteger uid) {
        // 获取对象
        User user = userService.getUser(uid);
        if (!ObjectUtils.isEmpty(user)) {
            log.info("user-------------" + user);
            userService.detailUser(user);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", user);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param user User对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_USER, type = LogConstant.TYPE_INSERT, description = "新增会员")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertUser(@RequestBody User user) {
        if (!ObjectUtils.isEmpty(user)) {
            // 是否存在相同用户名
            boolean verifyUsername = userService.verifyUsername(user);
            if (!verifyUsername) {
                return jsonUtil.error(String.format(languageConstant.getDuplicated(), user.getUsername()), null);
            }
            // 处理参数
            userService.handlerRequest(user);
            // 执行
            userService.insertUser(user);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param user User对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_USER, type = LogConstant.TYPE_UPDATE, description = "更新会员")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateUser(@RequestBody User user) {
        if (!ObjectUtils.isEmpty(user)) {
            // 是否存在相同用户名
            boolean verifyUsername = userService.verifyUsername(user);
            if (!verifyUsername) {
                return jsonUtil.error(String.format(languageConstant.getDuplicated(), user.getUsername()), null);
            }
            userService.handlerRequest(user);
            // 执行
            userService.updateUser(user);
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
    @Logging(module = LogConstant.MODULE_USER, type = LogConstant.TYPE_TOGGLE, description = "修改会员状态")
    @RequestMapping(value = "/toggle", method = RequestMethod.GET)
    public String toggle(@RequestParam(value = "uid") BigInteger uid,
                         @RequestParam(value = "key") String key) {
        // 根据id获取对象
        User user = userService.getUser(uid);
        if (ObjectUtils.isEmpty(user)) {
            return jsonUtil.error(languageConstant.getUnfounded(), null);
        }
        // 修改属性
        if ("status".equals(key)) {
            int status = userService.toggleStatus(user);
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
     * @param uid uid
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_USER, type = LogConstant.TYPE_DELETE, description = "删除会员")
    @RequestMapping(value = "/{uid}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable BigInteger uid) {
        userService.deleteUser(uid);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_USER, type = LogConstant.TYPE_DELETE, description = "批量删除会员")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchUser(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        userService.deleteBatchUser(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
