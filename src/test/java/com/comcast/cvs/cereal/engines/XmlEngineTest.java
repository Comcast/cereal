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

import static org.testng.Assert.assertEquals;

import javax.mail.internet.InternetAddress;

import org.testng.annotations.Test;

import com.comcast.cvs.testclasses.Member;

public class XmlEngineTest {

    @Test
    public void testXmlStrings() throws Exception {
        Member expected = new Member();
        expected.firstName = "";
        expected.lastName = "null";
        expected.email = new InternetAddress("_null@company.com");

        XmlCerealEngine xml = new XmlCerealEngine();

        String xmlString = xml.writeToString(expected, Member.class);
        Member actual = xml.readFromString(xmlString, Member.class);

        assertEquals(actual, expected);
    }
}
