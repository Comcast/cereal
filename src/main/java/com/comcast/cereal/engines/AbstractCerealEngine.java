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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.CerealSettings;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;
import com.comcast.cereal.convert.ClassCerealizer;

/**
 * Provides common functions for most {@link CerealEngine} implementations as most methods build
 * upon each other.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public abstract class AbstractCerealEngine implements CerealEngine {

    private CerealFactory cerealFactory;
    private CerealSettings settings = new CerealSettings();

    /**
     * Constructor for an AbstractCerealEngine that will create and store a new
     * {@link CerealFactory}.
     */
    public AbstractCerealEngine() {
        this.cerealFactory = new CerealFactory();
    }

    /**
     * Actually write the cereal-compatible object to the given writer.
     * 
     * @param writer
     *            the writer to write out to
     * @param cereal
     *            the cereal-compatible object representation that should be written
     * 
     * @throws CerealException
     *             if there was a problem parsing the cereal-compatible object(s) or writing to the
     *             writer
     */
    protected abstract void doWrite(OutputStream outputStream, Object cereal)
            throws CerealException;

    /**
     * Actually write the cereal-compatible object to the given writer.
     * 
     * @param writer
     *            the writer to write out to
     * @param cereal
     *            the cereal-compatible object representation that should be written
     * 
     * @throws CerealException
     *             if there was a problem parsing the cereal-compatible object(s) or writing to the
     *             writer
     */
    protected abstract void doWrite(Writer writer, Object cereal) throws CerealException;

    /**
     * Actually read the cereal-compatible object from the given input stream.
     * 
     * @param inputStream
     *            the stream to read from
     * @return the cereal-compatible object representation of the file that was read
     * 
     * @throws CerealException
     *             if there was a problem reading from the stream or the contents were not formatted
     *             or encoded correctly
     */
    protected abstract Object doRead(InputStream inputStream) throws CerealException;

    /**
     * Actually read the cereal-compatible object from the given reader.
     * 
     * @param reader
     *            the reader to read from
     * @return the cereal-compatible object representation of the file that was read
     * 
     * @throws CerealException
     *             if there was a problem reading from the stream or the contents were not formatted
     *             or encoded correctly
     */
    protected abstract Object doRead(Reader reader) throws CerealException;

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#getCerealFactory()
     */
    public CerealFactory getCerealFactory() {
        return cerealFactory;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#cerealize(java.lang.Object)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object cerealize(Object object) throws CerealException {
        ObjectCache objectCache = new ObjectCache(settings);
        try {
            Cerealizer cerealizer = cerealFactory.getCerealizer(object.getClass());
            Object cereal = cerealizer.cerealize(object, objectCache);
            if (settings.shouldIncludeClassName() && (cereal instanceof Map)) {
                ((Map) cereal).put("--class", object.getClass().getName());
            }
            return cereal;
        } finally {
            objectCache.resetCache();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#cerealize(T, java.lang.Class)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> Object cerealize(T t, Class<T> clazz) throws CerealException {
        ObjectCache objectCache = new ObjectCache(settings);
        try {
            Cerealizer cerealizer = cerealFactory.getCerealizer(clazz);
            Object cereal = cerealizer.cerealize(t, objectCache);
            if (settings.shouldIncludeClassName() && (cereal instanceof Map)) {
                ((Map) cereal).put("--class", t.getClass().getName());
            }
            return cereal;
        } finally {
            objectCache.resetCache();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#deCerealize(java.util.HashMap,
     * java.lang.Class)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T deCerealize(Object cereal, Class<T> clazz) throws CerealException {
        ObjectCache objectCache = new ObjectCache(settings);
        try {
            if (cereal instanceof List && clazz.isArray()) {
                List<Object> cerealList = (List) cereal;
                Class arrayType = clazz.getComponentType();
                Cerealizer cerealizer = cerealFactory.getCerealizer(arrayType);
                
                T array = (T) Array.newInstance(arrayType, cerealList.size());
                for (int i = 0; i < cerealList.size(); i++) {
                    Array.set(array, i, cerealizer.deCerealize(cerealList.get(i), objectCache));
                }
                
                return array;
            } else {
                Class<?> runtimeClass = cerealFactory.getRuntimeClass(cereal);
                Cerealizer cerealizer = cerealFactory.getCerealizer(clazz);
                
                if ((runtimeClass != null) && clazz.isAssignableFrom(runtimeClass)) {
                    /** need to check if the runtime class is a subclass of the given class
                     *  or we will get a class cast exception when we return it */
                    cerealizer = cerealFactory.getRuntimeCerealizer(cereal, cerealizer);
                }
                return (T) cerealizer.deCerealize(cereal, objectCache);
            }
        } finally {
            objectCache.resetCache();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#apply(java.util.Map, java.lang.Object)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void apply(Map<String, Object> cereal, Object target) throws CerealException {
        ObjectCache objectCache = new ObjectCache(settings);
        try {
            Class<?> clazz = target.getClass();
            Cerealizer cerealizer = cerealFactory.getCerealizer(clazz);
            if (cerealizer instanceof ClassCerealizer) {
                ClassCerealizer classCerealizer = (ClassCerealizer) cerealizer;
                classCerealizer.applyCereal(cereal, target, true, objectCache);
            } else {
                throw new CerealException("Cannot apply cereal for type \"" + clazz.getName()
                        + "\" with a Cerealizer that is not a ClassCerealizer: \""
                        + cerealizer.getClass().getName() + "\".");
            }
        } finally {
            objectCache.resetCache();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#writeToString(java.lang.Object)
     */
    public String writeToString(Object object) throws CerealException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Object cereal = cerealize(object);
            doWrite(baos, cereal);
            return baos.toString();
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#writeToString(java.lang.Object,
     * java.lang.Class)
     */
    public <T> String writeToString(T t, Class<T> clazz) throws CerealException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Object cereal = cerealize(t, clazz);
            doWrite(baos, cereal);
            return baos.toString();
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#write(java.lang.Object, java.lang.Class,
     * java.io.Writer)
     */
    public <T> void write(T t, Class<T> clazz, Writer writer) throws CerealException {
        doWrite(writer, cerealize(t, clazz));
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#write(java.lang.Object, java.io.Writer)
     */
    public void write(Object object, Writer writer) throws CerealException {
        doWrite(writer, cerealize(object));
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#write(T, java.lang.Class, java.io.File)
     */
    public <T> void write(T t, Class<T> clazz, File file) throws CerealException {
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (IOException ioex) {
            throw new CerealException("Failed to open the file " + file.getAbsolutePath()
                    + " for writing", ioex);
        }

        try {
            Object cereal = cerealize(t, clazz);
            doWrite(outputStream, cereal);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#write(java.lang.Object, java.io.File)
     */
    public void write(Object object, File file) throws CerealException {
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (IOException ioex) {
            throw new CerealException("Failed to open the file " + file.getAbsolutePath()
                    + " for writing", ioex);
        }

        try {
            Object cereal = cerealize(object);
            doWrite(outputStream, cereal);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#readFromString(java.lang.String,
     * java.lang.Class)
     */
    public <T> T readFromString(String string, Class<T> clazz) throws CerealException {
        ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes());
        try {
            Object cereal = doRead(bais);
            return deCerealize(cereal, clazz);
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#readFromClasspath(java.lang.String, java.lang.Class)
     */
    public <T> T readFromClasspath(String path, Class<T> clazz) throws CerealException {
        InputStream in = CerealEngine.class.getResourceAsStream(path);
        try {
            Object cereal = doRead(in);
            return deCerealize(cereal, clazz);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#read(java.io.Reader, java.lang.Class)
     */
    public <T> T read(Reader reader, Class<T> clazz) throws CerealException {
        Object cereal = doRead(reader);
        return deCerealize(cereal, clazz);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#read(java.io.File, java.lang.Class)
     */
    public <T> T read(File file, Class<T> clazz) throws CerealException {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (IOException ioex) {
            throw new CerealException("Failed to open the file " + file.getAbsolutePath()
                    + " for reading", ioex);
        }

        try {
            Object cereal = doRead(inputStream);
            return deCerealize(cereal, clazz);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#applyFromString(java.lang.String,
     * java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void applyFromString(String string, Object target) throws CerealException {
        ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes());
        try {
            Object cereal = doRead(bais);
            if (cereal instanceof Map) {
                apply((Map<String, Object>) cereal, target);
            } else {
                throw new CerealException("CerealEngine.apply* methods can only accept objects.");
            }
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#applyFromClasspath(java.lang.String,
     * java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void applyFromClasspath(String path, Object target) throws CerealException {
        InputStream in = CerealEngine.class.getResourceAsStream(path);
        try {
            Object cereal = doRead(in);
            if (cereal instanceof Map) {
                apply((Map<String, Object>) cereal, target);
            } else {
                throw new CerealException("CerealEngine.apply* methods can only accept objects.");
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#apply(java.io.Reader, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void apply(Reader reader, Object target) throws CerealException {
        Object cereal = doRead(reader);
        if (cereal instanceof Map) {
            apply((Map<String, Object>) cereal, target);
        } else {
            throw new CerealException("CerealEngine.apply* methods can only accept objects.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.CerealEngine#apply(java.io.File, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void apply(File file, Object target) throws CerealException {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (IOException ioex) {
            throw new CerealException("Failed to open the file " + file.getAbsolutePath()
                    + " for reading", ioex);
        }

        try {
            Object cereal = doRead(inputStream);
            if (cereal instanceof Map) {
                apply((Map<String, Object>) cereal, target);
            } else {
                throw new CerealException("CerealEngine.apply* methods can only accept objects.");
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public CerealSettings getSettings() {
        return settings;
    }

    public void setSettings(CerealSettings settings) {
        this.settings = settings;
    }
    
    /**
     * Tells this engine to use the given cerealize when cerealizing the given class
     * @param clazz The class that the cerealizer is for
     * @param cerealizer The cerealizer to use for the given class
     */
    public <T> void addCerealizer(Class<T> clazz, Cerealizer<T, ?> cerealizer) {
    	this.cerealFactory.addCerealizer(clazz, cerealizer);
    }
}
