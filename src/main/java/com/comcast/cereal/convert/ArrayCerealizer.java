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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * Simple wrapper to turn any {@link Cerealizer} into a array converter. This will apply the
 * internal {@link Cerealizer} on each element in the array.
 * 
 * @author Clark Malmgren
 * @param <T>
 *            the array type
 */
@SuppressWarnings("rawtypes")
public class ArrayCerealizer<T> implements Cerealizer<T[], List<?>> {

    private Cerealizer cerealizer;
    private Class<?> arrayType;

    /**
     * Create a new {@link ArrayCerealizer} for an array of objects.
     * 
     * @param cerealizer
     *            the converter to apply on each element of the array
     * @param arrayType
     *            the type of array
     */
    public ArrayCerealizer(Cerealizer cerealizer, Class<T> arrayType) {
        this.cerealizer = cerealizer;
        this.arrayType = arrayType;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public T[] deCerealize(List<?> cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        }

        T[] array = (T[]) Array.newInstance(arrayType, cereal.size());
        for (int i = 0; i < array.length; i++) {
            array[i] = (T) cerealizer.deCerealize(cereal.get(i), objectCache);
        }
        return array;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<?> cerealize(T[] array, ObjectCache objectCache) throws CerealException {
        if (null == array) {
            return null;
        }

        List list = new ArrayList();
        for (int i = 0; i < array.length; i++) {
            list.add(cerealizer.cerealize(array[i], objectCache));
        }
        return list;
    }

}
