/*-
 * #%L
 * rapidoid-rest
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.rapidoid.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Arr;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class Reflect extends Cls {

    private static final Object[] EMPTY_ARRAY = {};

    public static void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            for (Class<?> c = instance.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
                try {
                    Field field = c.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(instance, value);
                    return;
                } catch (NoSuchFieldException e) {
                    // keep searching the filed in the super-class...
                }
            }
        } catch (Exception e) {
            throw U.rte("Cannot set field value!", e);
        }

        throw U.rte("Cannot find the field '%s' in the class '%s'", fieldName, instance.getClass());
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw U.rte("Cannot set field value!", e);
        }
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> allFields = U.list();

        try {
            for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    allFields.add(field);
                }
            }

        } catch (Exception e) {
            throw U.rte("Cannot get fields!", e);
        }

        return allFields;
    }

    public static List<Field> getFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> annotatedFields = U.list();

        try {
            for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(annotation)) {
                        annotatedFields.add(field);
                    }
                }
            }

        } catch (Exception e) {
            throw U.rte("Cannot get annotated fields!", e);
        }

        return annotatedFields;
    }

    public static Map<String, Class<?>> classMap(Iterable<Class<?>> classes) {
        Map<String, Class<?>> map = new LinkedHashMap<>();

        for (Class<?> cls : classes) {
            map.put(cls.getSimpleName(), cls);
        }

        return map;
    }

    public static Class<?>[] typesOf(Object[] args) {
        Class<?>[] types = new Class<?>[args.length];

        for (int i = 0; i < types.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : null;
        }

        return types;
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        ClassPool cp = new ClassPool();
        cp.insertClassPath(new ClassClassPath(clazz));

        CtClass cc;
        try {
            cc = cp.get(clazz.getName());
        } catch (NotFoundException e) {
            throw U.rte("Cannot find the target class!", e);
        }

        List<Method> methods = U.list();

        for (CtMethod m : cc.getDeclaredMethods()) {
            try {
                methods.add(getMethod(clazz, m.getName(), ctTypes(m.getParameterTypes())));
            } catch (Exception e) {
                throw U.rte(e);
            }
        }

        return methods;
    }

    private static Class<?>[] ctTypes(CtClass[] types) {
        Class<?>[] classes = new Class[types.length];

        for (int i = 0; i < classes.length; i++) {
            classes[i] = Cls.get(types[i].getName());
        }

        return classes;
    }

    public static String[] getMethodParameterNames(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        String[] names = new String[paramTypes.length];

        boolean defaultNames = true;
        Method getParameters = Cls.findMethod(method.getClass(), "getParameters");

        if (getParameters != null) {
            Object[] parameters = Cls.invoke(getParameters, method);

            for (int i = 0; i < parameters.length; i++) {
                names[i] = Beany.getPropValue(parameters[i], "name");
                U.notNull(names[i], "parameter name");
                if (!names[i].equals("arg" + i)) {
                    defaultNames = false;
                }
            }
        }

        if (defaultNames) {
            boolean useIndexMapping;
            CtMethod cm;

            try {
                ClassPool cp = new ClassPool();
                cp.insertClassPath(new ClassClassPath(method.getDeclaringClass()));
                CtClass cc = cp.get(method.getDeclaringClass().getName());

                useIndexMapping = cc.getClassFile().getMajorVersion() >= 52;

                CtClass[] params = new CtClass[paramTypes.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = cp.get(method.getParameterTypes()[i].getName());
                }

                cm = cc.getDeclaredMethod(method.getName(), params);

            } catch (NotFoundException e) {
                throw U.rte("Cannot find the target method!", e);
            }

            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

            if (codeAttribute != null) {
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                        .getAttribute(LocalVariableAttribute.tag);

                int offset = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

                for (int i = 0; i < names.length; i++) {
                    names[i] = null;
                }

                for (int i = 0; i < attr.tableLength(); i++) {
                    int index = i - offset;

                    if (useIndexMapping) {
                        index = attr.index(index);
                    }

                    String var = attr.variableName(i);

                    if (index >= 0 && index < names.length && !"this".equals(var)) {
                        names[index] = var;
                    }
                }

                if (!validNames(names)) {
                    for (int i = 0; i < names.length; i++) {
                        names[i] = null;
                    }

                    for (int i = 0; i < attr.tableLength(); i++) {
                        int index = i - offset;
                        String var = attr.variableName(i);

                        if (index >= 0 && index < names.length && !"this".equals(var)) {
                            names[index] = var;
                        }
                    }
                }
            }

            U.must(validNames(names), "Couldn't retrieve the parameter names! Please report this problem. " +
                    "You can explicitly specify the names using @Param(\"thename\"), " +
                    "or configure the option '-parameters' on the Java 8 compiler.");
        }

        return names;
    }

    public static String[] getLambdaParameterNames(Serializable lambda) {
        Method lambdaMethod = getLambdaMethod(lambda);
        Class<?>[] lambdaTypes = lambdaMethod.getParameterTypes();
        String[] names = getMethodParameterNames(lambdaMethod);

        List<Method> methods = U.list();

        for (Class<?> interf : lambda.getClass().getInterfaces()) {
            for (Method m : interf.getMethods()) {
                Class<?>[] types = m.getParameterTypes();

                if (types.length <= names.length) {
                    int diff = names.length - types.length;
                    boolean matching = true;

                    for (int i = 0; i < types.length; i++) {
                        if (!types[i].isAssignableFrom(lambdaTypes[i + diff])) {
                            matching = false;
                        }
                    }

                    if (matching) {
                        methods.add(m);
                    }
                }
            }
        }

        U.must(methods.size() > 0, "Cannot find the lambda target method of the functional interface!");
        U.must(methods.size() == 1, "Found more than one lambda target method of the functional interface: " + methods);

        return Arr.sub(names, names.length - methods.get(0).getParameterTypes().length, names.length);
    }

    public static Method getLambdaMethod(Serializable lambda) {
        return getLambdaMethod(lambda, "execute");
    }

    public static Method getLambdaMethod(Serializable lambda, String functionalMethodName) {
        Method writeReplace = Cls.findMethod(lambda.getClass(), "writeReplace");

        if (writeReplace == null) {
            List<Method> methods = getMethodsNamed(lambda.getClass(), functionalMethodName);

            U.must(U.notEmpty(methods), "Cannot find the lambda method named: %s", functionalMethodName);

            for (Method method : methods) {
                Class<?>[] paramTypes = method.getParameterTypes();
                for (Class<?> paramType : paramTypes) {
                    if (!paramType.getName().equals("java.lang.Object")) {
                        return method;
                    }
                }
            }

            U.must(methods.size() == 1, "Expected one, but found %s lambda methods named: %s", methods.size(), functionalMethodName);

            return methods.get(0);
        }

        Object serializedLambda = Cls.invoke(writeReplace, lambda);

        Method getImplClass = Cls.findMethod(serializedLambda.getClass(), "getImplClass");

        if (getImplClass != null) {
            String implClass = Cls.invoke(getImplClass, serializedLambda);
            String className = implClass.replaceAll("/", ".");

            Class<?> cls;
            try {
                cls = Class.forName(className, true, lambda.getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                throw U.rte("Cannot find or load the lambda class: %s", className);
            }

            Method getImplMethodName = Cls.findMethod(serializedLambda.getClass(), "getImplMethodName");
            String lambdaMethodName = Cls.invoke(getImplMethodName, serializedLambda);

            for (Method method : cls.getDeclaredMethods()) {
                if (method.getName().equals(lambdaMethodName)) {
                    return method;
                }
            }

            throw U.rte("Cannot find the lambda method: %s#%s", cls.getName(), lambdaMethodName);
        } else {
            throw U.rte("Cannot find the 'getImplClass' method of the serialized lambda!");
        }
    }

    private static boolean validNames(String[] names) {
        for (String name : names) {
            if (name == null) return false;
        }

        return true;
    }

    public static Object invokeStatic(String className, String methodName, Object... args) {
        Class<?> cls = Cls.get(className);
        Method method = findMethodByArgs(cls, methodName, args);
        return Cls.invokeStatic(method, args);
    }

    public static boolean annotatedMethod(Object instance, String methodName, Class<Annotation> annotation) {
        try {
            Method method = instance.getClass().getMethod(methodName);
            return method.getAnnotation(annotation) != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) {
        try {
            return clazz.getConstructor(paramTypes);
        } catch (Exception e) {
            throw U.rte("Cannot find the constructor for %s with param types: %s", e, clazz,
                    U.str(paramTypes));
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            throw U.rte("Cannot find field: %s", e, name);
        } catch (SecurityException e) {
            throw U.rte("Cannot access field: %s", e, name);
        }
    }

    public static List<Method> getMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> annotatedMethods = U.list();

        try {
            for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
                Method[] methods = c.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(annotation)) {
                        annotatedMethods.add(method);
                    }
                }
            }

        } catch (Exception e) {
            throw U.rte("Cannot instantiate class!", e);
        }

        return annotatedMethods;
    }

}
