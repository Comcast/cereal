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
/**
 * 
 */
package com.comcast.cereal.engines;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.AUTO;
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.annotations.Cereal;
import com.comcast.cereal.annotations.CerealObject;

/**
 * A <i>YamlCerealEngine</i> is capable of converting between YAML and Java objects.
 * 
 * @see Cereal
 * @see CerealObject
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class YamlCerealEngine extends AbstractCerealEngine {

    private Yaml yaml;

    /**
     * Create a new YamlCerealEngine object without block output and an indent width of 2. Block
     * output is easier to read, but will potentially take up more data.
     */
    public YamlCerealEngine() {
        this(false, 2);
    }

    /**
     * Create a new YamlCerealEngine object with an indent width of 2 and the requested output flow
     * style.
     * 
     * @param useBlockFlow
     *            if <code>true</code>, any output YAML will use block flow which is easier to read,
     *            but potentially can take up more data
     */
    public YamlCerealEngine(boolean useBlockFlow) {
        this(useBlockFlow, 2);
    }

    /**
     * Create a new YamlCerealEngine object with the requested output flow style and indent width.
     * 
     * @param useBlockFlow
     *            if <code>true</code>, any output YAML will use block flow which is easier to read,
     *            but potentially can take up more data
     * @param indentWidth
     *            the indent width
     */
    public YamlCerealEngine(boolean useBlockFlow, int indentWidth) {
        DumperOptions options = new DumperOptions();
        options.setWidth(100);
        options.setIndent(indentWidth);
        options.setDefaultFlowStyle(useBlockFlow ? BLOCK : AUTO);

        this.yaml = new Yaml(options);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doWrite(java.io.OutputStream,
     * java.lang.Object)
     */
    protected void doWrite(OutputStream outputStream, Object cereal) throws CerealException {
        yaml.dump(cereal, new OutputStreamWriter(outputStream));
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doWrite(java.io.Writer,
     * java.lang.Object)
     */
    protected void doWrite(Writer writer, Object cereal) throws CerealException {
        yaml.dump(cereal, writer);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doRead(java.io.InputStream)
     */
    protected Object doRead(InputStream inputStream) throws CerealException {
        return yaml.load(inputStream);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.engines.AbstractCerealEngine#doRead(java.io.Reader)
     */
    protected Object doRead(Reader reader) throws CerealException {
        return yaml.load(reader);
    }
}
