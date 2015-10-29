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
package com.comcast.cereal.convert;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * The <i>DefaultCerealizer</i> is a placeholder to indicate that the Cereal framework should choose
 * a Cerealizer based upon the actual attributes of the target/source java type. Every method and
 * constructor will throw a {@link CerealException} because this Cerealizer should never be used at
 * runtime.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class DefaultCerealizer<J, C> implements Cerealizer<J, C> {

    private static final String MESSAGE = "The DefaultCerealizer should never be directly used";

    /**
     * Constructor that throws an exception.
     * 
     * @throws CerealException
     *             <b>always</b>
     */
    public DefaultCerealizer() throws CerealException {
        throw new CerealException(MESSAGE);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public C cerealize(J object, ObjectCache objectCache) throws CerealException {
        throw new CerealException(MESSAGE);
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    public J deCerealize(C cereal, ObjectCache objectCache) throws CerealException {
        throw new CerealException(MESSAGE);
    }
}
