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

import java.lang.reflect.Field;
import java.util.Map;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * Provides CerealAccess for object {@link Field}s.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FieldAccess extends CerealAccess {

    private Field field;
    private CerealFactory cerealFactory;
    private boolean cerealizerOverride = false;

    /**
     * Construct a new FieldAccess object that targets the given field and uses the given cerealizer
     * to convert.
     * 
     * @param field
     *            the field to read from and write to
     * @param cerealizer
     *            the cerealizer to use when converting
     * @param requireValue
     *            <code>true</code> if a value is required for this field
     * @param defaultValue
     *            if not <code>null</code>, the JSON encoded default value to use when no value is
     *            found when de-cerealizing
     * @param cerealFactory
     *            The factory used to look up runtime cerealizers
     * @param cerealizerOverride
     *            <code>true</code> if  should use the given cerealizer no matter what. False if
     *            it can choose to use the runtime cerealizer
     */
    public FieldAccess(Field field, Cerealizer cerealizer, boolean requireValue, String defaultValue, 
            Class<?> defaultObjectClass, CerealFactory cerealFactory, boolean cerealizerOverride) {
        super(cerealizer, requireValue, defaultValue, defaultObjectClass);
        this.field = field;
        this.cerealFactory = cerealFactory;
        this.cerealizerOverride = cerealizerOverride;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.impl.CerealAccess#applyCereal(java.lang.Object, java.lang.Object)
     */
    public void applyCereal(Object cereal, Object target, ObjectCache objectCache) throws CerealException {
        try {
            Cerealizer cz = cerealizer;
            if (!cerealizerOverride) {
                cz = cerealFactory.getRuntimeCerealizer(cereal, cerealizer);
            }
            Object value = cz.deCerealize(cereal, objectCache);
            value = ReflectionHelper.convert(value, getJavaType());
            field.setAccessible(true);
            field.set(target, value);
        } catch (Throwable throwable) {
            throw new CerealException("Failed to set the value of '" + field.toString() + "'",
                    throwable);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.impl.CerealAccess#getCereal(java.lang.Object)
     */
    public Object getCereal(Object source, ObjectCache objectCache) throws CerealException {
        try {
            field.setAccessible(true);
            Object value = field.get(source);
            Cerealizer cz = cerealizer;
            Class<?> fieldClass = value == null ? field.getDeclaringClass() : value.getClass();
            if (!cerealizerOverride && (value != null)) {
                cz = cerealFactory.getCerealizer(fieldClass);
            }
            Object cereal = cz.cerealize(value, objectCache);
            if (objectCache.getSettings().shouldIncludeClassName() && (cereal instanceof Map)) {
                ((Map) cereal).put("--class", fieldClass.getName());
            }
            return cereal;
        } catch (Throwable throwable) {
            throw new CerealException("Failed to get the value from '" + field.toString() + "'",
                    throwable);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.impl.CerealAccess#getJavaType()
     */
    public Class getJavaType() {
        return field.getType();
    }
}
