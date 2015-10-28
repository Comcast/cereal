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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

public class EmailCerealizer implements Cerealizer<InternetAddress, String> {

    public String cerealize(InternetAddress object, ObjectCache objectCache) throws CerealException {
        return object.getAddress();
    }

    public InternetAddress deCerealize(String cereal, ObjectCache objectCache) throws CerealException {
        try {
            return new InternetAddress(cereal);
        } catch (AddressException aex) {
            throw new CerealException("Bad Email Address: " + cereal, aex);
        }
    }

}
