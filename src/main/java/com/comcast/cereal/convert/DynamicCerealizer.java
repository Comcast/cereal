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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.CerealFactoryAware;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * The <i>DynamicCerealizer</i> cerealizes an object using the object type at runtime. This is
 * primarily used when Cerealizing non-typed {@link List}s. This will attempt to guess the type when
 * deserializing by looking for an encoded <code>--class</code> type on the {@link Map}. This is not
 * always guarenteed to be there however as the type may not be encode as a Map.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class DynamicCerealizer implements Cerealizer<Object, Object>, CerealFactoryAware {

    private CerealFactory cerealFactory;

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
     * @see com.comcast.cvs.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object cerealize(Object object, ObjectCache objectCache) throws CerealException {
        if (null == object) {
            return null;
        }

        /* First check to see if this object has already been created */
        Object cereal = objectCache.getReferenceCereal(object);
        if (null != cereal) {
            return cereal;
        }

        Class<?> type = object.getClass();

        if (Map.class.isAssignableFrom(type)) {
            MapCerealizer cerealizer = new MapCerealizer();
            cerealizer.setCerealFactory(cerealFactory);
            return cerealizer.cerealize((Map<?, ?>) object, objectCache);
        } else if (Collection.class.isAssignableFrom(type)) {
        	CollectionCerealizer cerealizer = new CollectionCerealizer(this, (Class<? extends Collection>) type);
        	cerealizer.setCerealFactory(cerealFactory);
            return cerealizer.cerealize((Collection) object, objectCache);
        }
        
        Cerealizer cerealizer = cerealFactory.getCerealizer(type);
        cereal = cerealizer.cerealize(object, objectCache);
        boolean includeClassName = objectCache.getSettings().shouldIncludeClassName();
        if (includeClassName && (cereal instanceof Map)) {
            Map<String, Object> map = (Map<String, Object>) cereal;
            map.put("--class", type.getName());
            
            objectCache.cache(object, map);
        }
        
        return cereal;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object deCerealize(Object cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        }

        if (cereal instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) cereal;
            if (map.containsKey("--class")) {
                try {
                    String typeName = (String) map.get("--class");

                    Class<?> type = Class.forName(typeName);
                    Cerealizer cerealizer = cerealFactory.getCerealizer(type);
                    return cerealizer.deCerealize(cereal, objectCache);
                } catch (Exception ex) {
                    throw new CerealException("Failed to decerealize dynamically", ex);
                }
            } else {
                /* This might be a reference to an existing object */
                Object object = objectCache.getReferenceObject(map);
                if (null != object) {
                    return object;
                }
            }

        } else if (cereal instanceof List) {
        	List list = (List) cereal;
        	List<Object> rv = new ArrayList<Object>();
        	for (Object cerealItem : list) {
        		rv.add(deCerealize(cerealItem, objectCache));
        	}
        	return rv;
        }

        /* Hopefully this was just a simple extraction, it might fail */
        return cereal;
    }
}
