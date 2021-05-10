package com.ted.constant;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 日志常量
 *
 * @author Ted
 */
@Data
@Component
public class LogConstant {
    // 操作模块-静态常量
    public static final String MODULE_ADMIN = "系统用户";
    public static final String MODULE_ATTACHMENT = "附件";
    public static final String MODULE_LOG = "日志";
    public static final String MODULE_CATEGORY = "栏目分类";
    public static final String MODULE_ARTICLE = "文章";
    public static final String MODULE_LINK = "友情链接";
    public static final String MODULE_USER = "会员";
    public static final String MODULE_AD_POSITION = "广告位置";
    public static final String MODULE_AD = "广告";

    // 操作类型-静态常量
    public static final String TYPE_INSERT = "新增";
    public static final String TYPE_UPDATE = "修改";
    public static final String TYPE_DELETE = "删除";
    public static final String TYPE_TOGGLE = "更新状态";
    public static final String TYPE_SELECT = "查询";

    /**
     * 操作模块
     *
     * @return 操作模块列表
     */
    public ArrayList<String> module() {
        ArrayList<String> list = new ArrayList<>();
        list.add(MODULE_ADMIN);
        list.add(MODULE_ATTACHMENT);
        list.add(MODULE_LOG);
        list.add(MODULE_CATEGORY);
        list.add(MODULE_ARTICLE);
        list.add(MODULE_LINK);
        list.add(MODULE_USER);
        list.add(MODULE_AD_POSITION);
        list.add(MODULE_AD);
        return list;
    }

    /**
     * 操作类型
     *
     * @return 操作类型列表
     */
    public ArrayList<String> type() {
        ArrayList<String> list = new ArrayList<>();
        list.add(TYPE_INSERT);
        list.add(TYPE_UPDATE);
        list.add(TYPE_DELETE);
        list.add(TYPE_TOGGLE);
        // 查询不记录操作日志 list.add(TYPE_SELECT);
        return list;
    }
}
