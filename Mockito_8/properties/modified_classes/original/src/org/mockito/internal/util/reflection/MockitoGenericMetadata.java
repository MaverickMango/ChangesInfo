/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.reflection;


import org.mockito.Incubating;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.util.Checks;
import org.mockito.stubbing.Answer;

import java.lang.reflect.*;
import java.util.*;

import static org.mockito.Mockito.withSettings;


/**
 * This class can retrieve generic meta-data that the compiler stores on classes
 * and accessible members.
 *
 * <p>
 *     The main idea of this code is to create a Map that will help to resolve return types.
 *     In order to actually work with nested generics, this map will have to be passed along new instances
 *     as a type context.
 * </p>
 *
 * <p>
 *     Hence :
 *     <ul>
 *         <li>the metadata is created using the {@link #from(Type)} method from a real
 *         Class or from a ParameterizedType, other types are not yet supported.</li>
 *
 *         <li>Then from this metadata, we can extract meta-data for a generic return type of a method, using
 *         {@link #resolveGenericReturnType(Method)}.</li>
 *
 *         <li>Finally as we want to mock the actual type, but we want to pass along the contextual generics meta-data
 *         we need to create the mock ourselves as we know how to create it, depending on the kind of Type (Class,
 *         ParameterizedType, TypeVariable), the method {@link #toMock(Answer)} assumes this responsibility.</li>
 *     </ul>
 * </p>
 *
 * <p>
 * For now this code support the following kind of generic declarations :
 * <pre class="code"><code class="java">
 * interface GenericsNest&lt;K extends Comparable&lt;K&gt; & Cloneable&gt; extends Map&lt;K, Set&lt;Number&gt;&gt; {
 *     Set&lt;Number&gt; remove(Object key); // override with fixed ParameterizedType
 *     List&lt;? super Integer&gt; returning_wildcard_with_class_lower_bound();
 *     List&lt;? super K&gt; returning_wildcard_with_typeVar_lower_bound();
 *     List&lt;? extends K&gt; returning_wildcard_with_typeVar_upper_bound();
 *     K returningK();
 *     &lt;O extends K&gt; List&lt;O&gt; paramType_with_type_params();
 *     &lt;S, T extends S&gt; T two_type_params();
 *     &lt;O extends K&gt; O typeVar_with_type_params();
 *     Number returningNonGeneric();
 * }
 * </code></pre>
 *
 * @see #from(Type)
 * @see #resolveGenericReturnType(Method)
 * @see #toMock(Answer)
 * @see org.mockito.internal.stubbing.defaultanswers.ReturnsGenericDeepStubs
 */
@Incubating
public abstract class MockitoGenericMetadata {

    // public static MockitoLogger logger = new ConsoleMockitoLogger();

    /**
     * Represents actual type variables resolved for current class.
     */
    protected Map<TypeVariable, Type> contextualActualTypeParameters = new HashMap<TypeVariable, Type>();


    protected void registerTypeVariablesOn(Type classType) {
        if (!(classType instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) classType;
        TypeVariable[] typeParameters = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            TypeVariable typeParameter = typeParameters[i];
            Type actualTypeArgument = actualTypeArguments[i];

            if (actualTypeArgument instanceof WildcardType) {
                contextualActualTypeParameters.put(typeParameter, boundsOf((WildcardType) actualTypeArgument));
            } else {
                contextualActualTypeParameters.put(typeParameter, actualTypeArgument);
            }
            // logger.log("For '" + parameterizedType + "' found type variable : { '" + typeParameter + "(" + System.identityHashCode(typeParameter) + ")" + "' : '" + actualTypeArgument + "(" + System.identityHashCode(typeParameter) + ")" + "' }");
        }
    }

    protected void registerTypeParametersOn(TypeVariable[] typeParameters) {
        for (TypeVariable typeParameter : typeParameters) {
            contextualActualTypeParameters.put(typeParameter, boundsOf(typeParameter));
            // logger.log("For '" + typeParameter.getGenericDeclaration() + "' found type variable : { '" + typeParameter + "(" + System.identityHashCode(typeParameter) + ")" + "' : '" + boundsOf(typeParameter) + "' }");
        }
    }

