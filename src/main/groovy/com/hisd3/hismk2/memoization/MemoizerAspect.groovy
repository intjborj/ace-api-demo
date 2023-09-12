package com.hisd3.hismk2.memoization

import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired

@Aspect
@Slf4j
class MemoizerAspect {

    @Autowired
    private RequestScopeCache requestScopeCache;

    @Around("@annotation(com.hisd3.hismk2.memoization.Memoize)")
     Object memoize(ProceedingJoinPoint pjp) throws Throwable {
        InvocationContext invocationContext = new InvocationContext(
                pjp.getSignature().getDeclaringType(),
                pjp.getSignature().getName(),
                pjp.getArgs()
        );
        Object result = requestScopeCache.get(invocationContext)
        if (RequestScopeCache.NONE == result) {
            result = pjp.proceed()
            log.info("Memoizing result {}, for method invocation: {}",  result.class.name, invocationContext)
            requestScopeCache.put(invocationContext, result)
        } else {
            log.info("Using memoized result: {}, for method invocation: {}", result.class.name, invocationContext)
        }
        return result
    }
}
