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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.impl.XmlHelper;

/**
 * The <i>XmlCerealEngine</i> is for converting between XML and Java Objects.
 * 
 * <p>
 * Unfortunately, XML is less clear on structure (String vs. Number) and (List vs. Object) than
 * other languages like JSON. As a result, some assumptions are made when decoding an XML document.
 * 
 * <ol>
 * <li>If a portion of text is exactly <code>null</code> it will return null.</li>
 * <li>If a portion of text can be parsed as a boolean it will be treated as a boolean.</li>
 * <li>If a portion of text can be parsed as a number it will be treated as a number.</li>
 * <li>If an element has only one child element, it will default to an Object (i.e. {@link Map}).</li>
 * <li>If an element has more than one child element, it will be treated as a list if and only if
 * the first two elements have the same tag name.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Since these rules are not necessarily reflective, additional type encoding will be placed upon
 * the XML elements during writing. This will add the attribute <code>_t_="s"</code> to elements
 * that are strings and the attribute <code>_t_="l"</code> to elements that are lists.
 * </p>
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class XmlCerealEngine extends AbstractCerealEngine {

    private XmlHelper helper;

    /**
     * Create a new XmlCerealEngine object without indentation.
     */
    public XmlCerealEngine() {
        this(false);
    }

    /**
     * Create a new XmlCerealEngine object and turn on or off the pretty print.
     * 
     * @param prettyPrint
     *            If <code>true</code>, this will output all XML messages with nice indentations. If
     *            <code>false</code>, whitespace will be minimized.
     */
    public XmlCerealEngine(boolean prettyPrint) {
        this(prettyPrint, "data");
    }

    /**
     * Create a new XmlCerealEngine object with a specific name for the root element and turn on or
     * off the pretty print.
     * 
     * @param prettyPrint
     *            If <code>true</code>, this will output all XML messages with nice indentations. If
     *            <code>false</code>, whitespace will be minimized.
     * @param name
     *            the name of the root element
     */
    public XmlCerealEngine(boolean prettyPrint, String name) {
        super();
        this.helper = new XmlHelper(prettyPrint, name);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.AbstractCerealEngine#doWrite(java.io.OutputStream,
     * java.lang.Object)
     */
    protected void doWrite(OutputStream outputStream, Object cereal) throws CerealException {
        doWrite(new OutputStreamWriter(outputStream), cereal);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.AbstractCerealEngine#doWrite(java.io.Writer,
     * java.lang.Object)
     */
    protected void doWrite(Writer writer, Object cereal) throws CerealException {
        try {
            helper.write(cereal, writer);
        } catch (Exception ex) {
            throw new CerealException("Failed while writing to XML", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.AbstractCerealEngine#doRead(java.io.InputStream)
     */
    protected Object doRead(InputStream inputStream) throws CerealException {
        try {
            return helper.read(inputStream);
        } catch (Exception ex) {
            throw new CerealException("Failed while reading XML", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.engines.AbstractCerealEngine#doRead(java.io.Reader)
     */
    protected Object doRead(Reader reader) throws CerealException {
        return doRead(new ReaderInputStream(reader));
    }
}