    /**
     * @param typeParameter The TypeVariable parameter
     * @return A {@link BoundedType} for easy bound information, if first bound is a TypeVariable
     *         then retrieve BoundedType of this TypeVariable
     */
    private BoundedType boundsOf(TypeVariable typeParameter) {
        if (typeParameter.getBounds()[0] instanceof TypeVariable) {
            return boundsOf((TypeVariable) typeParameter.getBounds()[0]);
        }
        return new TypeVarBoundedType(typeParameter);
    }

    /**
     * @param wildCard The WildCard type
     * @return A {@link BoundedType} for easy bound information, if first bound is a TypeVariable
     *         then retrieve BoundedType of this TypeVariable
     */
    private BoundedType boundsOf(WildcardType wildCard) {
        /*
         *  According to JLS(http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.5.1):
         *  - Lower and upper can't coexist: (for instance, this is not allowed: <? extends List<String> & super MyInterface>)
         *  - Multiple bounds are not supported (for instance, this is not allowed: <? extends List<String> & MyInterface>)
         */

        WildCardBoundedType wildCardBoundedType = new WildCardBoundedType(wildCard);
        if (wildCardBoundedType.firstBound() instanceof TypeVariable) {
            return boundsOf((TypeVariable) wildCardBoundedType.firstBound());
        }

        return wildCardBoundedType;
    }



    /**
     * @return Raw type of the current instance.
     */
    public abstract Class<?> rawType();



    /**
     * @return Returns extra interfaces if relevant, otherwise empty List.
     */
    public List<Type> extraInterfaces() {
        return Collections.emptyList();
    }



    /**
     * @return Actual type arguments matching the type variables of the raw type represented by this {@link MockitoGenericMetadata} instance.
     */
    public Map<TypeVariable, Type> actualTypeArguments() {
        TypeVariable[] typeParameters = rawType().getTypeParameters();
        LinkedHashMap<TypeVariable, Type> actualTypeArguments = new LinkedHashMap<TypeVariable, Type>();

        for (TypeVariable typeParameter : typeParameters) {

            Type actualType = getActualTypeArgumentFor(typeParameter);

            actualTypeArguments.put(typeParameter, actualType);
            // logger.log("For '" + rawType().getCanonicalName() + "' returning explicit TypeVariable : { '" + typeParameter + "(" + System.identityHashCode(typeParameter) + ")" + "' : '" + actualType +"' }");
        }

        return actualTypeArguments;
    }

    protected Type getActualTypeArgumentFor(TypeVariable typeParameter) {
        Type type = this.contextualActualTypeParameters.get(typeParameter);
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            return getActualTypeArgumentFor(typeVariable);
        }

