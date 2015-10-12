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
package com.comcast.cvs.cereal.engines;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.annotations.Cereal;
import com.comcast.cvs.cereal.annotations.CerealObject;
import com.comcast.cvs.cereal.impl.JsonHelper;

/**
 * A <i>JsonCerealEngine</i> is capable of converting between JSON and Java objects.
 * 
 * @see Cereal
 * @see CerealObject
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class JsonCerealEngine extends AbstractCerealEngine {
    private boolean prettyPrint;

    /**
     * Create a new JsonCerealEngine object without pretty print.
     */
    public JsonCerealEngine() {
        this(false);
    }

    /**
     * Create a new JsonCerealEngine object and turn on or off the pretty print.
     * 
     * @param prettyPrint
     *            If <code>true</code>, this will output all JSON messages with nice indentations.
     *            If <code>false</code>, whitespace will be minimized.
     */
    public JsonCerealEngine(boolean prettyPrint) {
        super();
        this.prettyPrint = prettyPrint;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doRead(java.io.InputStream)
     */
    protected Object doRead(InputStream inputStream) throws CerealException {
        Reader reader = new InputStreamReader(inputStream);
        try {
            return doRead(reader);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doRead(java.io.Reader)
     */
    protected Object doRead(Reader reader) throws CerealException {
        JsonHelper helper = new JsonHelper();
        return helper.read(reader);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doWrite(java.io.OutputStream,
     * java.lang.Object)
     */
    protected void doWrite(OutputStream outputStream, Object cereal) throws CerealException {
        Writer writer = new OutputStreamWriter(outputStream);
        try {
            doWrite(writer, cereal);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doWrite(java.io.Writer,
     * java.lang.Object)
     */
    protected void doWrite(Writer writer, Object cereal) throws CerealException {
        try {
            JsonHelper helper = new JsonHelper();
            helper.write(writer, cereal, prettyPrint);
        } catch (Exception ex) {
            throw new CerealException("Failed while writing to JSON", ex);
        }
    }
}
