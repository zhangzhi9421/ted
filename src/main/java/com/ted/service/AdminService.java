package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.Admin;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * AdminService接口类
 *
 * @author Ted
 */
public interface AdminService {
    /**
     * 通过id获取对象
     *
     * @param uid uid
     * @return Admin对象
     */
    Admin getAdmin(BigInteger uid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Admin集合
     */
    List<Admin> listAdmin(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<Admin> pageAdmin(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param uid uid
     */
    void deleteAdmin(BigInteger uid);

    /**
     * 批量删除
     *
     * @param array uid列表
     */
    void deleteBatchAdmin(String[] array);

    /**
     * 新增
     *
     * @param admin Admin对象
     */
    void insertAdmin(Admin admin);

    /**
     * 更新
     *
     * @param admin Admin对象
     */
    void updateAdmin(Admin admin);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countAdmin(Map<String, Object> params);

    /**
     * 通过用户名获取对象
     * 通常用于登录等场景
     *
     * @param username 用户名
     * @return Admin对象
     */
    Admin getAdminByUsername(@Param(value = "username") String username);

    /**
     * 通过token获取对象
     * 通常用于验证token等场景
     *
     * @param token token
     * @return Admin对象
     */
    Admin getAdminByToken(@Param(value = "token") String token);

    /**
     * 更新Token
     *
     * @param admin Admin对象
     * @return token
     */
    String updateToken(Admin admin);

    /**
     * 更新Token有效期
     *
     * @param admin Admin对象
     */
    void updateValid(Admin admin);

    /**
     * 展示名称
     * 用于页面中展示
     *
     * @param admin Admin对象
     * @return 名称
     */
    String getName(Admin admin);

    /**
     * 头像完整路径
     *
     * @param admin Admin对象
     * @return 头像
     */
    String getProfilePath(Admin admin);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsAdmin(List<Admin> list);

    /**
     * 修改状态
     *
     * @param admin Admin对象
     * @return 状态
     */
    int toggleStatus(Admin admin);

    /**
     * 详情
     *
     * @param admin Admin对象
     */
    void detailAdmin(Admin admin);

    /**
     * 验证是否存在相同记录
     *
     * @param admin Admin对象
     * @return 是否存在相同记录
     */
    boolean verifyUsername(Admin admin);

    /**
     * 处理请求参数
     *
     * @param admin Admin对象
     */
    void handlerRequest(Admin admin);
}
