package com.ted.controller;

import com.github.pagehelper.PageInfo;
import com.ted.annotation.AuthRequired;
import com.ted.annotation.Logging;
import com.ted.constant.LanguageConstant;
import com.ted.constant.LogConstant;
import com.ted.entity.Attachment;
import com.ted.entity.ListRequestParam;
import com.ted.service.AttachmentService;
import com.ted.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attachment控制器
 *
 * @author Ted
 */
@Slf4j
@RestController
@RequestMapping(value = "/attachment")
public class AttachmentController extends BaseController {

    private final AttachmentService attachmentService;
    private JsonUtil<Map<String, Object>> jsonUtil;
    private LanguageConstant languageConstant;

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Autowired
    public AttachmentController(@Qualifier("AttachmentService") AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Autowired(required = false)
    public void setJsonUtil(JsonUtil<Map<String, Object>> jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * @param isPage       是否分页
     * @param requestParam 请求参数
     * @return 列表
     */
    @AuthRequired
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listAttachment(@RequestParam(value = "isPage", required = false, defaultValue = "0") int isPage,
                                 ListRequestParam requestParam) {
        HashMap<String, Object> data = new HashMap<>();
        if (isPage <= 0) {
            // 不分页
            // 分页查询数据
            List<Attachment> list = attachmentService.listAttachment(requestParam);
            // 构造返回数据
            attachmentService.itemsAttachment(list);
            data.put("items", list);
        } else {
            // 分页查询数据
            PageInfo<Attachment> pageInfo = attachmentService.pageAttachment(requestParam);
            // 构造返回数据
            data.put("total", pageInfo.getTotal());
            List<Attachment> items = pageInfo.getList();
            attachmentService.itemsAttachment(items);
            data.put("items", items);
        }
        return jsonUtil.success(data);
    }

    /**
     * 删除
     *
     * @param file_id 附件id
     * @return 结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ATTACHMENT, type = LogConstant.TYPE_DELETE, description = "删除附件")
    @RequestMapping(value = "/{file_id}", method = RequestMethod.DELETE)
    public String deleteAttachment(@PathVariable BigInteger file_id) {
        attachmentService.deleteAttachment(file_id);
        return jsonUtil.success();
    }

    /**
     * 批量删除
     *
     * @param ids 目标id
     * @return 执行结果
     */
    @AuthRequired
    @Logging(module = LogConstant.MODULE_ATTACHMENT, type = LogConstant.TYPE_DELETE, description = "批量删除附件")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBatchAttachment(@RequestParam(value = "ids") String ids) {
        if (StringUtils.isBlank(ids)) {
            return jsonUtil.error(languageConstant.getFail(), null);
        }
        // 分隔字符串
        String[] array = ids.split(",");
        attachmentService.deleteBatchAttachment(array);
        return jsonUtil.success(languageConstant.getSuccess(), null);
    }
}
