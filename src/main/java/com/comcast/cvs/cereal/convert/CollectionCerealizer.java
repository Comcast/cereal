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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealFactory;
import com.comcast.cvs.cereal.CerealFactoryAware;
import com.comcast.cvs.cereal.Cerealizer;
import com.comcast.cvs.cereal.ObjectCache;

/**
 * Simple wrapper to turn any {@link Cerealizer} into a collection converter. This will apply the internal
 * {@link Cerealizer} on each element in the collection.
 * 
 * @author Clark Malmgren
 */
@SuppressWarnings("rawtypes")
public class CollectionCerealizer implements Cerealizer<Collection, Collection>, CerealFactoryAware {
	public static Map<Class<? extends Collection>, Class<? extends Collection>> INSTANCE_CLASSES 
		= new HashMap<Class<? extends Collection>, Class<? extends Collection>>();
	static {
        INSTANCE_CLASSES.put(Collection.class, ArrayList.class);
        INSTANCE_CLASSES.put(List.class, ArrayList.class);
		INSTANCE_CLASSES.put(Set.class, HashSet.class);
		INSTANCE_CLASSES.put(Queue.class, LinkedList.class);
	}

    private Cerealizer cerealizer;
    private boolean cerealizerOverride = false;
    private Class<? extends Collection> collClass;
    private CerealFactory cerealFactory;

    /**
     * Create a new {@link CollectionCerealizer} for a {@link Collection}
     * 
     * @param converter
     *            the converter to apply on each element of the collection
     */
    public CollectionCerealizer(Cerealizer cerealizer, Class<? extends Collection> collClass) {
        this(cerealizer, false, collClass);
    }
    
    /**
     * Creates a CollectionCerealizer
     * @param cerealizer The default cerealizer to use
     * @param cerealizerOverride True if the given cerealizer should be used no matter what. False if the 
     *                          cerealizer is only used if the individual elements do not give a --class
     * @param collClass The class that should be used as the instance of the collection (eg. ArrayList)
     */
    public CollectionCerealizer(Cerealizer cerealizer, boolean cerealizerOverride, Class<? extends Collection> collClass) {
        this.cerealizer = cerealizer;
        this.cerealizerOverride = cerealizerOverride;
        this.collClass = collClass;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Collection deCerealize(Collection cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        }

		Class<? extends Collection> subclass = INSTANCE_CLASSES.containsKey(collClass) ? INSTANCE_CLASSES.get(collClass) : collClass;
        Collection coll;
		try {
			coll = subclass.newInstance();
		} catch (Exception e) {
			throw new CerealException("Could not instantiate a collection subclass for '" + subclass.getName() + "'", e);
		} 
        for (Object obj : (Collection) cereal) {
            Cerealizer cz = cerealizer;
            if (!cerealizerOverride) {
                cz = cerealFactory.getRuntimeCerealizer(obj, cerealizer);
            }
            coll.add(cz.deCerealize(obj, objectCache));
        }
        return coll;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Collection cerealize(Collection object, ObjectCache objectCache) throws CerealException {
        if (null == object) {
            return null;
        }

        Collection coll = new ArrayList();
        for (Object obj : (Collection) object) {
            
            Cerealizer cz = cerealizerOverride ? cerealizer : cerealFactory.getCerealizer(obj.getClass());
            boolean includeClassName = objectCache.getSettings().shouldIncludeClassName();
            Object cereal = cz.cerealize(obj, objectCache);
            if (includeClassName && (cereal instanceof Map)) {
                ((Map) cereal).put("--class", obj.getClass().getName());
            }
            coll.add(cereal);
        }
        return coll;
    }

    public void setCerealFactory(CerealFactory cerealFactory) {
        this.cerealFactory = cerealFactory;
    }

}
