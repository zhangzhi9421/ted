package com.ted.mapper;

import com.ted.entity.Admin;
import com.ted.entity.ListRequestParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * AdminMapper
 *
 * @author Ted
 */
@Mapper
@Component("AdminMapper")
public interface AdminMapper {
    /**
     * 通过id获取对象
     *
     * @param uid uid
     * @return Admin对象
     */
    Admin getAdmin(@Param(value = "uid") BigInteger uid);

    /**
     * 获取列表
     *
     * @param requestParam 请求参数
     * @return Admin集合
     */
    List<Admin> listAdmin(ListRequestParam requestParam);

    /**
     * 删除
     *
     * @param uid uid
     */
    void deleteAdmin(BigInteger uid);

    /**
     * 批量删除
     *
     * @param array 列表
     */
    void deleteBatchAdmin(String[] array);

    /**
     * 新增
     *
     * @param admin Admin对象
     */
    int insertAdmin(Admin admin);

    /**
     * 修改
     *
     * @param admin Admin对象
     */
    int updateAdmin(Admin admin);

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
     * 通过用户名和uid获取对象
     * 通常用于更新时判断是否存在相同用户名等场景
     *
     * @param username 用户名
     * @param uid      uid
     * @return Admin对象
     */
    Admin getAdminByUsernameNotUid(@Param(value = "username") String username, @Param(value = "uid") BigInteger uid);

    /**
     * 通过token获取对象
     * 通常用于验证token等场景
     *
     * @param token token
     * @return Admin对象
     */
    Admin getAdminByToken(@Param(value = "token") String token);
}
