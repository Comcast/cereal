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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.testclasses.Employee;

public class JsonEngineWithMethodTest {

    @Test
    public void testMethodConversion() throws CerealException {
        Employee a = new Employee();
        a.firstName = "Clark";
        a.lastName = "Malmgren";
        a.startYear = 2006;

        JsonCerealEngine json = new JsonCerealEngine();
        String jsonString = json.writeToString(a, Employee.class);
        Employee b = json.readFromString(jsonString, Employee.class);

        assertEquals(b.firstName, "Clark");
        assertEquals(b.lastName, "Malmgren");
        assertEquals(b.startYear, 2006);
    }
}
