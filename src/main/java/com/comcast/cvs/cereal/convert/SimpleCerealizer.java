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

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.Cerealizer;
import com.comcast.cvs.cereal.ObjectCache;

/**
 * A simple Cerealizer to use when for the base cereal-supported java types. This is a simple
 * pass-through since no conversion needs to take place.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class SimpleCerealizer implements Cerealizer<Object, Object> {

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    public Object deCerealize(Object cereal, ObjectCache objectCache) throws CerealException {
        return cereal;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cvs.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public Object cerealize(Object object, ObjectCache objectCache) throws CerealException {
        return object;
    }

}
