package org.mockito.internal.creation;

import java.lang.reflect.Method;

import org.mockito.internal.invocation.MockitoMethod;

public class DelegatingMethod implements MockitoMethod {

    private final Method method;

    public DelegatingMethod(Method method) {
        assert method != null : "Method cannot be null";
        this.method = method;
    }

    public Class<?>[] getExceptionTypes() {
        return method.getExceptionTypes();
    }

    public Method getJavaMethod() {
        return method;
    }

    public String getName() {
        return method.getName();
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public boolean isVarArgs() {
        return method.isVarArgs();
    }
    
    @Override
    public int hashCode() {
        throw new RuntimeException("hashCode() not implemented");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DelegatingMethod) {
            DelegatingMethod that = (DelegatingMethod) obj;
            return method.equals(that.method);
        }
        return method.equals(obj);
    }
}