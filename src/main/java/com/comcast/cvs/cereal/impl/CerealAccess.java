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
package com.comcast.cvs.cereal.impl;

import java.io.StringReader;
import java.lang.reflect.Constructor;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.Cerealizer;
import com.comcast.cvs.cereal.ObjectCache;

/**
 * Interface for accessing and applying cereal to fields or methods. All {@link CerealAccess}
 * implementations are given the appropriate {@link Cerealizer} objects to use when converting
 * before setting a value or after reading a value.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
@SuppressWarnings("rawtypes")
public abstract class CerealAccess {

	protected Cerealizer cerealizer;
	private boolean requireValue;
	private String defaultValue;
	private Class<?> defaultObjectClass;

	/**
	 * Construct a new Access object that uses the given cerealizer to convert.
	 * 
	 * @param field
	 *            the field to read from and write to
	 * @param cerealizer
	 *            the cerealizer to use when converting
	 * @param requireValue
	 *            <code>true</code> if a value is required for this access object
	 * @param defaultValue
	 *            if not <code>null</code>, the JSON encoded default value to use when no value is
	 *            found when de-cerealizing
	 */
	public CerealAccess(Cerealizer cerealizer, boolean requireValue, String defaultValue, 
			Class<?> defaultObjectClass) {
		this.cerealizer = cerealizer;
		this.requireValue = requireValue;
		this.defaultValue = defaultValue;
		this.defaultObjectClass = defaultObjectClass;
	}

	/**
	 * Set the default value.
	 * 
	 * @param defaultValue
	 *            if not <code>null</code>, the JSON encoded default value to use when no value is
	 *            found when de-cerealizing
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Change whether or not this access object requires a value when de-cerealizing.
	 * 
	 * @param requireValue
	 *            <code>true</code> if a value is required for this access object
	 */
	public void setRequireValue(boolean requireValue) {
		this.requireValue = requireValue;
	}

	/**
	 * Sets the class for the default object
	 * @param defaultObjectClass The class of the default object
	 */
	public void setDefaultObjectClass(Class<?> defaultObjectClass) {
		this.defaultObjectClass = defaultObjectClass;
	}

	/**
	 * Convert the given cereal and then apply (write) the java object in the appropriate fashion to
	 * the given target object.
	 * 
	 * @param cereal
	 *            the cereal to convert and apply
	 * @param target
	 *            the object to apply the converted value to
	 * 
	 * @throws CerealException
	 *             if there was a problem converting or applying the value
	 */
	public abstract void applyCereal(Object cereal, Object target, ObjectCache objectCache)
			throws CerealException;

	/**
	 * Read a java object from the given source object and then convert it.
	 * 
	 * @param source
	 *            the object to read the value from
	 * 
	 * @return the converted value read from the given object
	 * 
	 * @throws CerealException
	 *             if there was a problem getting the value or converting it
	 */
	public abstract Object getCereal(Object source, ObjectCache objectCache) throws CerealException;

	/**
	 * Returns <code>true</code> if this particular CerealAccess object is directed to require a
	 * value when applying cereal.
	 * 
	 * @return <code>true</code> if a value is required for this cereal
	 */
	public boolean requireValue() {
		return requireValue;
	}

	public Class<?> getDefaultObjectClass() {
		return defaultObjectClass;
	}

	/**
	 * Returns true if a default value was provided. Calls to {@link #getDefaultValue()} should
	 * always be checked using this method first.
	 * 
	 * @return <code>true</code> if a default value exists
	 */
	public boolean hasDefaultValue() {
		return (null != defaultValue) || (null != defaultObjectClass);
	}



	/**
	 * Get the java type of this access object.
	 * 
	 * @return the java type
	 * @throws CerealException
	 *             if the type can not be determined
	 */
	public abstract Class getJavaType() throws CerealException;

	/**
	 * Get the default value decoded into Cereal compatible objects.
	 * 
	 * @return the default value in cereal compatible representation
	 * @throws CerealException
	 *             if no
	 */
	public Object getDefaultValue() throws CerealException {
		if (defaultObjectClass != null) {
			Constructor<?>[] constrs = defaultObjectClass.getConstructors();
			Constructor defaultConstr = null;
			for (Constructor<?> constr : constrs) {
				if (constr.getParameterTypes().length == 0) {
					defaultConstr = constr;
					break;
				}
			}
			if (defaultConstr == null) {
				throw new CerealException("Failed to instantiate default object. No public default constructor found for " + defaultObjectClass.getName());
			}
			try {
				return defaultConstr.newInstance();
			} catch (Exception e) {
				throw new CerealException("Failed to instantiate default object for class " + defaultObjectClass.getName());
			}
		}
		if (null == defaultValue) {
			throw new CerealException("Default Request but none Provided");
		}

		if (String.class.equals(getJavaType())) {
			return defaultValue;
		} else {
			try {
				JsonHelper helper = new JsonHelper();
				return helper.read(new StringReader(defaultValue));
			} catch (Exception ex) {
				throw new CerealException("Failed to read JSON encoded default value: "
						+ defaultValue, ex);
			}
		}
	}
}
