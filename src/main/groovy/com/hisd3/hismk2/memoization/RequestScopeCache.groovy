package com.hisd3.hismk2.memoization

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component



@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
class RequestScopeCache {

    public static final Object NONE = new Object()

    private final Map<InvocationContext, Object> cache = new HashMap<InvocationContext, Object>()

      Object get(InvocationContext invocationContext) {
        return cache.containsKey(invocationContext) ? cache.get(invocationContext) : NONE
    }

      void put(InvocationContext methodInvocation, Object result) {
        cache.put(methodInvocation, result)
    }
}
