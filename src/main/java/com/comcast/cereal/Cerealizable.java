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

/**
 * A class that implements the {@link Cerealizable} interface provides it's own methodology for
 * converting to and from cereal-compatible objects. When deceralizing, the object will be
 * constructed and then {@link #applyCereal(Object)} will be invoked. This allows for the method to
 * be provided on the instance instead of a static "get" method.
 * <p>
 * If this object has embedded types that should be handled by cereal, this class should also
 * implement {@link CerealFactoryAware}.
 * </p>
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface Cerealizable<C> {

    /**
     * Convert <code>this</code> object into cereal.
     * 
     * @return the cereal representing this object
     * 
     * @throws CerealException
     *             if the object cannot be converted into cereal
     */
    C toCereal() throws CerealException;

    /**
     * Apply the given cereal to this newly created object.
     * 
     * @param cereal
     *            the cereal representation of this object
     * 
     * @throws CerealException
     *             if the cereal cannot be applied
     */
    void applyCereal(C cereal) throws CerealException;
}
