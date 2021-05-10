package com.ted.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ted.annotation.Logging;
import com.ted.converter.DateConverter;
import com.ted.converter.IpConverter;
import com.ted.entity.Admin;
import com.ted.entity.Log;
import com.ted.service.AdminService;
import com.ted.service.LogService;
import com.ted.util.IpUtil;
import com.ted.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志切面类
 *
 * @author Ted
 */
@Aspect
@Component
@PropertySource(value = {"classpath:config/general.properties"})
public class LogAspect {

    @Value("${tokenHeader}")
    private String tokenHeader;

    private LogService logService;
    private AdminService adminService;
    private IpConverter ipConverter;
    private IpUtil ipUtil;
    private Log log;
    private DateConverter dateConverter;

    /**
     * Jackson对象
     */
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setAdminService(@Qualifier("AdminService") AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setIpUtil(IpUtil ipUtil) {
        this.ipUtil = ipUtil;
    }

    @Autowired
    public void setLog(Log log) {
        this.log = log;
    }

    @Autowired
    public void setIpConverter(IpConverter ipConverter) {
        this.ipConverter = ipConverter;
    }

    @Autowired
    public void setLogService(@Qualifier("LogService") LogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setDateConverter(DateConverter dateConverter) {
        this.dateConverter = dateConverter;
    }

    /**
     * 设置日志切入点
     * 记录操作日志，在注解的位置切入代码
     */
    @Pointcut("@annotation(com.ted.annotation.Logging)")
    public void logPointCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param response  返回结果
     */
    @AfterReturning(value = "logPointCut()", returning = "response")
    public void saveLog(JoinPoint joinPoint, Object response) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        try {
            // 从切面切入点通过反射机制获取切入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在方法
            Method method = signature.getMethod();
            // 获取操作
            Logging logging = method.getAnnotation(Logging.class);
            // 获取注解中的值
            if (!ObjectUtils.isEmpty(logging)) {
                log.setModule(logging.module());
                log.setType(logging.type());
                log.setDescription(logging.description());
            }
            // 其他字段赋值
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            log.setMethod(methodName);

            // 请求的参数
            Object[] args = joinPoint.getArgs();
            String params = objectMapper.writeValueAsString(args);
            log.setParams(params);
            // URI
            log.setUri(request != null ? request.getRequestURI() : "");
            // IP
            String ip = ipUtil.getIp(request);
            log.setIp(ipConverter.ipToLong(ip));
            // 创建时间
            log.setCreateTime(dateConverter.timeSecond(dateConverter.timestampFormat()));

            // 操作用户
            if (request != null) {
                String token = request.getHeader(tokenHeader);
                if (StringUtils.isBlank(token)) {
                    token = request.getParameter("token");
                }
                Admin admin = adminService.getAdminByToken(token);
                if (!ObjectUtils.isEmpty(admin)) {
                    log.setUid(admin.getUid());
                    log.setUsername(admin.getUsername());
                }
            }
            // 分析返回值是否成功，不成功不记录日志
            // 解析返回值json
            JsonUtil<?> json = objectMapper.readValue(response.toString(), JsonUtil.class);
            if (json.getCode() == JsonUtil.SUCCESS_CODE) {
                // 添加日志记录
                int insertLog = logService.insertLog(log);
                System.out.println("创建日志：" + insertLog + "条");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
