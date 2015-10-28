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
package com.comcast.cereal.engines;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.annotations.Cereal;

/**
 * A <i>CerealEngine</i> converts java objects to a formatted text language. As an intermediate
 * step, the rest of the Cereal framework will convert to and from a base set of cereal-supported
 * java types. See the {@link Cereal} annotation for the full list of supported types.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface CerealEngine {

    /**
     * Get the cereal factory used by this cereal engine.
     * 
     * @return the {@link CerealFactory}
     */
    CerealFactory getCerealFactory();

    /**
     * Convert the given object to cereal-compatible objects. Unless the class type is unknown, the
     * {@link #cerealize(Object, Class)} method should be used for corrrectness.
     * 
     * @param object
     *            the object to convert
     * 
     * @return the object converted to cereal compatible objects
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    Object cerealize(Object object) throws CerealException;

    /**
     * Convert the given object to cereal-compatible objects.
     * 
     * @param t
     *            the object to convert
     * @param clazz
     *            the class definition to use when converting the object. If the full object type is
     *            a subclass of this type, only this defined class definition (and its superclasses)
     *            annotations will be used when converting the object.
     * 
     * @return the object converted to cereal compatible objects
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> Object cerealize(T t, Class<T> clazz) throws CerealException;

    /**
     * Convert the given cereal-compatible object ({@link HashMap}) to a Java Object.
     * 
     * @param cereal
     *            the cereal-compatible encoded definition of the object
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> T deCerealize(Object cereal, Class<T> clazz) throws CerealException;

    /**
     * Convert the given cereal-compatible object ({@link HashMap}) and apply it to the given java
     * Object.
     * 
     * @param cereal
     *            the cereal-compatible encoded definition of the object
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void apply(Map<String, Object> cereal, Object target) throws CerealException;

    /**
     * Cerealize the given object and then convert it to the correct string representation. Unless
     * the class type is unknown, the {@link #writeToString(Object, Class)} method should be used
     * for corrrectness.
     * 
     * @param object
     *            the object to convert
     * 
     * @return the object formatted in the correct string representation
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    String writeToString(Object object) throws CerealException;

    /**
     * Cerealize the given object and then convert it to the correct string representation.
     * 
     * @param t
     *            the object to convert
     * @param clazz
     *            the class definition to use when converting the object. If the full object type is
     *            a subclass of this type, only this defined class definition (and its superclasses)
     *            annotations will be used when converting the object.
     * 
     * @return the object formatted in the correct string representation
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> String writeToString(T t, Class<T> clazz) throws CerealException;

    /**
     * Cerealize the given object, convert it to the correct string representation and write it to
     * the given writer.
     * 
     * @param t
     *            the object to convert
     * @param clazz
     *            the class definition to use when converting the object. If the full object type is
     *            a subclass of this type, only this defined class definition (and its superclasses)
     *            annotations will be used when converting the object.
     * @param writer
     *            the writer where the formatted string should be written
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> void write(T t, Class<T> clazz, Writer writer) throws CerealException;

    /**
     * Cerealize the given object, convert it to the correct string representation and write it to
     * the given file.
     * 
     * @param t
     *            the object to convert
     * @param clazz
     *            the class definition to use when converting the object. If the full object type is
     *            a subclass of this type, only this defined class definition (and its superclasses)
     *            annotations will be used when converting the object.
     * @param file
     *            the file where the formatted string should be written
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> void write(T t, Class<T> clazz, File file) throws CerealException;

    /**
     * Cerealize the given object, convert it to the correct string representation and write it to
     * the given writer. Unless the class type is unknown, the {@link #write(Object, Class, Writer)}
     * method should be used for corrrectness.
     * 
     * @param object
     *            the object to convert
     * @param writer
     *            the writer where the formatted string should be written
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void write(Object object, Writer writer) throws CerealException;

    /**
     * Cerealize the given object, convert it to the correct string representation and write it to
     * the given file. Unless the class type is unknown, the {@link #write(Object, Class, File)}
     * method should be used for corrrectness.
     * 
     * @param object
     *            the object to convert
     * @param file
     *            the file where the formatted string should be written
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void write(Object object, File file) throws CerealException;

    /**
     * Convert the encoded string into cereal-compatible objects and then de-cerealize that into the
     * correct values.
     * 
     * @param string
     *            the encoded string definition of the object
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> T readFromString(String string, Class<T> clazz) throws CerealException;

    /**
     * Convert the encoded classpath resource into cereal-compatible objects and then de-cerealize
     * that into the correct values. The path to the resource should be either absolute or relative
     * to {@link CerealEngine}.
     * 
     * @param path
     *            the path to the encoded resource to read
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> T readFromClasspath(String path, Class<T> clazz) throws CerealException;

    /**
     * Convert the encoded string read from the given {@link Reader} into cereal-compatible objects
     * and then de-cerealize that into the correct values.
     * 
     * @param reader
     *            the {@link Reader} that contains the encoded String definition of the object
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> T read(Reader reader, Class<T> clazz) throws CerealException;

    /**
     * Convert the encoded string read from the given {@link File} into cereal-compatible objects
     * and then de-cerealize that into the correct values.
     * 
     * @param file
     *            the {@link File} that contains the encoded String definition of the object
     * @param clazz
     *            the class definition to use when converting the object. The object returned will
     *            be of this type. It and its superclasses annotations will be used when converting
     *            the object.
     * 
     * @return the converted object
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    <T> T read(File file, Class<T> clazz) throws CerealException;

    /**
     * Convert the encoded string into cereal-compatible objects and then de-cerealize that into the
     * correct values. Encoded cereal-values that are missing (the name/key is missing) will not
     * override existing values in the target. This should only be used when the values sent only
     * represent the changed values in the original object. This does not propogate though so if a
     * child object is modified at all, the entire object must be provided.
     * 
     * @param string
     *            the encoded string definition of the object
     * @param target
     *            the object on which to store the converted values the object. Its class and its
     *            superclasses annotations will be used when converting.
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void applyFromString(String string, Object target) throws CerealException;

    /**
     * Convert the encoded classpath resource into cereal-compatible objects and then de-cerealize
     * that into the correct values. The path to the resource should be either absolute or relative
     * to {@link CerealEngine}. Encoded cereal-values that are missing (the name/key is missing)
     * will not override existing values in the target. This should only be used when the values
     * sent only represent the changed values in the original object. This does not propogate though
     * so if a child object is modified at all, the entire object must be provided.
     * 
     * @param path
     *            the path to the encoded resource to read
     * @param target
     *            the object on which to store the converted values the object. Its class and its
     *            superclasses annotations will be used when converting.
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void applyFromClasspath(String path, Object target) throws CerealException;

    /**
     * Convert the encoded string read from the given {@link Reader} into cereal-compatible objects
     * and then de-cerealize that into the correct values. Encoded cereal-values that are missing
     * (the name/key is missing) will not override existing values in the target. This should only
     * be used when the values sent only represent the changed values in the original object. This
     * does not propogate though so if a child object is modified at all, the entire object must be
     * provided.
     * 
     * @param reader
     *            the {@link Reader} that contains the encoded String definition of the object
     * @param target
     *            the object on which to store the converted values the object. Its class and its
     *            superclasses annotations will be used when converting.
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void apply(Reader reader, Object target) throws CerealException;

    /**
     * Convert the encoded string read from the given {@link File} into cereal-compatible objects
     * and then de-cerealize that into the correct values. Encoded cereal-values that are missing
     * (the name/key is missing) will not override existing values in the target. This should only
     * be used when the values sent only represent the changed values in the original object. This
     * does not propogate though so if a child object is modified at all, the entire object must be
     * provided.
     * 
     * @param file
     *            the {@link File} that contains the encoded String definition of the object
     * @param target
     *            the object on which to store the converted values the object. Its class and its
     *            superclasses annotations will be used when converting.
     * 
     * @throws CerealException
     *             if there was a problem reading the annotations on the class or the convertion
     *             failed for any reason
     */
    void apply(File file, Object target) throws CerealException;

}
