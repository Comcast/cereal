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
package com.comcast.cereal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.convert.ClassCerealizer;
import com.comcast.cereal.convert.DefaultCerealizer;
import com.comcast.cereal.engines.CerealEngine;
import com.comcast.cereal.engines.XmlCerealEngine;

/**
 * An annotation to indicate that the given field is to be included when serializing or
 * deserializing an object. This annotation can be used on class {@link Field}s or {@link Method}s.
 * This annotation is required for all {@link Method}s. It is only "required" for {@link Field}s on
 * classes marked with {@link CerealObject}. If the {@link CerealObject} annotation is not present
 * on a class, the default conversion valuess are applied to every {@link Field} not marked with
 * {@link Ignore}.
 * <p>
 * <h3>Example</h3>
 * <p>
 * Below is an example of a class with two members, one with a standard name mapping and one with a
 * custom name mapping. Both are standard cereal-supported types so neither requires a
 * <code>cerealizer</code> or <code>type</code> modifier.
 * 
 * <pre>
 * &#064;CerealObject
 * public class Student {
 *     &#064;Cereal
 *     private String name;
 * 
 *     &#064;Cereal(&quot;gpa&quot;)
 *     private float gradePointAverage;
 * }
 * </pre>
 * 
 * @author Clark Malmgren
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("rawtypes")
public @interface Cereal {

    /**
     * The name to use for this cereal value when converting. A name is required when this
     * annotation is present on a method. If this annotation is present on a field, the default
     * value will cause the name to match the field name.
     * 
     * @return the name associated with this cereal or <code>""</code> if the default name should be
     *         used.
     */
    String name() default "";

    /**
     * Defines the {@link Cerealizer} type to use when converting this value. If no value is
     * specified, this will first try to find an appropriate {@link Cerealizer} in the
     * {@link CerealFactory}. If none exists, a new {@link ClassCerealizer} will be created for the
     * {@link Class} type returned from {@link #type()}.
     * <p>
     * If the field type or method argument/return type is an array or a {@link List}, this
     * cerealizer will be applied to each element in the array or list.
     * </p>
     * 
     * @return the type of {@link Cerealizer} to use when converting
     */
    Class<? extends Cerealizer> cerealizer() default DefaultCerealizer.class;

    /**
     * Define the specific java-type to use when converting the value. If this is not specified, by
     * default, this will use the actual type associated with the field type or method
     * argument/return type. This is primarily used when converting an {@link List} of specific
     * types because determining the generic type is not easily discoverable.
     * 
     * @return the java-type to assume when converting this object.
     */
    Class<?> type() default void.class;

    /**
     * If set to <code>true</code>, this will throw an exception when de-cerealizing an object if
     * the source cereal did not contain a value for this {@link Field} or {@link Method}. This will
     * not fail however if it contains a <code>null</code> value, only if the entire key-value pair
     * is missing. When using this value with {@link Method}s, this value will be read from only the
     * {@link Cereal} annotation on the setter method. This defaults to <code>true</code>.
     * 
     * @return <code>true</code> if values are strictly required when de-cerealizing
     * @deprecated use {@link #defaultValue()} instead
     */
    boolean requireValue() default true;

    /**
     * If set to <code>{}</code> (default), this will throw an exception when de-cerealizing an
     * object if the source cereal did not contain a value for this {@link Field} or {@link Method}.
     * This will not fail however if it contains a <code>null</code> value, only if the entire
     * key-value pair is missing.
     * 
     * <p>
     * If there is a value set, it should be a JSON encoded string representation of the default
     * value. Only the first (0th) string in the array will be read. This is an array only to allow
     * for easily encoding an empty array as a default value.
     * </p>
     * 
     * <p>
     * If the target type is a {@link String}, the quotes normally required for JSON encoding should
     * be left out. The Cereal framework will NOT do any processing of the value if the expected
     * Java type is a {@link String}.
     * </p>
     * 
     * <p>
     * If the defaultValue is specified as <code>"null"</code>, the value set will actually just be
     * <code>null</code> and it will not be processed as a string.
     * </p>
     * 
     * <p>
     * This value is ignored when executing any of the apply methods of the {@link CerealEngine}
     * because the assumption is that if the value has already been initailized when that object was
     * created.
     * </p>
     * 
     * <p>
     * Note that When using this value with {@link Method}s, this value will be read from only the
     * {@link Cereal} annotation on the setter method.
     * </p>
     * 
     * @return if not <code>{}</code>, an array of length 1 with a JSON encoded string
     *         representation of the default value that should be used
     */
    String[] defaultValue() default {};
    
    /**
     * If no value is given for the field that this annotation is on then it will instantiate an object
     * with the given class using the default constructor of this class. The field will then be set to
     * this object. This is useful for setting collections to be an empty array when they aren't
     * set in the serialized data. Or setting empty maps.
     * @return
     */
    Class<?>[] defaultObjectClass() default {};

    /**
     * Directive to indicate how to record this element. This only applies when cerealizing and
     * de-cerealizing using an {@link XmlCerealEngine}.
     * 
     * @return the directive indicating how to record this value in XML
     */
    XmlType xmlType() default XmlType.AUTO;

    /**
     * The available ways of recording XML information. This is ONLY used with a
     * {@link XmlCerealEngine}.
     * 
     * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
     */
    public static enum XmlType {
        /**
         * Record this value as a separate XML element whose tag name will be equal to the value
         * determined according to the rules defined on {@link Cereal#name()};
         */
        ELEMENT,

        /**
         * Record this value as an attribute on its parent element with an attribute name will be
         * equal to the value determined according to the rules defined on {@link Cereal#name()};
         */
        ATTRIBUTE,

        /**
         * Automatically determine the recording method based upon the content type. {@link String}
         * s, {@link Number}s, {@link Boolean}s and <code>null</code>s will be recorded as
         * attributes and all other {@link Object}s will be recorded as Elements.
         */
        AUTO
    }
}
