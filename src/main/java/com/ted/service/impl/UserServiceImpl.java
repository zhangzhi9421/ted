package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.ListRequestParam;
import com.ted.entity.User;
import com.ted.enums.GenderEnum;
import com.ted.enums.StatusEnum;
import com.ted.mapper.AttachmentMapper;
import com.ted.mapper.UserMapper;
import com.ted.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * UserService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("UserService")
@PropertySource(value = {"classpath:config/general.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    /**
     * 默认头像
     */
    @Value("${defaultProfile:/images/profile.png}")
    private String defaultProfile;

    /**
     * Token有效期：3天
     */
    @Value("${tokenValidDay:3}")
    private int validDay;

    /**
     * 状态-正常
     */
    @Value("${statusActive}")
    private int statusActive;

    private UserMapper userMapper;
    private DateConverter dateConverter;
    private LanguageConstant languageConstant;
    private UploadConstant uploadConstant;
    private AttachmentMapper attachmentMapper;
    private DateConstant dateConstant;

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setAttachmentMapper(@Qualifier("AttachmentMapper") AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setUserMapper(@Qualifier("UserMapper") UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public User getUser(BigInteger uid) {
        return userMapper.getUser(uid);
    }

    @Override
    public List<User> listUser(ListRequestParam requestParam) {
        return userMapper.listUser(requestParam);
    }

    @Override
    public PageInfo<User> pageUser(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<User> list = this.listUser(requestParam);
        // 3.填装数据
        return new PageInfo<>(list);
    }

    /**
     * 处理请求参数
     *
     * @param requestParam 请求参数
     */
    private void handlerRequestParam(ListRequestParam requestParam) {
        // 时间段
        if (StringUtils.isNotBlank(requestParam.getBeginDate()) && StringUtils.isNotBlank(requestParam.getEndDate())) {
            requestParam.setBeginTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getBeginDate(), dateConstant.getPatternYmd())));
            requestParam.setEndTime(dateConverter.timeSecond(dateConverter.timestampFormat(requestParam.getEndDate() + " " + dateConstant.getPatternEndOfDay(), dateConstant.getPatternYmdHis())));
        }
        // 排序
        if (StringUtils.isNotBlank(requestParam.getSort())) {
            String orderBy = requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteUser(BigInteger uid) {
        // 删除
        userMapper.deleteUser(uid);
        // 清空头像
        this.handlerProfile(uid, null);
    }

    @Override
    @Transactional
    public void deleteBatchUser(String[] array) {
        // 删除
        userMapper.deleteBatchUser(array);
        // 清空头像
        for (String uid : array) {
            this.handlerProfile(new BigInteger(uid), null);
        }
    }

    @Override
    @Transactional
    public void insertUser(User user) {
        int i = userMapper.insertUser(user);
        if (i > 0) {
            this.handlerAssociated(user);
        }

    }

    @Override
    @Transactional
    public void updateUser(User user) {
        int i = userMapper.updateUser(user);
        if (i > 0) {
            this.handlerAssociated(user);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param user user对象
     */
    private void handlerAssociated(User user) {
        // 处理头像
        this.handlerProfile(user.getUid(), user.getProfileIds());
    }

    /**
     * 处理头像附件
     *
     * @param uid        uid
     * @param profileIds 头像附件列表
     */
    private void handlerProfile(BigInteger uid, String profileIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tableId", uid);
        params.put("tableName", "user");
        params.put("tableField", "profile");
        log.error("保留的附件id========" + profileIds);
        // 如果字段为空，表示本次更新，需要清空所有符合条件的附件
        if (StringUtils.isNotBlank(profileIds)) {
            // 处理头像附件
            log.error("保留的附件id========" + profileIds);
            String[] profileArray = profileIds.split(",");
            params.put("array", profileArray);
            // 1.把原有表数据符合的附件记录，更新为status=0
            attachmentMapper.disableAttachmentExclude(params);
            // 2.把保留的附件设置成status=1，及其他表字段赋值
            attachmentMapper.disableAttachment(params);
        } else {
            // 1.把原有表数据符合的附件记录，更新为status=0
            attachmentMapper.disableAttachmentExclude(params);
        }
    }

    @Override
    public int countUser(Map<String, Object> params) {
        return userMapper.countUser(params);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public User getUserByToken(String token) {
        return userMapper.getUserByToken(token);
    }

    @Override
    public String updateToken(User user) {
        String token = this.setToken(user.getUid());
        int validTime = this.setValidTime();
        user.setToken(token);
        user.setValidTime(validTime);
        // 更新信息
        int updateUser = userMapper.updateUser(user);
        if (updateUser <= 0) {
            return "";
        }
        return token;
    }

    @Override
    public void updateValid(User user) {
        int validTime = this.setValidTime();
        user.setValidTime(validTime);
        userMapper.updateUser(user);
    }

    @Override
    public String getName(User user) {
        String name = user.getNickname();
        if (StringUtils.isBlank(name)) {
            name = user.getRealName();
        }
        if (StringUtils.isBlank(name)) {
            name = user.getUsername();
        }
        return name;
    }

    @Override
    public String getProfilePath(User user) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String profilePath = basePath + defaultProfile;
        String profile = user.getProfile();
        if (StringUtils.isNotBlank(profile)) {
            // 如果有头像，返回完整URL
            if (profile.startsWith("http")) {
                profilePath = profile;
            } else {
                profilePath = basePath + uploadConstant.getUploadDir() + profile;
            }
        }
        return profilePath;
    }

    @Override
    public void itemsUser(List<User> list) {
        // 遍历
        for (User user : list) {
            handlerDetail(user);
        }
    }

    @Override
    public int toggleStatus(User user) {
        int status = user.getStatus();
        if (status == statusActive) {
            // 去掉该标志
            user.setStatus(status & (~statusActive));
        } else {
            // 增加该标志
            user.setStatus(status | statusActive);
        }
        int updateUser = userMapper.updateUser(user);

        int result = 0;
        if (updateUser > 0) {
            result = user.getStatus();
        }
        return result;
    }

    @Override
    public void detailUser(User user) {
        this.handlerDetail(user);
    }

    /**
     * @param user 对象
     */
    private void handlerDetail(User user) {
        // 枚举[状态]
        StatusEnum statusEnum = StatusEnum.getEnum(user.getStatus());
        user.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
        // 枚举[性别]
        GenderEnum genderEnum = GenderEnum.getEnum(user.getGender());
        user.setGenderLabel(!ObjectUtils.isEmpty(genderEnum) ? genderEnum.getName() : languageConstant.getUnknown());
        user.setCreateDate(dateConverter.dateFormat(user.getCreateTime()));
        // 去掉密码
        user.setPassword(null);
    }

    @Override
    public boolean verifyUsername(User user) {
        BigInteger uid = user.getUid();
        int compare = uid.compareTo(BigInteger.valueOf(0));
        if (compare == 0) {
            // uid = 0，新增
            User userByUsername = userMapper.getUserByUsername(user.getUsername());
            // 存在
            return ObjectUtils.isEmpty(userByUsername);
        } else {
            // 更新
            User userByUsernameNotUid = userMapper.getUserByUsernameNotUid(user.getUsername(), user.getUid());
            // 存在
            return ObjectUtils.isEmpty(userByUsernameNotUid);
        }
    }

    @Override
    public void handlerRequest(User user) {
        log.warn("user-----" + user.toString());
        // 密码
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        // 发布时间
        if (StringUtils.isNotBlank(user.getCreateDate())) {
            // 日期转时间戳
            user.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(user.getCreateDate())));
        } else {
            user.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }

    /**
     * 设置Token有效期
     *
     * @return 有效期
     */
    private int setValidTime() {
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        return timestamp + (60 * 60 * 24 * validDay);
    }

    /**
     * 生成新的token
     *
     * @param uid 用户uid
     * @return token
     */
    private String setToken(BigInteger uid) {
        return BCrypt.hashpw(UUID.randomUUID().toString() + uid, BCrypt.gensalt());
    }
}
