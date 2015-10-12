/**
 * Copyright 2012 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.cvs.cereal.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

/**
 * Simple utility class for ensuring that if a type read by a CerealEngine is too generic, it will
 * be appropriately converted to the ultimate type required by the class where it will be set.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class ReflectionHelper {

    private static final Map<Class<?>, Class<?>> map;
    static {
        map = new HashMap<Class<?>, Class<?>>();
        map.put(Boolean.class, boolean.class);
        map.put(Byte.class, byte.class);
        map.put(Character.class, char.class);
        map.put(Short.class, short.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Float.class, float.class);
        map.put(Double.class, double.class);
    }

    /**
     * Get the given class in a primitive form or return the given class if it has no primitive
     * equivalent.
     * 
     * @param clazz
     *            the class to look up
     * 
     * @return the clazz in primitive form or the given clazz if it has no primitive form
     */
    private static Class<?> asPrimitive(Class<?> clazz) {
        Class<?> primative = map.get(clazz);
        return (primative != null) ? primative : clazz;
    }

    /**
     * Convert the given object to be castable to the given <code>goal</code> type.
     * 
     * @param obj
     *            the object to convert
     * @param goal
     *            the goal type
     * 
     * @return the converted object
     */
    public static Object convert(Object obj, Class<?> goal) {
        if (null == obj) {
            return obj;
        } else {
            if (goal.isInstance(obj)) {
                return obj;
            } else if (asPrimitive(goal).equals(asPrimitive(obj.getClass()))) {
                return obj;
            } else {
                Converter converter = ConvertUtils.lookup(goal);
                return (null != converter) ? converter.convert(goal, obj) : obj;
            }
        }
    }

    public static Class<?> getGenericClass(Type type) {
        return getGenericClass(type, 0);
    }
    
    public static Class<?> getGenericClass(Type type, int arg) {
        if ((null == type) || !ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return null;
        }

        try {
            ParameterizedType paramType = (ParameterizedType) type;
            return (Class<?>) paramType.getActualTypeArguments()[arg];
        } catch (Throwable throwable) {
            return null;
        }
    }
}
