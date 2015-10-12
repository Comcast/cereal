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
package com.comcast.cvs.cereal.convert;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConstructorUtils;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealFactory;
import com.comcast.cvs.cereal.CerealFactoryAware;
import com.comcast.cvs.cereal.Cerealizer;
import com.comcast.cvs.cereal.ObjectCache;

@SuppressWarnings("rawtypes")
public class MapCerealizer implements Cerealizer<Map<?, ?>, Map<?, ?>>, CerealFactoryAware {

    private CerealFactory cerealFactory;
	private Cerealizer cerealizer;
	private Class<? extends Map> mapClass = HashMap.class;
    
    public MapCerealizer() {
    	this(null);
    }
    
    public MapCerealizer(Cerealizer cerealizer) {
    	this.cerealizer = cerealizer;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.comcast.cvs.cereal.CerealFactoryAware#setCerealFactory(com.comcast.cvs.cereal.CerealFactory
     * )
     */
    public void setCerealFactory(CerealFactory cerealFactory) {
        this.cerealFactory = cerealFactory;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings({"unchecked" })
    public Map<?, ?> deCerealize(Map<?, ?> cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        }

        DynamicCerealizer dc = cerealFactory.getCachedCerealizer(DynamicCerealizer.class);
        Cerealizer cerealizer = this.cerealizer == null ? dc : this.cerealizer;
        Map object;
        try {
            object = HashMap.class.equals(mapClass) || !isInstantiable(mapClass) ? new HashMap(cereal.size()) : mapClass.newInstance();
        } catch (Exception e) {
            throw new CerealException("Failed to instantiate map class " + mapClass.getName(), e);
        }

        for (Entry<?, ?> entry : cereal.entrySet()) {
            Object key = dc.deCerealize(entry.getKey(), objectCache);
            Object value = cerealizer.deCerealize(entry.getValue(), objectCache);

            if (!key.equals("--class")) {
                object.put(key, value);
            }
        }

        return object;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    @SuppressWarnings({ "unchecked" })
    public Map<?, ?> cerealize(Map<?, ?> object, ObjectCache objectCache) throws CerealException {
        if (null == object) {
            return null;
        }
        
        DynamicCerealizer dc = cerealFactory.getCachedCerealizer(DynamicCerealizer.class);
        Cerealizer cerealizer = this.cerealizer == null ? dc : this.cerealizer;
        Map cereal = new HashMap(object.size());

        for (Entry<?, ?> entry : object.entrySet()) {
            Object key = dc.cerealize(entry.getKey(), objectCache);
            Object value = cerealizer.cerealize(entry.getValue(), objectCache);

            cereal.put(key, value);
        }

        return cereal;
    }
    
    private static boolean isInstantiable(Class<?> clazz) {
        boolean abs = Modifier.isAbstract(clazz.getModifiers());
        boolean hasConstuctor = ConstructorUtils.getAccessibleConstructor(clazz, new Class[0]) != null;
        return !clazz.isInterface() && !abs && hasConstuctor;
    }

    public Class<? extends Map> getMapClass() {
        return mapClass;
    }

    public void setMapClass(Class<? extends Map> mapClass) {
        this.mapClass = mapClass;
    }
}
