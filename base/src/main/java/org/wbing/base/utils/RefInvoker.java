package org.wbing.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 反射调用辅助类
 * Created by dolphinWang on 15-2-4.
 */
public class RefInvoker {

    static class SingletonHolder {
        final static RefInvoker invoker = new RefInvoker();
    }

    private RefInvoker() {
    }

    public static RefInvoker getInstance() {
        return SingletonHolder.invoker;
    }

    public Object invokeMethod(String methodName,
                               Object instance, Class[] paramsType, Object[] paramsValues)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = findMethod(instance, methodName, paramsType);

        return method.invoke(instance, paramsValues);
    }


    public Object invokeSuperMethod(String methodName, Object instance, Class[] paramsType, Object[] paramsValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = instance.getClass();
        Method method = clazz.getSuperclass().getDeclaredMethod(methodName, paramsType);
        method.setAccessible(true);

        return method.invoke(instance, paramsValues);
    }


    /**
     * 反射调用静态函数
     *
     * @param className
     * @param methodName
     * @param paramsTypes
     * @param paramsValues
     * @return
     */
    public Object invokeStaticMethod(String className, String methodName,
                                     Class[] paramsTypes, Object[] paramsValues)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Class clazz = Class.forName(className);
        Method method = findStaticMethod(clazz, methodName, paramsTypes);

        return method.invoke(null, paramsValues);
    }


    public Object getField(Object instance, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        Field field = findField(instance, fieldName);

        return field.get(instance);
    }

    public Field[] getAllField(String className) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        Field[] fields = clazz.getDeclaredFields();

        return fields;
    }

    public Object getStaticField(String className, String fieldName)
            throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

        Class clazz = Class.forName(className);
        Field field = findStaticField(clazz, fieldName);
        return field.get(null);

    }

    public void setField(Object instance, String fieldName, Object fieldValue)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        Field field = findField(instance, fieldName);

        field.set(instance, fieldValue);
    }

    public void setFieldForClass(Object instance, String fieldName, Class cls, Object fieldValue)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        try {
            Field field = cls.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(instance, fieldValue);

        } catch (NoSuchFieldException e) {
            // ignore and search next
        }

    }

    public void setStaticField(String className, String fieldName, Object fieldValue)
            throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

        Class clazz = Class.forName(className);
        Field field = findField(clazz, fieldName);

        field.set(null, fieldValue);
    }


    public Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);


                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    public Field findStaticField(Class klass, String name) throws NoSuchFieldException {
        for (Class<?> clazz = klass; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);


                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + klass);
    }

    public Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);


                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
                e.printStackTrace();
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " +
                Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    public Method findStaticMethod(Class klass, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = klass; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);


                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
                e.printStackTrace();
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " +
                Arrays.asList(parameterTypes) + " not found in " + klass);
    }
}
