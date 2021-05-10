package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Admin;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.service.AdminService;
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
 * Admin控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@PropertySource(value = {"classpath:config/general.properties"})
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public AdminController(@Qualifier("AdminService") AdminService adminService) {
        this.adminService = adminService;
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
    public String listAdmin(ListRequestParam requestParam) {
        // 分页查询数据
        PageInfo<Admin> pageInfo = adminService.pageAdmin(requestParam);
        // 构造返回数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", pageInfo.getTotal());
        List<Admin> items = pageInfo.getList();
        adminService.itemsAdmin(items);
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
    public String getAdmin(@PathVariable BigInteger uid) {
        // 获取对象
        Admin admin = adminService.getAdmin(uid);
        if (!ObjectUtils.isEmpty(admin)) {
            log.info("admin-------------" + admin);
            adminService.detailAdmin(admin);
            HashMap<String, Object> data = new HashMap<>();
            data.put("detail", admin);
            return jsonUtil.success(data);
        }
        return jsonUtil.error();
    }

    /**
     * 新增
     *
     * @param admin Admin对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ADMIN, type = LogConstant.TYPE_INSERT, description = "新增系统用户")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String insertAdmin(@RequestBody Admin admin) {
        if (!ObjectUtils.isEmpty(admin)) {
            // 是否存在相同用户名
            boolean verifyUsername = adminService.verifyUsername(admin);
            if (!verifyUsername) {
                return jsonUtil.error(String.format(languageConstant.getDuplicated(), admin.getUsername()), null);
            }
            // 处理参数
            adminService.handlerRequest(admin);
            // 执行
            adminService.insertAdmin(admin);
            return jsonUtil.success();
        }
        return jsonUtil.error();
    }

    /**
     * 更新
     *
     * @param admin Admin对象
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ADMIN, type = LogConstant.TYPE_UPDATE, description = "更新系统用户")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public String updateAdmin(@RequestBody Admin admin) {
        if (!ObjectUtils.isEmpty(admin)) {
            // 是否存在相同用户名
            boolean verifyUsername = adminService.verifyUsername(admin);
            if (!verifyUsername) {
                return jsonUtil.error(String.format(languageConstant.getDuplicated(), admin.getUsername()), null);
            }
            adminService.handlerRequest(admin);
            // 执行
            adminService.updateAdmin(admin);
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
    @Logging(module = LogConstant.MODULE_ADMIN, type = LogConstant.TYPE_TOGGLE, description = "修改系统用户状态")
    @RequestMapping(value = "/toggle", method = RequestMethod.GET)
    public String toggle(@RequestParam(value = "uid") BigInteger uid,
                         @RequestParam(value = "key") String key) {
        // 根据id获取对象
        Admin admin = adminService.getAdmin(uid);
        if (ObjectUtils.isEmpty(admin)) {
            return jsonUtil.error(languageConstant.getUnfounded(), null);
        }
        // 修改属性
        if ("status".equals(key)) {
            int status = adminService.toggleStatus(admin);
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
    @Logging(module = LogConstant.MODULE_ADMIN, type = LogConstant.TYPE_DELETE, description = "删除系统用户")
    @RequestMapping(value = "/{uid}", method = RequestMethod.DELETE)
    public String deleteAdmin(@PathVariable BigInteger uid) {
        adminService.deleteAdmin(uid);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ADMIN, type = LogConstant.TYPE_DELETE, description = "批量删除系统用户")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchAdmin(@RequestParam(value = "ids") String ids) {
        log.error("ids=" + ids);
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        adminService.deleteBatchAdmin(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
