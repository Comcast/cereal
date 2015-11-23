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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.comcast.cereal.annotations.CerealClass;
import com.comcast.cereal.convert.ArrayCerealizer;
import com.comcast.cereal.convert.ByteArrayCerealizer;
import com.comcast.cereal.convert.CerealizableCerealizer;
import com.comcast.cereal.convert.ClassCerealizer;
import com.comcast.cereal.convert.CollectionCerealizer;
import com.comcast.cereal.convert.DateCerealizer;
import com.comcast.cereal.convert.DynamicCerealizer;
import com.comcast.cereal.convert.EnumCerealizer;
import com.comcast.cereal.convert.MapCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.BooleanCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.CharCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.DoubleCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.FloatCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.IntegerCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.LongCerealizer;
import com.comcast.cereal.convert.PrimitiveCerealizer.ShortCerealizer;
import com.comcast.cereal.convert.SimpleCerealizer;
import com.comcast.cereal.engines.CerealEngine;

/**
 * The <i>CerealFactory</i> is a central repository for all {@link Cerealizer} objects within a
 * given {@link CerealEngine}.
 * 
 * @see CerealEngine#getCerealFactory()
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class CerealFactory {

    /* This is a mapping of types to cached base type and class cerealizers */
    private final Map<TypeSubtype, Cerealizer<?, ?>> map;

    /* This is a cache of instance objects for typed cerealizers */
    private final Map<Class<?>, Cerealizer<?, ?>> cache;
    
    /* This is a cache of the Dynamic Cerealizer */
    private DynamicCerealizer dc;

    /**
     * Construct a new {@link CerealFactory} and initialize the types supported by default (see
     * {@link Cerealizer} for that list) to use a {@link SimpleCerealizer}.
     * 
     * @param objectCache
     *            the {@link ObjectCache} used for the {@link CerealEngine} associated with this
     *            factory
     */
    public CerealFactory() {

        this.map = new HashMap<TypeSubtype, Cerealizer<?, ?>>();
        this.cache = new HashMap<Class<?>, Cerealizer<?, ?>>();

        /* Insert the SimpleCeralizer for all the primitive types */
        final SimpleCerealizer sc = new SimpleCerealizer();
        addCerealizer(String.class, sc);
        final BooleanCerealizer bc = new BooleanCerealizer();
        addCerealizer(boolean.class, bc);
        addCerealizer(Boolean.class, bc);
        addCerealizer(byte.class, sc);
        addCerealizer(Byte.class, sc);
        final CharCerealizer cs = new CharCerealizer();
        addCerealizer(char.class, cs);
        addCerealizer(Character.class, cs);
        final ShortCerealizer shc = new ShortCerealizer();
        addCerealizer(short.class, shc);
        addCerealizer(Short.class, shc);
        final IntegerCerealizer ic = new IntegerCerealizer();
        addCerealizer(int.class, ic);
        addCerealizer(Integer.class, ic);
        final LongCerealizer lc = new LongCerealizer();
        addCerealizer(long.class, lc);
        addCerealizer(Long.class, lc);
        final FloatCerealizer fc = new FloatCerealizer();
        addCerealizer(float.class, fc);
        addCerealizer(Float.class, fc);
        final DoubleCerealizer dbc = new DoubleCerealizer();
        addCerealizer(double.class, dbc);
        addCerealizer(Double.class, dbc);

        /* If it is java.lang.Object, it should use the DynamicCerealizer */
        dc = new DynamicCerealizer();
        dc.setCerealFactory(this);
        addCerealizer(Object.class, dc);

        /* Add the DateCerealizer */
        final DateCerealizer dac = new DateCerealizer();
        addCerealizer(Date.class, dac);

        /* Cache the default cerealizers */
        this.cacheCerealizers(sc, bc, cs, shc, ic, lc, fc, dbc, dc, dac);

        /* Simply cache a ByteArrayCerealizer */
        this.cacheCerealizer(new ByteArrayCerealizer());
    }

    /**
     * Get a cached version of a Cerealizer that is capable of converting to and from the specific
     * java type.
     * 
     * @param type
     *            the java type to convert to and from
     *            
     * @return the correct {@link Cerealizer}
     * 
     * @throws CerealException
     *             if there is a problem instantiating the {@link ClassCerealizer} created for the
     *             given type
     */
    public <J> Cerealizer<J, ?> getCerealizer(Class<J> type) throws CerealException {
    	return getCerealizer(type, null);
    }

    /**
     * Get a cached version of a Cerealizer that is capable of converting to and from the specific
     * java type.
     * 
     * @param type
     *            the java type to convert to and from
     * @param subtype
     *            the java type to convert elements within a Collection to and from, type must be a subclass of Collection
     * 
     * 
     * @return the correct {@link Cerealizer}
     * 
     * @throws CerealException
     *             if there is a problem instantiating the {@link ClassCerealizer} created for the
     *             given type
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <J> Cerealizer<J, ?> getCerealizer(Class<J> type, Class<?> subtype) throws CerealException {
    	TypeSubtype typeSubtype = new TypeSubtype(type, subtype);
        if (map.containsKey(typeSubtype)) {
            return (Cerealizer<J, ?>) map.get(typeSubtype);
        }
        
        /* Handle use case where the class has the @CerealClass annotation */
        CerealClass cerealClass = type.getAnnotation(CerealClass.class);
        if (null != cerealClass) {
            Cerealizer<J, ?> cerealizer = (Cerealizer<J, ?>) getCachedCerealizer(cerealClass.value());
            if (null == cerealizer) {
                try {
                    cerealizer = (Cerealizer<J, ?>) cerealClass.value().newInstance();
                    cacheCerealizer(cerealizer);
                } catch (Exception ex) {
                    throw new CerealException("Failed to create new cerealizer", ex);
                }
            }
            return cerealizer;
        }

        if (type.isEnum()) {
            return new EnumCerealizer(type);
        }
        
        /* Special case to check for a byte array */
        if (type.equals(byte[].class)) {
            return (Cerealizer<J, ?>) getCachedCerealizer(ByteArrayCerealizer.class);
        }

        if (type.isArray()) {
            Class<?> arrayType = type.getComponentType();
            Cerealizer<?, ?> delegate = getCerealizer(arrayType);
            return new ArrayCerealizer(delegate, arrayType);
        }
        
        if (Collection.class.isAssignableFrom(type)) {
        	/** If we were given a subtype, use that for the internal cerealizer for the collection */
        	Cerealizer elementCerealizer = subtype != null ? getCerealizer(subtype) : dc;
            CollectionCerealizer cerealizer = new CollectionCerealizer(elementCerealizer, (Class<? extends Collection>) type);

            map.put(typeSubtype, cerealizer);
            cerealizer.setCerealFactory(this);

            return (Cerealizer<J, ?>) cerealizer;
        }
        
        if (Map.class.isAssignableFrom(type)) {
            MapCerealizer mc = new MapCerealizer();
            mc.setMapClass((Class<? extends Map>) type);
            mc.setCerealFactory(this);
            addCerealizer(type, mc);
            return (Cerealizer<J, ?>) mc;
        }

        if (Cerealizable.class.isAssignableFrom(type)) {
            CerealizableCerealizer cerealizer = new CerealizableCerealizer(type);

            addCerealizer(type, cerealizer);
            cerealizer.setCerealFactory(this);

            return cerealizer;
        } else {
            ClassCerealizer<J> cerealizer = new ClassCerealizer<J>(type);

            /*
             * Need to insert the Cerealizer into the map before initializing because
             * self-referencing classes would otherwise infinitely recurse
             */
            addCerealizer(type, cerealizer);

            cerealizer.setCerealFactory(this);
            cerealizer.initialize();

            return cerealizer;
        }
    }
    
    /**
     * Cache an array of cerealizers
     * @param cerealizers The list of cerealizers to cache
     */
    public void cacheCerealizers(Cerealizer<?, ?>... cerealizers) {
    	for (Cerealizer<?, ?> cerealizer : cerealizers) {
    		cacheCerealizer(cerealizer);
    	}
    }

    /**
     * Cache a specific instance of specific cerealizer type. This is primarily used when an
     * implementation of a {@link Cerealizer} requires access to object that must be configured or
     * referenced at runtime.
     * 
     * @param cerealizer
     *            the instance to cache
     * 
     * @see #getCachedCerealizer(Class)
     */
    public void cacheCerealizer(Cerealizer<?, ?> cerealizer) {
        if (cerealizer instanceof CerealFactoryAware) {
            ((CerealFactoryAware) cerealizer).setCerealFactory(this);
        }

        cache.put(cerealizer.getClass(), cerealizer);
    }

    /**
     * Get a cached instance of a specific {@link Cerealizer} if one has already been cached or
     * <code>null</code> if none has been created.
     * 
     * @param type
     *            the java type of {@link Cerealizer} to fetch
     * 
     * @return a cached instance or <code>null</code> if no instance has yet been cached
     * 
     * @see #cacheCerealizer(Cerealizer)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <C extends Cerealizer> C getCachedCerealizer(Class<C> type) {
        return (C) cache.get(type);
    }
    
    /**
     * Checks if the given cereal is a map and contains a --class property. This property is then used to create
     * a class cerealizer for that class and uses that cerealizer. If the cereal does not have
     * the --class property then the given default cerealizer is used.
     * @param cereal The cereal used to look up the --class property
     * @param defaultCerealizer The cereal to use if the cereal does not have the --class property
     * @return
     * @throws CerealException
     */
    @SuppressWarnings("rawtypes")
    public Cerealizer getRuntimeCerealizer(Object cereal, Cerealizer defaultCerealizer) throws CerealException {
        Class<?> runtimeClass = getRuntimeClass(cereal);
        return runtimeClass == null ? defaultCerealizer : getCerealizer(runtimeClass);
    }

    @SuppressWarnings("rawtypes")
    public Class<?> getRuntimeClass(Object cereal) throws CerealException {
        if (cereal instanceof Map) {
            Map map = (Map) cereal;
            if (map.containsKey("--class")) {
                Object className = map.get("--class");
                if (className instanceof String) {
                    try {
                        return Class.forName((String) className);
                    } catch (ClassNotFoundException e) {
                        throw new CerealException("Failed to get runtime cerealizer", e);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Tells this factory to use the given cerealizer for the given class
     * @param clazz The class that the cerealizer is for
     * @param cerealizer The cerealizer to use for the given class
     */
    public <T> void addCerealizer(Class<?> clazz, Cerealizer<?, ?> cerealizer) {
    	this.map.put(new TypeSubtype(clazz, null), cerealizer);
    }
    
    /**
     * Allows caching based on type and subtype pair
     * @author Kevin Pearson
     *
     */
    static class TypeSubtype {
    	private Class<?> type;
    	private Class<?> subtype;
    	
		public TypeSubtype(Class<?> type, Class<?> subtype) {
			this.type = type;
			this.subtype = subtype;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((subtype == null) ? 0 : subtype.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TypeSubtype other = (TypeSubtype) obj;
			if (!Objects.equals(type, other.type)) return false;
			if (!Objects.equals(subtype, other.subtype)) return false;
			return true;
		}
    	
    }

}
