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
package com.comcast.cereal.convert;

import java.lang.reflect.Method;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * Class for converting back and forth from Enums.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class EnumCerealizer<T extends Enum<T>> implements Cerealizer<Enum<T>, String> {

    private Class<Enum<T>> type;

    /**
     * Construct a new EnumCerealizer for a given type.
     * 
     * @param type
     *            the enum type
     */
    public EnumCerealizer(Class<Enum<T>> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Enum<T> deCerealize(String cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        }
        
        try {
            Method m = type.getMethod("valueOf", String.class);
            return (Enum<T>) m.invoke(null, cereal);
        } catch (Exception ex) {
            throw new CerealException("Failed to deCerealize an Enum", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public String cerealize(Enum<T> object, ObjectCache objectCache) throws CerealException {
        return (null == object) ? null : object.name();
    }

}
