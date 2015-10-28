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
package com.comcast.cereal;

import java.util.List;
import java.util.Map;

/**
 * Interface for converting to and from serializable base elements supported by the cereal
 * libraries.
 * 
 * <p>
 * The base elements supported by Cereal are as follows:
 * </p>
 * 
 * <ol>
 * <li>{@link Map}</li>
 * <li>{@link List}</li>
 * <li>{@link String}</li>
 * <li>{@link Number}</li>
 * <li>{@link Boolean}</li>
 * <li><code>null</code></li>
 * </ol>
 * 
 * @param <J>
 *            the java object type
 * @param <C>
 *            the cereal-compatible object type (see table for available types)
 * 
 * @author Clark Malmgren
 */
public interface Cerealizer<J, C> {

    /**
     * Convert the given natively supported cereal object to the appropriate java object.
     * 
     * @param cereal
     *            a cereal-compatible object
     * 
     * @return the converted java object
     * 
     * @throws CerealException
     *             if the conversion fails
     */
    J deCerealize(C cereal, ObjectCache objectCache) throws CerealException;

    /**
     * Convert the given java object to the appropriate cereal-compatible object.
     * 
     * @param object
     *            a java object
     * 
     * @return the converted cereal-compatible object
     * 
     * @throws CerealException
     *             if the conversion fails
     */
    C cerealize(J object, ObjectCache objectCache) throws CerealException;
}
