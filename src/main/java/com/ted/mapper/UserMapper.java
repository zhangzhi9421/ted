package com.ted.mapper;

import com.ted.entity.ListRequestParam;
import com.ted.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * UserMapper
 *
 * @author Ted
 */
@Mapper
@Component("UserMapper")
public interface UserMapper {
    /**
     * 通过id获取对象
     *
     * @param uid uid
     * @return User对象
     */
    User getUser(@Param(value = "uid") BigInteger uid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return User集合
     */
    List<User> listUser(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param uid uid
     */
    void deleteUser(BigInteger uid);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchUser(String[] array);

    /**
     * 新增
     *
     * @param user User对象
     */
    int insertUser(User user);

    /**
     * 修改
     *
     * @param user User对象
     */
    int updateUser(User user);

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
     * 通过用户名和uid获取对象
     * 通常用于更新时判断是否存在相同用户名等场景
     *
     * @param username 用户名
     * @param uid      uid
     * @return User对象
     */
    User getUserByUsernameNotUid(@Param(value = "username") String username, @Param(value = "uid") BigInteger uid);

    /**
     * 通过token获取对象
     * 通常用于验证token等场景
     *
     * @param token token
     * @return User对象
     */
    User getUserByToken(@Param(value = "token") String token);
}
