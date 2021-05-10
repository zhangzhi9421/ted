package com.ted.controller;

import com.ted.annotation.AuthRequired;
import com.ted.entity.Admin;
import com.ted.service.AdminService;
import com.ted.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 授权
 *
 * @author Ted
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthController extends BaseController {
    private final AdminService adminService;
    private JsonUtil<Map<String, Object>> jsonUtil;

    @Autowired
    public AuthController(@Qualifier("AdminService") AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<Map<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * 登录
     *
     * @param admin 提交参数
     * @return Json返回值
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Admin admin) {
        String username = admin.getUsername();
        String password = admin.getPassword();

        if (username.equals("") || password.equals("")) {
            return jsonUtil.error("用户名或者密码不能为空", null);
        }

        // 获取对象
        Admin adminModel = adminService.getAdminByUsername(username);
        // 用户不存在
        if (ObjectUtils.isEmpty(adminModel)) {
            return jsonUtil.error("用户不存在", null);
        }
        // 验证密码
        boolean checkPassword = BCrypt.checkpw(password, adminModel.getPassword());
        if (!checkPassword) {
            return jsonUtil.error("密码错误", null);
        }
        // 验证是否禁用
        if (adminModel.getStatus() <= 0) {
            return jsonUtil.error("用户已禁用", null);
        }

        // 更新token
        String token = adminService.updateToken(adminModel);
        if (!token.equals("")) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", token);
            return jsonUtil.success(map);
        }
        return jsonUtil.error("操作失败", null);
    }

    /**
     * 获取信息
     *
     * @param token token
     * @return Json返回值
     */
    @AuthRequired
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info(String token) {

        if (token.equals("")) {
            return jsonUtil.error("Token不能为空", null);
        }

        // 获取对象
        Admin admin = adminService.getAdminByToken(token);
        // 用户不存在
        if (ObjectUtils.isEmpty(admin)) {
            return jsonUtil.error("用户不存在", null);
        }
        // 验证是否禁用
        if (admin.getStatus() <= 0) {
            return jsonUtil.error("用户已禁用", null);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("introduction", adminService.getName(admin));
        map.put("avatar", adminService.getProfilePath(admin));
        map.put("name", adminService.getName(admin));
        map.put("id", admin.getUid());
        // 返回
        return jsonUtil.success(map);
    }

    /**
     * 登出
     *
     * @return Json返回值
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout() {
        return jsonUtil.success();
    }
}