        return type;
    }



    /**
     * Creates a mock using the Generics Metadata represented by this instance.
     *
     * @param answer The answer to use in mock settings.
     * @return The mock or null if not mockable.
     */
    public Object toMock(Answer answer) {
        return createMock(rawType(), ((MockSettingsImpl) withSettings().defaultAnswer(answer)).parameterizedInfo(this));
    }

    private Object createMock(Class<?> rawType, MockSettings mockSettings) {
        return Mockito.mock(rawType, mockSettings);
    }


    /**
     * Resolve current method generic return type to a {@link MockitoGenericMetadata}.
     *
     * @param method Method to resolve the return type.
     * @return {@link MockitoGenericMetadata} representing this generic return type.
     */
    public MockitoGenericMetadata resolveGenericReturnType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        // logger.log("Method '" + method.toGenericString() + "' has return type : " + genericReturnType.getClass().getInterfaces()[0].getSimpleName() + " : " + genericReturnType);

        if (genericReturnType instanceof Class) {
            return new NotGenericReturnType(genericReturnType);
        }
        if (genericReturnType instanceof ParameterizedType) {
            return new ParameterizedReturnType(this, method.getTypeParameters(), (ParameterizedType) method.getGenericReturnType());
        }
        if (genericReturnType instanceof TypeVariable) {
            return new TypeVariableReturnType(this, method.getTypeParameters(), (TypeVariable) genericReturnType);
        }

        throw new MockitoException("Ouch, it shouldn't happen, type '" + genericReturnType.getClass().getCanonicalName() + "' on method : '" + method.toGenericString() + "' is not supported : " + genericReturnType);
    }

    /**
     * Create an new MockitoGenericMetadata from a {@link Type}.
     *
     * <p>
     *     Supports only {@link Class} and {@link ParameterizedType}, otherwise throw a {@link MockitoException}.
     * </p>
     *
     * @param type The class from which the {@link MockitoGenericMetadata} should be built.
     * @return The new {@link MockitoGenericMetadata}.
     * @throws MockitoException Raised if type is not a {@link Class} or a {@link ParameterizedType}.
     */
    public static MockitoGenericMetadata from(Type type) {
        Checks.checkNotNull(type, "type");
        if (type instanceof Class) {
            return new FromClassMockitoGenericMetadata((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return new FromParameterizedTypeMockitoGenericMetadata((ParameterizedType) type);
        }

        throw new MockitoException("Type meta-data for this Type (" + type.getClass().getCanonicalName() + ") is not supported : " + type);
    }


    /**
     * Metadata for source {@link Class}
     */
    private static class FromClassMockitoGenericMetadata extends MockitoGenericMetadata {
        private Class<?> clazz;

        public FromClassMockitoGenericMetadata(Class<?> clazz) {
            this.clazz = clazz;
            readActualTypeParametersOnDeclaringClass();
        }

        private void readActualTypeParametersOnDeclaringClass() {
            registerTypeParametersOn(clazz.getTypeParameters());
            registerTypeVariablesOn(clazz.getGenericSuperclass());
            for (Type genericInterface : clazz.getGenericInterfaces()) {
                registerTypeVariablesOn(genericInterface);
            }
        }

        @Override
        public Class<?> rawType() {
            return clazz;
        }
    }


    /**
     * Metadata for source {@link ParameterizedType}.
     * Don't work with ParameterizedType returned in {@link Method#getGenericReturnType()}.
     */
    private static class FromParameterizedTypeMockitoGenericMetadata extends MockitoGenericMetadata {
        private ParameterizedType parameterizedType;

        public FromParameterizedTypeMockitoGenericMetadata(ParameterizedType parameterizedType) {
            this.parameterizedType = parameterizedType;
            readActualTypeParameters();
        }

        private void readActualTypeParameters() {
            registerTypeVariablesOn(parameterizedType.getRawType());
            registerTypeVariablesOn(parameterizedType);
        }

        @Override
        public Class<?> rawType() {
            return (Class<?>) parameterizedType.getRawType();
        }
    }


    /**
     * Metadata specific to {@link ParameterizedType} generic return types.
     */
    private static class ParameterizedReturnType extends MockitoGenericMetadata {
        private final ParameterizedType parameterizedType;
        private final TypeVariable[] typeParameters;

        public ParameterizedReturnType(MockitoGenericMetadata source, TypeVariable[] typeParameters, ParameterizedType parameterizedType) {
            this.parameterizedType = parameterizedType;
            this.typeParameters = typeParameters;
            this.contextualActualTypeParameters = source.contextualActualTypeParameters;

            readTypeParameters();
            readTypeVariables();
        }

        private void readTypeParameters() {
            registerTypeParametersOn(typeParameters);
        }

        private void readTypeVariables() {
            registerTypeVariablesOn(parameterizedType);
        }

        @Override
        public Class<?> rawType() {
            return (Class<?>) parameterizedType.getRawType();
        }

    }


    /**
     * Metadata specific to {@link TypeVariable} generic return type.
     */
    private static class TypeVariableReturnType extends MockitoGenericMetadata {
        private final TypeVariable typeVariable;
        private final TypeVariable[] typeParameters;
        private Class<?> rawType;



        public TypeVariableReturnType(MockitoGenericMetadata source, TypeVariable[] typeParameters, TypeVariable typeVariable) {
            this.typeParameters = typeParameters;
            this.typeVariable = typeVariable;
            this.contextualActualTypeParameters = source.contextualActualTypeParameters;

            readTypeParameters();
            readTypeVariables();
        }

        private void readTypeParameters() {
            registerTypeParametersOn(typeParameters);
        }

        private void readTypeVariables() {
            for (Type type : typeVariable.getBounds()) {
                registerTypeVariablesOn(type);
            }
            registerTypeVariablesOn(getActualTypeArgumentFor(typeVariable));
        }

        @Override
        public Class<?> rawType() {
            if (rawType == null) {
                rawType = extractRawTypeOf(typeVariable);
            }
            return rawType;
        }

        private Class<?> extractRawTypeOf(Type type) {
            if (type instanceof Class) {
                return (Class<?>) type;
            }
            if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            }
            if (type instanceof BoundedType) {
                return extractRawTypeOf(((BoundedType) type).firstBound());
            }
            if (type instanceof TypeVariable) {
                /*
                 * If type is a TypeVariable, then it is needed to gather data elsewhere. Usually TypeVariables are declared
                 * on the class definition, such as such as List<E>.
                 */
                return extractRawTypeOf(contextualActualTypeParameters.get(type));
            }
            throw new MockitoException("Raw extraction not supported for : '" + type + "'");
        }

        @Override
        public List<Type> extraInterfaces() {
            Type type = extractActualBoundedTypeOf(typeVariable);
            if (type instanceof BoundedType) {
                return Arrays.asList(((BoundedType) type).interfaceBounds());
            }
            if (type instanceof ParameterizedType) {
                return Collections.singletonList(type);
            }
            if (type instanceof Class) {
                return Collections.emptyList();
            }
            throw new MockitoException("Cannot extract extra-interfaces from '" + typeVariable + "' : '" + type + "'");
        }

        private Class<?>[] rawExtraInterfaces() {
            List<Type> extraInterfaces = extraInterfaces();
            List<Class<?>> rawExtraInterfaces = new ArrayList<Class<?>>();
            for (Type extraInterface : extraInterfaces) {
                Class<?> rawInterface = extractRawTypeOf(extraInterface);
                // avoid interface collision with actual raw type (with typevariables, resolution ca be quite aggressive)
                if(!rawType().equals(rawInterface)) {
                    rawExtraInterfaces.add(rawInterface);
                }
            }
            return rawExtraInterfaces.toArray(new Class[rawExtraInterfaces.size()]);
        }

        private Type extractActualBoundedTypeOf(Type type) {
            if (type instanceof TypeVariable) {
                /*
                If type is a TypeVariable, then it is needed to gather data elsewhere. Usually TypeVariables are declared
                on the class definition, such as such as List<E>.
                */
                return extractActualBoundedTypeOf(contextualActualTypeParameters.get(type));
            }
            if (type instanceof BoundedType) {
                Type actualFirstBound = extractActualBoundedTypeOf(((BoundedType) type).firstBound());
                if (!(actualFirstBound instanceof BoundedType)) {
                    return type; // avoid going one step further, ie avoid : O(TypeVar) -> K(TypeVar) -> Some ParamType
                }
                return actualFirstBound;
            }
            return type; // irrelevant, we don't manage other types.
        }

        public Object toMock(Answer answer) {
            Class<?>[] rawExtraInterfaces = rawExtraInterfaces();
            if (rawExtraInterfaces.length <= 0) {
                return super.toMock(answer);
            }

            return super.createMock(
                    rawType(),
                    ((MockSettingsImpl) withSettings()
                            .defaultAnswer(answer)
                            .extraInterfaces(rawExtraInterfaces))
                            .parameterizedInfo(this)
            );
        }
    }



    /**
     * Metadata specific to {@link Class} return type.
     */
    private static class NotGenericReturnType extends MockitoGenericMetadata {
        private final Class<?> returnType;

        public NotGenericReturnType(Type genericReturnType) {
            returnType = (Class<?>) genericReturnType;
        }

        @Override
        public Class<?> rawType() {
            return returnType;
        }
    }



    /**
     * Type representing bounds of a type
     *
     * @see TypeVarBoundedType
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4">http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4</a>
     * @see WildCardBoundedType
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.5.1">http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.5.1</a>
     */
    public static interface BoundedType extends Type {
        Type firstBound();

        Type[] interfaceBounds();
    }

    /**
     * Type representing bounds of a type variable, allows to keep all bounds information.
     *
     * <p>It uses the first bound in the array, as this array is never null and always contains at least
     * one element (Object is always here if no bounds are declared).</p>
     *
     * <p>If upper bounds are declared with SomeClass and additional interfaces, then firstBound will be SomeClass and
     * interfacesBound will be an array of the additional interfaces.
     *
     * i.e. <code>SomeClass</code>.
     * <pre class="code"><code class="java">
     *     interface UpperBoundedTypeWithClass<E extends Comparable<E> & Cloneable> {
     *         E get();
     *     }
     *     // will return Comparable type
     * </code></pre>
     * </p>
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4">http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4</a>
     */
    public static class TypeVarBoundedType implements BoundedType {
        private TypeVariable typeVariable;


        public TypeVarBoundedType(TypeVariable typeVariable) {
            this.typeVariable = typeVariable;
        }

        /**
         * @return either a class or an interface (parameterized or not), if no bounds declared Object is returned.
         */
        public Type firstBound() {
            return typeVariable.getBounds()[0]; //
        }

        /**
         * On a Type Variable (typeVar extends C_0 & I_1 & I_2 & etc), will return an array
         * containing I_1 and I_2.
         *
         * @return other bounds for this type, these bounds can only be only interfaces as the JLS says,
         * empty array if no other bound declared.
         */
        public Type[] interfaceBounds() {
            Type[] interfaceBounds = new Type[typeVariable.getBounds().length - 1];
            System.arraycopy(typeVariable.getBounds(), 1, interfaceBounds, 0, typeVariable.getBounds().length - 1);
            return interfaceBounds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            return typeVariable.equals(((TypeVarBoundedType) o).typeVariable);

        }

        @Override
        public int hashCode() {
            return typeVariable.hashCode();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{firstBound=").append(firstBound());
            sb.append(", interfaceBounds=").append(Arrays.deepToString(interfaceBounds()));
            sb.append('}');
            return sb.toString();
        }

        public TypeVariable typeVariable() {
            return typeVariable;
        }
    }

    /**
     * Type representing bounds of a wildcard, allows to keep all bounds information.
     *
     * <p>The JLS says that lower bound and upper bound are mutually exclusive, and that multiple bounds
     * are not allowed.
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4">http://docs.oracle.com/javase/specs/jls/se5.0/html/typesValues.html#4.4</a>
     */
    public static class WildCardBoundedType implements BoundedType {
        private WildcardType wildcard;


        public WildCardBoundedType(WildcardType wildcard) {
            this.wildcard = wildcard;
        }

        /**
         * @return The first bound, either a type or a reference to a TypeVariable
         */
        public Type firstBound() {
            Type[] lowerBounds = wildcard.getLowerBounds();
            Type[] upperBounds = wildcard.getUpperBounds();

            return lowerBounds.length != 0 ? lowerBounds[0] : upperBounds[0];
        }

        /**
         * @return An empty array as, wildcard don't support multiple bounds.
         */
        public Type[] interfaceBounds() {
            return new Type[0];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            return wildcard.equals(((TypeVarBoundedType) o).typeVariable);

        }

        @Override
        public int hashCode() {
            return wildcard.hashCode();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{firstBound=").append(firstBound());
            sb.append(", interfaceBounds=[]}");
            return sb.toString();
        }

        public WildcardType wildCard() {
            return wildcard;
        }
    }

}


