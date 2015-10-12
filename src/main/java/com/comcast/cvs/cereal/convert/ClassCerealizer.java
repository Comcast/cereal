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

import static com.comcast.cvs.cereal.impl.ReflectionHelper.getGenericClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.objenesis.ObjenesisHelper;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealFactory;
import com.comcast.cvs.cereal.CerealFactoryAware;
import com.comcast.cvs.cereal.Cerealizer;
import com.comcast.cvs.cereal.ObjectCache;
import com.comcast.cvs.cereal.annotations.Cereal;
import com.comcast.cvs.cereal.annotations.CerealObject;
import com.comcast.cvs.cereal.annotations.Ignore;
import com.comcast.cvs.cereal.engines.CerealEngine;
import com.comcast.cvs.cereal.impl.CerealAccess;
import com.comcast.cvs.cereal.impl.FieldAccess;
import com.comcast.cvs.cereal.impl.MethodAccess;

/**
 * Class to convert java objects to and from the standard cereal
 * <code>Map&lt;String, Object&gt;</code> representation of a class.
 * 
 * @author Clark Malmgren
 * 
 * @param <J>
 *            the class to convert to and from JSON
 */
public class ClassCerealizer<J extends Object> implements Cerealizer<J, Map<String, Object>>,
        CerealFactoryAware {
    
    private static final Map<Class<?>, Class<?>> DEFAULTS;
    static {
        DEFAULTS = new HashMap<Class<?>, Class<?>>();
        DEFAULTS.put(Map.class, HashMap.class);
        DEFAULTS.put(List.class, ArrayList.class);
        DEFAULTS.put(Set.class, HashSet.class);
        DEFAULTS.put(Queue.class, LinkedList.class);
    }

    private Class<J> javaType;
    private CerealFactory cerealFactory;
    private Map<String, CerealAccess> delegates;

    /**
     * The java type that this {@link ClassCerealizer} is converting to and from.
     * 
     * @param javaType
     *            the java type
     */
    public ClassCerealizer(Class<J> javaType) {
        this.javaType = javaType;
        this.cerealFactory = null;
        this.delegates = new HashMap<String, CerealAccess>();
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

    /**
     * Initializes this {@link ClassCerealizer} starting the process of reading the annotations and
     * applying the rules defined in {@link Cereal}, {@link CerealObject} and {@link Ignore}.
     * 
     * @throws CerealException
     *             if there was a problem scanning the object and configuring this cerealizer
     */
    public void initialize() throws CerealException {
        if (null == javaType.getAnnotation(CerealObject.class)) {
            /* Cerealize all fields not marked with @Ignore */
            scanForAccess(javaType, true);
        } else {
            /* Cerealize only fields and methods marked with @CerealValue */
            scanForAccess(javaType, false);
        }
    }
    
    public static String getDefaultValue(Cereal info) {
        String[] array = info.defaultValue();
        return (null == array || array.length < 1) ? null : array[0];
    }
    
    public static Class<?> getDefaultObjectClass(Cereal info) {
        Class<?>[] array = info.defaultObjectClass();
        return (null == array || array.length < 1) ? null : array[0];
    }

    /**
     * A helper method to recursively scan looking for how to apply {@link CerealAccess} objects to
     * this class.
     * 
     * @param clazz
     *            the current class to scan
     * @param scanAll
     *            If <code>false</code>, the initial class was marked with {@link CerealObject}
     *            indicating that only fields marked with {@link Cereal} should be included. If
     *            <code>true</code>, all fields should be included using the {@link Cereal}
     *            information if it exists or defaults if it doesn't.
     * 
     * @throws CerealException
     *             if there was a problem scanning this class or configuring internal
     *             {@link Cerealizer}s
     * 
     * @see Cereal
     * @see CerealObject
     */
    @SuppressWarnings("rawtypes")
    private void scanForAccess(Class<?> clazz, boolean scanAll) throws CerealException {
        /* Look on all fields in this class */
        for (Field field : clazz.getDeclaredFields()) {
            Cereal info = field.getAnnotation(Cereal.class);
            if (null != info) {
                int genericArg = Map.class.isAssignableFrom(field.getType()) ? 1 : 0;
                Class<?> genericClass = getGenericClass(field.getGenericType(), genericArg);
                Cerealizer cerealizer = getCerealizer(info, field.getType(), genericClass);
                String name = ("".equals(info.name()) ? field.getName() : info.name());
                boolean cerealizerOverride = (null != info) && (DefaultCerealizer.class != info.cerealizer());

                FieldAccess access = new FieldAccess(field, cerealizer, info.requireValue(), getDefaultValue(info), 
                		getDefaultObjectClass(info), cerealFactory, cerealizerOverride);
                delegates.put(name, access);

                // Already put in the field, so we just continue to next field
                continue;
            }

            if (scanAll) {
                int mods = field.getModifiers();
                if (!ignore(field) && !Modifier.isTransient(mods) && !Modifier.isStatic(mods)) {
                    int genericArg = Map.class.isAssignableFrom(field.getType()) ? 1 : 0;
                    Class<?> genericClass = getGenericClass(field.getGenericType(), genericArg);
                    Cerealizer cerealizer = getCerealizer(null, field.getType(), genericClass);

                    FieldAccess access = new FieldAccess(field, cerealizer, true, null, null, cerealFactory, false);
                    delegates.put(field.getName(), access);
                }
            }
        }

        /* Now scan for methods */
        for (Method method : clazz.getDeclaredMethods()) {
            Cereal info = method.getAnnotation(Cereal.class);
            if (null != info) {
                if ("".equals(info.name())) {
                    throw new CerealException("@CerealValue annotation on '" + method.toString()
                            + "' must have a declared name");
                }

                Class<?> type = null;
                Type genericType = null;

                boolean setter = isMethodSetter(method);

                /* Verify method type no matter what */
                if (setter) {
                    type = method.getParameterTypes()[0];
                    genericType = method.getGenericParameterTypes()[0];
                } else {
                    type = method.getReturnType();
                    genericType = method.getGenericReturnType();
                }

                Class<?> genericClass = getGenericClass(genericType);

                MethodAccess access = null;
                if (delegates.containsKey(info.name())) {
                    access = (MethodAccess) delegates.get(info.name());
                } else {
                    Cerealizer cerealizer = getCerealizer(info, type, genericClass);
                    access = new MethodAccess(cerealizer);
                    delegates.put(info.name(), access);
                }

                if (setter) {
                    access.setSetter(method);
                    access.setRequireValue(info.requireValue());
                    access.setDefaultValue(getDefaultValue(info));
                } else {
                    access.setGetter(method);
                }
            }
        }

        /* Recurse on superclass & interfaces */
        for (Class<?> iface : clazz.getInterfaces()) {
            scanForAccess(iface, scanAll);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (null != superclass && !Object.class.equals(superclass)) {
            scanForAccess(superclass, scanAll);
        }
    }

    /**
     * Looks at a method to determine if it is a setter or getter method. If it is neither a getter
     * or a setter, this will throw a {@link CerealException}.
     * 
     * @param method
     *            the method to scan
     * 
     * @return <code>true</code> if the method scanned was a setter method, <code>false</code> if it
     *         was a getter method
     * 
     * @throws CerealException
     *             if the method scanned was neither a getter or setter method
     */
    private boolean isMethodSetter(Method method) throws CerealException {
        String name = method.getName();

        if (name.startsWith("set") && method.getParameterTypes().length == 1
                && void.class.equals(method.getReturnType())) {
            return true;
        } else if (name.startsWith("get") && method.getParameterTypes().length == 0
                && !void.class.equals(method.getReturnType())) {
            return false;
        } else {
            throw new CerealException("Method is not a valid java bean setter or getter: '"
                    + method.toString() + "'");
        }
    }

    /**
     * Helper method to get a cerealizer of a given type.
     * 
     * @param info
     *            the Cereal annotation information
     * @param type
     *            the java type
     * @param genericClass
     *            the Java 1.5 generic type associated with the appropriate field, argument or
     *            return type. This is only used if the actual type is a {@link List}.
     * 
     * @return the appropriate Cerealizer
     * 
     * @throws CerealException
     *             if there was a problem creating or initializing the associated Cerealizer
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Cerealizer getCerealizer(Cereal info, Class<?> type, Class<?> genericClass)
            throws CerealException {
        Cerealizer cerealizer = null;
        boolean cerealizerOverride = false;

        if (null != info) {
            if (DefaultCerealizer.class != info.cerealizer()) {
                cerealizerOverride = true;
                cerealizer = newCerealizer(info.cerealizer());
            } else if (void.class != info.type()) {
                cerealizer = cerealFactory.getCerealizer(info.type());
            }
        }
        
        /* Special case to check for a byte array */
        if (type.equals(byte[].class)) {
            return cerealFactory.getCachedCerealizer(ByteArrayCerealizer.class);
        }

        if (null == cerealizer && type.isArray()) {
            cerealizer = cerealFactory.getCerealizer(type.getComponentType());
        } else if (null == cerealizer && Collection.class.isAssignableFrom(type)) {
            if (null != genericClass) {
                /* We were able to determine the generic signature parameter */
                cerealizer = cerealFactory.getCerealizer(genericClass);
            } else {
                /* Couldn't figure it out, just use the DynamicCerealizer */
                cerealizer = new DynamicCerealizer();
                ((DynamicCerealizer) cerealizer).setCerealFactory(cerealFactory);
            }
        }

        if (type.isArray()) {
            cerealizer = new ArrayCerealizer(cerealizer, type.getComponentType());
        } else if (Collection.class.isAssignableFrom(type)) {
            cerealizer = new CollectionCerealizer(cerealizer, cerealizerOverride, (Class<? extends Collection>) type);
            ((CollectionCerealizer) cerealizer).setCerealFactory(cerealFactory);
        } else if (Map.class.isAssignableFrom(type)) {

            if ((cerealizer == null) && (genericClass != null)) {
                cerealizer = cerealFactory.getCerealizer(genericClass);
            }
        	cerealizer = new MapCerealizer(cerealizer);
            ((MapCerealizer) cerealizer).setCerealFactory(cerealFactory);
        }

        if (null == cerealizer) {
            cerealizer = cerealFactory.getCerealizer(type);
        }

        return cerealizer;
    }

    /**
     * Helper method to either get a cached version of a specific Cerealizer type or create a new
     * Cerealizer instance. If the type implements {@link CerealFactoryAware}, this will invoking
     * the {@link CerealFactoryAware#setCerealFactory(CerealFactory)} method.
     * 
     * @param type
     *            the class of Cerealizer to invoke
     * 
     * @return a new or cached instance of the given type
     * 
     * @throws CerealException
     *             if there was a problem creating or initializing an instance of the given type
     */
    @SuppressWarnings("rawtypes")
    private <C extends Cerealizer> C newCerealizer(Class<C> type) throws CerealException {
        try {
            C cerealizer = cerealFactory.getCachedCerealizer(type);
            if (null == cerealizer) {
                cerealizer = type.newInstance();
                if (CerealFactoryAware.class.isAssignableFrom(type)) {
                    CerealFactoryAware cfa = (CerealFactoryAware) cerealizer;
                    cfa.setCerealFactory(cerealFactory);
                }
            }

            return cerealizer;
        } catch (Throwable throwable) {
            throw new CerealException("Failed to instantiate the cerealizer of type '"
                    + type.getName() + "'", throwable);
        }
    }

    /**
     * Determine if the {@link Ignore} annotation is present on a given field.
     * 
     * @param field
     *            the field to scan
     * 
     * @return true if the field is marked with {@link Ignore}
     */
    private static boolean ignore(Field field) {
        return (null != field.getAnnotation(Ignore.class));
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public Map<String, Object> cerealize(J object, ObjectCache objectCache) throws CerealException {

        if (null == object) {
            return null;
        }

        /* First check to see if this object has already been created */
        Map<String, Object> cereal = objectCache.getReferenceCereal(object);
        if (null != cereal) {
            return cereal;
        }

        /* Cache this object BEFORE we start recursing over the fields */
        cereal = new HashMap<String, Object>();
        objectCache.cache(object, cereal);

        for (String name : delegates.keySet()) {
            CerealAccess access = delegates.get(name);
            cereal.put(name, access.getCereal(object, objectCache));
        }
        
        return cereal;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public J deCerealize(Map<String, Object> cereal, ObjectCache objectCache) throws CerealException {

        if (null == cereal) {
            return null;
        }
        

        J object = (J) objectCache.getReferenceObject(cereal);
        if (null != object) {
            return object;
        }
        
        try {
            if (DEFAULTS.containsKey(javaType)) {
                object = (J) DEFAULTS.get(javaType).newInstance();
            } else {
                object = javaType.newInstance();
            }
        } catch (Exception ex) {
            object = (J) ObjenesisHelper.newInstance(javaType);
        }
        
        /* If this object has an object id, store it now before we continue */
        if (cereal.containsKey(ObjectCache.KEY_ID)) {
            objectCache.cacheById((Integer) cereal.get(ObjectCache.KEY_ID), object);
        }

        applyCereal(cereal, object, false, objectCache);

        return object;
    }

    /**
     * Decerealize the given representation and then apply it ot the target object.
     * 
     * @param cereal
     *            the cereal to convert and apply
     * @param target
     *            the object to apply it to
     * @param ignoreMissing
     *            if <code>true</code>, values that are missing in the passed cereal (the name/key
     *            is missing) will not override existing values in the target. This should only be
     *            used when the values sent only represent the changed values in the original
     *            object. This does not propogate though so if a child object is modified at all,
     *            the entire object must be provided.
     *
     * @throws CerealException
     * 
     * @see {@link CerealEngine#applyFromString(String, Object)}
     * @see {@link CerealEngine#apply(java.io.Reader, Object)}
     * @see {@link CerealEngine#apply(java.io.File, Object)}
     */
    public void applyCereal(Map<String, Object> cereal, J target, boolean ignoreMissing, ObjectCache objectCache)
            throws CerealException {
        
        for (String name : delegates.keySet()) {
            CerealAccess access = delegates.get(name);
            
            /*
             * If the value is missing, skip this access if either we can globally ignore missing
             * values or this specific Access object doesn't require a value.
             */
            boolean missing = !cereal.containsKey(name);
            Object value = null;
            if (!missing) {
                value = cereal.get(name);
            } else if (ignoreMissing) {
                /*
                 * It is important to look at ignoreMissing BEFORE the default value. Otherwise
                 * updates that didn't include a value could be set back to an original default
                 * value when it wasn't really intended.
                 */
                continue;
            } else if (access.hasDefaultValue()) {
            	try {
            		value = access.getDefaultValue();
            	} catch (CerealException e) {
            		throw new CerealException("Could not get default value for field '" + name + "'", e);
            	}
                /* Check for the special case of "null" */
                if ("null".equals(value)) {
                    value = null;
                }
            } else if (!access.requireValue()) {
                continue;
            } else {
            	throw new CerealException("No value given for required field '" + name + "'");
            }
            
            access.applyCereal(value, target, objectCache);
        }
    }
}
