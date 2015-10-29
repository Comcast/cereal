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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * Cerealizer for converting {@link Date} objects to and from Strings following the ISO8601 date
 * format.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class DateCerealizer implements Cerealizer<Date, String> {

    /**
     * A formatter that combines a full date and time, separated by a 'T' (
     * <code>yyyy-MM-dd'T'HH:mm:ss.SSSZ</code>). The time zone offset is 'Z' for zero, and of the
     * form '<code>(+|-)HH:mm</code>' for non-zero.
     * 
     * @see ISODateTimeFormat#dateTime()
     */
    public static final DateTimeFormatter ISO8601 = ISODateTimeFormat.dateTime();

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public String cerealize(Date date, ObjectCache objectCache) throws CerealException {
        if (null == date) {
            return null;
        } else {
            return ISO8601.print(new DateTime(date));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    public Date deCerealize(String cereal, ObjectCache objectCache) throws CerealException {
        if (null == cereal) {
            return null;
        } else {
            return ISO8601.parseDateTime(cereal).toDate();
        }
    }

}
