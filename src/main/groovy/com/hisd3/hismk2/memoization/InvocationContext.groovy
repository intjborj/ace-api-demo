package com.hisd3.hismk2.memoization

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

class InvocationContext {

    public static final String TEMPLATE = "%s.%s(%s)";

    private final Class targetClass;
    private final String targetMethod;
    private final Object[] args;

      InvocationContext(Class targetClass, String targetMethod, Object[] args) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.args = args;
    }

      Class getTargetClass() {
        return targetClass;
    }

      String getTargetMethod() {
        return targetMethod;
    }

      Object[] getArgs() {
        return args;
    }

    @Override
      boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
      int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
      String toString() {
        return String.format(TEMPLATE, targetClass.getName(), targetMethod, Arrays.toString(args));
    }
}
