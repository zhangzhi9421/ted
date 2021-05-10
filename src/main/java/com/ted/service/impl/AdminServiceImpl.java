package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Admin;
import com.ted.entity.ListRequestParam;
import com.ted.enums.GenderEnum;
import com.ted.enums.StatusEnum;
import com.ted.mapper.AdminMapper;
import com.ted.mapper.AttachmentMapper;
import com.ted.service.AdminService;
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
 * AdminService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("AdminService")
@PropertySource(value = {"classpath:config/general.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AdminServiceImpl implements AdminService {

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

    private AdminMapper adminMapper;
    private DateConverter dateConverter;
    private LanguageConstant languageConstant;
    private UploadConstant uploadConstant;
    private AttachmentMapper attachmentMapper;
    private DateConstant dateConstant;

    @Autowired
    public void setAttachmentMapper(@Qualifier("AttachmentMapper") AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @Autowired
    public void setAdminMapper(@Qualifier("AdminMapper") AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public Admin getAdmin(BigInteger uid) {
        return adminMapper.getAdmin(uid);
    }

    @Override
    public List<Admin> listAdmin(ListRequestParam requestParam) {
        return adminMapper.listAdmin(requestParam);
    }

    @Override
    public PageInfo<Admin> pageAdmin(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Admin> list = this.listAdmin(requestParam);
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
    public void deleteAdmin(BigInteger uid) {
        // 删除
        adminMapper.deleteAdmin(uid);
        // 清空头像
        this.handlerProfile(uid, null);
    }

    @Override
    @Transactional
    public void deleteBatchAdmin(String[] array) {
        // 删除
        adminMapper.deleteBatchAdmin(array);
        // 清空头像
        for (String uid : array) {
            this.handlerProfile(new BigInteger(uid), null);
        }
    }

    @Override
    @Transactional
    public void insertAdmin(Admin admin) {
        int i = adminMapper.insertAdmin(admin);
        if (i > 0) {
            this.handlerAssociated(admin);
        }

    }

    @Override
    @Transactional
    public void updateAdmin(Admin admin) {
        int i = adminMapper.updateAdmin(admin);
        if (i > 0) {
            this.handlerAssociated(admin);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param admin admin对象
     */
    private void handlerAssociated(Admin admin) {
        // 处理头像
        this.handlerProfile(admin.getUid(), admin.getProfileIds());
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
        params.put("tableName", "admin");
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
    public int countAdmin(Map<String, Object> params) {
        return adminMapper.countAdmin(params);
    }

    @Override
    public Admin getAdminByUsername(String username) {
        return adminMapper.getAdminByUsername(username);
    }

    @Override
    public Admin getAdminByToken(String token) {
        return adminMapper.getAdminByToken(token);
    }

    @Override
    public String updateToken(Admin admin) {
        String token = this.setToken(admin.getUid());
        int validTime = this.setValidTime();
        admin.setToken(token);
        admin.setValidTime(validTime);
        // 更新信息
        int updateAdmin = adminMapper.updateAdmin(admin);
        if (updateAdmin <= 0) {
            return "";
        }
        return token;
    }

    @Override
    public void updateValid(Admin admin) {
        int validTime = this.setValidTime();
        admin.setValidTime(validTime);
        adminMapper.updateAdmin(admin);
    }

    @Override
    public String getName(Admin admin) {
        String name = admin.getNickname();
        if (StringUtils.isBlank(name)) {
            name = admin.getRealName();
        }
        if (StringUtils.isBlank(name)) {
            name = admin.getUsername();
        }
        return name;
    }

    @Override
    public String getProfilePath(Admin admin) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String profilePath = basePath + defaultProfile;
        String profile = admin.getProfile();
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
    public void itemsAdmin(List<Admin> list) {
        // 遍历
        for (Admin admin : list) {
            handlerDetail(admin);
        }
    }

    @Override
    public int toggleStatus(Admin admin) {
        int status = admin.getStatus();
        if (status == statusActive) {
            // 去掉该标志
            admin.setStatus(status & (~statusActive));
        } else {
            // 增加该标志
            admin.setStatus(status | statusActive);
        }
        int updateAdmin = adminMapper.updateAdmin(admin);

        int result = 0;
        if (updateAdmin > 0) {
            result = admin.getStatus();
        }
        return result;
    }

    @Override
    public void detailAdmin(Admin admin) {
        this.handlerDetail(admin);
    }

    /**
     * @param admin 对象
     */
    private void handlerDetail(Admin admin) {
        // 枚举[状态]
        StatusEnum statusEnum = StatusEnum.getEnum(admin.getStatus());
        admin.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
        // 枚举[性别]
        GenderEnum genderEnum = GenderEnum.getEnum(admin.getGender());
        admin.setGenderLabel(!ObjectUtils.isEmpty(genderEnum) ? genderEnum.getName() : languageConstant.getUnknown());
        admin.setCreateDate(dateConverter.dateFormat(admin.getCreateTime()));
        // 去掉密码
        admin.setPassword(null);
    }

    @Override
    public boolean verifyUsername(Admin admin) {
        BigInteger uid = admin.getUid();
        int compare = uid.compareTo(BigInteger.valueOf(0));
        if (compare == 0) {
            // uid = 0，新增
            Admin adminByUsername = adminMapper.getAdminByUsername(admin.getUsername());
            // 存在
            return ObjectUtils.isEmpty(adminByUsername);
        } else {
            // 更新
            Admin adminByUsernameNotUid = adminMapper.getAdminByUsernameNotUid(admin.getUsername(), admin.getUid());
            // 存在
            return ObjectUtils.isEmpty(adminByUsernameNotUid);
        }
    }

    @Override
    public void handlerRequest(Admin admin) {
        // 密码
        if (StringUtils.isNotBlank(admin.getPassword())) {
            admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
        }
        // 发布时间
        if (StringUtils.isNotBlank(admin.getCreateDate())) {
            // 日期转时间戳
            admin.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(admin.getCreateDate())));
        } else {
            admin.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
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
