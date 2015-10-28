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
package com.comcast.cereal.impl;

import java.lang.reflect.Method;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * Provides CerealAccess for object {@link Method}s.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MethodAccess extends CerealAccess {

    private Method setter = null;
    private Method getter = null;

    /**
     * Construct a new MethodAccess object that uses the given cerealizer to convert.
     * 
     * @param cerealizer
     *            the cerealizer to use when converting
     */
    public MethodAccess(Cerealizer cerealizer) {
        super(cerealizer, true, null, null);
    }

    /**
     * Set the given method as the getter method to use when reading.
     * 
     * @param getter
     *            the getter method
     */
    public void setGetter(Method getter) {
        this.getter = getter;
    }

    /**
     * Set the given method as the setter method to use when writing.
     * 
     * @param setter
     *            the setter method
     */
    public void setSetter(Method setter) {
        this.setter = setter;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.impl.CerealAccess#applyCereal(java.lang.Object, java.lang.Object)
     */
    public void applyCereal(Object cereal, Object target, ObjectCache objectCache) throws CerealException {
        if (null == setter) {
            throw new CerealException("No setter method was associated with this value");
        }

        try {
            Object value = cerealizer.deCerealize(cereal, objectCache);
            value = ReflectionHelper.convert(value, getJavaType());
            setter.setAccessible(true);
            setter.invoke(target, value);
        } catch (Throwable throwable) {
            throw new CerealException("Failed to invoke the setter method '" + setter.toString()
                    + "'", throwable);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.impl.CerealAccess#getCereal(java.lang.Object)
     */
    public Object getCereal(Object source, ObjectCache objectCache) throws CerealException {
        if (null == getter) {
            throw new CerealException("No getter method was associated with this value");
        }

        try {
            getter.setAccessible(true);
            Object value = getter.invoke(source);
            return cerealizer.cerealize(value, objectCache);
        } catch (Throwable throwable) {
            throw new CerealException("Failed to invoke the getter method '" + getter.toString()
                    + "'", throwable);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.impl.CerealAccess#getJavaType()
     */
    public Class getJavaType() throws CerealException {
        if (null == setter) {
            throw new CerealException("No setter method was associated with this value");
        } else {
            return setter.getParameterTypes()[0];
        }
    }
}
