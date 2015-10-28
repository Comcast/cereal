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
package com.comcast.cereal;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache of objects to handle recursive references object references. This will be cleared after
 * every cerealization and de-cerealization call.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class ObjectCache {

    public static final String KEY_ID = "--object-id";
    public static final String KEY_REF = "--object-ref";

    private Map<Object, Map<String, Object>> objectMap = new HashMap<Object, Map<String, Object>>();
    private Map<Integer, Object> idMap = new HashMap<Integer, Object>();
    private int nextObjectId = 0;
    /** This doesn't have to do with caching, but it is the easiest way to pass settings */
    private CerealSettings settings;
    
    public ObjectCache() {
        this(new CerealSettings());
    }
    
    public ObjectCache(CerealSettings settings) {
        this.settings = settings;
    }

    /**
     * Reset the cache of interally remembered objects. This should be called after every
     * cerealization or de-cerealization call.
     */
    public void resetCache() {
        this.objectMap.clear();
        this.nextObjectId = 0;
    }

    /**
     * Cache the given java object. This method is used for cerealization and the object may be
     * referenced again by calls to {@link #getReferenceCereal(Object)}.
     * 
     * @param java
     *            the java object
     * @param cereal
     *            the cereal version of the given object
     */
    public void cache(Object java, Map<String, Object> cereal) {
        objectMap.put(java, cereal);
    }

    /**
     * Cache the the given java object with the given id. This is used for de-cerealization, if the
     * cereal contains the key {@value KEY_ID}, this will be added to the internal map allowing the
     * object to be retrieved from future calls to {@link #getReferenceObject(Map)}.
     * 
     * @param id
     *            the id of the item to cache
     * @param object
     *            the item to cache
     */
    public void cacheById(int id, Object object) {
        idMap.put(id, object);
    }

    /**
     * Check to see if the given object has already been cerealized. If it has, then a reference to
     * that cereal object is returned. If it has not yet been cerealized, this returns
     * <code>null</code> indicating that it should be cerealized following the default rules.
     * 
     * @param java
     *            the object to search for
     * 
     * @return a reference cereal object or <code>null</code> if no existing copy of this
     *         configuration has been found
     */
    public Map<String, Object> getReferenceCereal(Object java) {
        if (null == java) {
            return null;
        }

        Map<String, Object> cereal = objectMap.get(java);
        if (null == cereal) {
            return null;
        }
        if (!settings.shouldUseObjectReferences()) {
        	return cereal;
        }

        int id = -1;
        if (cereal.containsKey(KEY_ID)) {
            id = (Integer) cereal.get(KEY_ID);
        } else {
            id = nextObjectId;
            cereal.put(KEY_ID, id);
            nextObjectId++;
        }

        Map<String, Object> ref = new HashMap<String, Object>(1);
        ref.put(KEY_REF, id);
        return ref;
    }

    /**
     * Get the referenced object pointed to by the given cereal or <code>null</code> if the cereal
     * is not a reference to another object.
     * 
     * @param cereal
     *            the cereal to look for the reference tag on
     * 
     * @return the referenced java object if this was a reference cereal, otherwise
     *         <code>null</code>
     * 
     * @throws CerealException
     *             if the cereal references an object that has not yet been loaded
     */
    public Object getReferenceObject(Map<String, Object> cereal) throws CerealException {
        if (null == cereal) {
            return null;
        }

        if (cereal.containsKey(KEY_REF)) {
            int id = (Integer) cereal.get(KEY_REF);
            if (idMap.containsKey(id)) {
                return idMap.get(id);
            } else {
                throw new CerealException("Could not find referenced object with id " + id);
            }
        } else {
            return null;
        }
    }

    public CerealSettings getSettings() {
        return settings;
    }

    public void setSettings(CerealSettings settings) {
        this.settings = settings;
    }
}
