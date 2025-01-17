package org.mockito.internal.util;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.mockito.internal.invocation.MockitoMethod;
import org.mockito.internal.invocation.SerializableMethod;

public class ObjectMethodsGuru implements Serializable {

    private static final long serialVersionUID = -1286718569065470494L;

    public boolean isToString(Method method) {
        return isToString(new SerializableMethod(method));
    }

    public boolean isToString(MockitoMethod method) {
        return method.getReturnType() == String.class && method.getParameterTypes().length == 0
                && method.getName().equals("toString");
    }

    public boolean isEqualsMethod(Method method) {
        return method.getName().equals("equals") && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class;
    }

    public boolean isHashCodeMethod(Method method) {
        return method.getName().equals("hashCode") && method.getParameterTypes().length == 0;
    }
}