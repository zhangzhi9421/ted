package com.ted.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ted.constant.DateConstant;
import com.ted.constant.LanguageConstant;
import com.ted.constant.UploadConstant;
import com.ted.converter.DateConverter;
import com.ted.entity.Ad;
import com.ted.entity.ListRequestParam;
import com.ted.enums.StatusEnum;
import com.ted.mapper.AdMapper;
import com.ted.mapper.AttachmentMapper;
import com.ted.service.AdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdService实现类
 *
 * @author Ted
 */
@Slf4j
@Service("AdService")
@PropertySource(value = {"classpath:config/general.properties", "classpath:config/content.properties"})
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AdServiceImpl implements AdService {

    /**
     * 默认图片
     */
    @Value("${defaultPhoto:/images/defaultPhoto.png}")
    private String defaultPhoto;

    /**
     * 状态-正常
     */
    @Value("${statusActive}")
    private int statusActive;

    @Value("${maxInt}")
    private int maxInt;

    private AdMapper adMapper;
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
    public void setDateConstant(DateConstant dateConstant) {
        this.dateConstant = dateConstant;
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
    public void setAdMapper(@Qualifier("AdMapper") AdMapper adMapper) {
        this.adMapper = adMapper;
    }

    @Autowired
    public void setUploadConstant(UploadConstant uploadConstant) {
        this.uploadConstant = uploadConstant;
    }

    @Override
    public Ad getAd(long adId) {
        return adMapper.getAd(adId);
    }

    @Override
    public List<Ad> listAd(ListRequestParam requestParam) {
        return adMapper.listAd(requestParam);
    }

    @Override
    public List<Ad> listAdAssociationPosition(ListRequestParam requestParam) {
        return adMapper.listAdAssociationPosition(requestParam);
    }

    @Override
    public PageInfo<Ad> pageAd(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Ad> list = this.listAd(requestParam);
        // 3.填装数据
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Ad> pageAdAssociationPosition(ListRequestParam requestParam) {
        // 处理请求参数
        this.handlerRequestParam(requestParam);
        // 1.开启分页
        PageHelper.startPage(requestParam.getPage(), requestParam.getLimit());
        // 2.查询数据
        List<Ad> list = this.listAdAssociationPosition(requestParam);
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
            String orderBy = "`ad`." + requestParam.getSort() + " " + ((requestParam.getAsc() == 1) ? "ASC" : "DESC");
            requestParam.setOrderBy(orderBy);
        }
    }

    @Override
    @Transactional
    public void deleteAd(long adId) {
        // 删除
        adMapper.deleteAd(adId);
        // 清空头像
        this.handlerPhoto(adId, null);
    }

    @Override
    @Transactional
    public void deleteBatchAd(String[] array) {
        // 删除
        adMapper.deleteBatchAd(array);
        // 清空头像
        for (String adId : array) {
            this.handlerPhoto(Long.parseLong(adId), null);
        }
    }

    @Override
    @Transactional
    public void insertAd(Ad ad) {
        int i = adMapper.insertAd(ad);
        if (i > 0) {
            this.handlerAssociated(ad);
        }
    }

    @Override
    @Transactional
    public void updateAd(Ad ad) {
        int i = adMapper.updateAd(ad);
        if (i > 0) {
            this.handlerAssociated(ad);
        }
    }

    /**
     * 处理相关联的事务
     *
     * @param ad ad对象
     */
    private void handlerAssociated(Ad ad) {
        // 处理头像
        this.handlerPhoto(ad.getAdId(), ad.getPhotoIds());
    }

    /**
     * 处理头像附件
     *
     * @param adId     adId
     * @param photoIds 头像附件列表
     */
    private void handlerPhoto(long adId, String photoIds) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tableId", adId);
        params.put("tableName", "ad");
        params.put("tableField", "photo");
        // 如果字段为空，表示本次更新，需要清空所有符合条件的附件
        if (StringUtils.isNotBlank(photoIds)) {
            // 处理头像附件
            log.error("保留的附件id========" + photoIds);
            String[] photoArray = photoIds.split(",");
            params.put("array", photoArray);
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
    public int countAd(Map<String, Object> params) {
        return adMapper.countAd(params);
    }

    @Override
    public String getPhotoPath(Ad ad) {
        // 当前完整域名URL
        String basePath = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }

        String photoPath = basePath + defaultPhoto;
        String photo = ad.getPhoto();
        if (StringUtils.isNotBlank(photo)) {
            // 如果有头像，返回完整URL
            if (photo.startsWith("http")) {
                photoPath = photo;
            } else {
                photoPath = basePath + uploadConstant.getUploadDir() + photo;
            }
        }
        return photoPath;
    }

    @Override
    public void itemsAd(List<Ad> list) {
        // 当前时间戳
        long timestamp = dateConverter.timeSecond(dateConverter.timestampFormat());
        // 遍历
        for (Ad ad : list) {
            // 发布时间
            ad.setCreateDate(dateConverter.dateFormat(ad.getCreateTime()));
            // 开始时间
            ad.setStartDate(dateConverter.dateFormat(ad.getStartTime()));
            // 结束时间
            ad.setEndDate(dateConverter.dateFormat(ad.getEndTime()));
            // 枚举[状态]
            StatusEnum statusEnum = StatusEnum.getEnum(ad.getStatus());
            ad.setStatusLabel(!ObjectUtils.isEmpty(statusEnum) ? statusEnum.getName() : languageConstant.getUnknown());
            // 是否有效
            if (ad.getStartTime() > timestamp) {
                // 开始时间大于当前时间，未生效
                ad.setIsValid(0);
                ad.setIsValidLabel("未生效");
            } else if (ad.getEndTime() < timestamp) {
                // 结束时间小于当前时间，已过期
                ad.setIsValid(2);
                ad.setIsValidLabel("已过期");
            } else {
                ad.setIsValid(1);
                ad.setIsValidLabel("生效中");
            }
            // 所属位置信息
            log.error("ad位置-"+ad.getAdPosition());
        }
    }

    @Override
    public int toggleStatus(Ad ad) {
        int status = ad.getStatus();
        if (status == statusActive) {
            // 去掉该标志
            ad.setStatus(status & (~statusActive));
        } else {
            // 增加该标志
            ad.setStatus(status | statusActive);
        }
        int updateAd = adMapper.updateAd(ad);

        int result = 0;
        if (updateAd > 0) {
            result = ad.getStatus();
        }
        return result;
    }

    @Override
    public void detailAd(Ad ad) {
        // 发布时间
        ad.setCreateDate(dateConverter.dateFormat(ad.getCreateTime()));
        // 开始时间
        ad.setStartDate(dateConverter.dateFormat(ad.getStartTime()));
        // 结束时间
        ad.setEndDate(dateConverter.dateFormat(ad.getEndTime()));
        // 有效时间
        // 排序：如果是默认的最大值，设置为0，前端不显示
        if (ad.getSort() >= maxInt) {
            ad.setSort(0);
        }
    }

    @Override
    public void handlerRequest(Ad ad) {
        // 发布时间
        if (StringUtils.isNotBlank(ad.getCreateDate())) {
            // 日期转时间戳
            ad.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat(ad.getCreateDate())));
        } else {
            ad.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
        // 开始时间
        if (StringUtils.isNotBlank(ad.getStartDate())) {
            // 日期转时间戳
            ad.setStartTime(dateConverter.timeSecond(dateConverter.timestampFormat(ad.getStartDate())));
        } else {
            ad.setStartTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
        // 结束时间
        if (StringUtils.isNotBlank(ad.getEndDate())) {
            // 日期转时间戳
            ad.setEndTime(dateConverter.timeSecond(dateConverter.timestampFormat(ad.getEndDate())));
        } else {
            ad.setEndTime(dateConverter.timeSecond(dateConverter.timestampFormat()));
        }
    }
}
