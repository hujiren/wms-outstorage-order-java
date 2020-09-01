package com.apl.wms.outstorage.operator.aop;

import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonAplConstants;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.tenant.AplTenantConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DatasourceAop {


    @Autowired
    AplCacheUtil aplCacheUtil;

    @Pointcut("execution(public * com.apl.wms.outstorage.operator.controller.*.* (..))")
    public void datasourceAop() {
    }

    @Around("datasourceAop()")
    public Object doInvoke(ProceedingJoinPoint pjp) throws Throwable {

        Object proceed = null;
        try {
            String token = CommonContextHolder.getHeader(CommonAplConstants.TOKEN_FLAG);

            // 安全用户上下文
            SecurityUser securityUser = CommonContextHolder.getSecurityUser(aplCacheUtil, token);

            CommonContextHolder.securityUserContextHolder.set(securityUser);

            // 多租户ID值
            AplTenantConfig.tenantIdContextHolder.set(securityUser.getInnerOrgId());

            Object[] args = pjp.getArgs();
            proceed = pjp.proceed(args);

        } catch (Throwable e) {
            throw e;
        } finally {
            CommonContextHolder.securityUserContextHolder.remove();
            CommonContextHolder.tokenContextHolder.remove();
        }

        return proceed;
    }


}
