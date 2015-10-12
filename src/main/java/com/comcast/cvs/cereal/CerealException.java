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
package com.comcast.cvs.cereal;

/**
 * A <i>CerealException</i> indicates a failure that occured while trying to serialize or
 * deserialize objects within the entire Cereal framework.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
@SuppressWarnings("serial")
public class CerealException extends Exception {

    /**
     * Construct a new CerealException with a useful message.
     * 
     * @param message
     *            a reason or message associated with this failure
     */
    public CerealException(String message) {
        super(message);
    }

    /**
     * Construct a new CerealException with a useful message and the causing throwable.
     * 
     * @param message
     *            a reason or message associated with this failure
     * @param cause
     *            the throwable that caused this exception to be created
     */
    public CerealException(String message, Throwable cause) {
        super(message, cause);
    }
}
