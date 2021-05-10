package com.ted.service;

import com.github.pagehelper.PageInfo;
import com.ted.entity.ListRequestParam;
import com.ted.entity.User;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * UserService接口类
 *
 * @author Ted
 */
public interface UserService {
    /**
     * 通过id获取对象
     *
     * @param uid uid
     * @return User对象
     */
    User getUser(BigInteger uid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return User集合
     */
    List<User> listUser(ListRequestParam requestParam);

    /**
     * 分页查询
     *
     * @param requestParam 请求参数
     * @return PageInfo
     */
    PageInfo<User> pageUser(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param uid uid
     */
    void deleteUser(BigInteger uid);

    /**
     * 批量删除
     *
     * @param array uid列表
     */
    void deleteBatchUser(String[] array);

    /**
     * 新增
     *
     * @param user User对象
     */
    void insertUser(User user);

    /**
     * 更新
     *
     * @param user User对象
     */
    void updateUser(User user);

    /**
     * 统计总数
     *
     * @param params 查询参数
     * @return 总数
     */
    int countUser(Map<String, Object> params);

    /**
     * 通过用户名获取对象
     * 通常用于登录等场景
     *
     * @param username 用户名
     * @return User对象
     */
    User getUserByUsername(@Param(value = "username") String username);

    /**
     * 通过token获取对象
     * 通常用于验证token等场景
     *
     * @param token token
     * @return User对象
     */
    User getUserByToken(@Param(value = "token") String token);

    /**
     * 更新Token
     *
     * @param user User对象
     * @return token
     */
    String updateToken(User user);

    /**
     * 更新Token有效期
     *
     * @param user User对象
     */
    void updateValid(User user);

    /**
     * 展示名称
     * 用于页面中展示
     *
     * @param user User对象
     * @return 名称
     */
    String getName(User user);

    /**
     * 头像完整路径
     *
     * @param user User对象
     * @return 头像
     */
    String getProfilePath(User user);

    /**
     * 适用于前端的集合
     *
     * @param list 列表
     */
    void itemsUser(List<User> list);

    /**
     * 修改状态
     *
     * @param user User对象
     * @return 状态
     */
    int toggleStatus(User user);

    /**
     * 详情
     *
     * @param user User对象
     */
    void detailUser(User user);

    /**
     * 验证是否存在相同记录
     *
     * @param user User对象
     * @return 是否存在相同记录
     */
    boolean verifyUsername(User user);

    /**
     * 处理请求参数
     *
     * @param user User对象
     */
    void handlerRequest(User user);
}
