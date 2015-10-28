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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizable;
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.cvs.testclasses.Name;
import com.comcast.pantry.test.TestList;

/**
 * Tests the utilities required for converting classes that implement the {@link Cerealizable}
 * interface.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class CerealizableTest {

    @DataProvider(name = "names")
    public TestList getNames() {
        TestList list = new TestList();

        list.add("Clark", "Malmgren");
        list.add("Apple", "Banana");

        return list;
    }

    @Test(dataProvider = "names")
    public void testNames(String first, String last) throws CerealException {
        Name expected = new Name(first, last);

        JsonCerealEngine engine = new JsonCerealEngine();
        String json = engine.writeToString(expected);
        Name actual = engine.readFromString(json, Name.class);

        assertEquals(actual, expected);
    }

    @Test()
    public void testNamesArray() throws CerealException {
        Name[] names = new Name[5];
        names[0] = new Name("Nick", "Carter");
        names[1] = new Name("AJ", "McLean");
        names[2] = new Name("Brian", "Littrell");
        names[3] = new Name("Howie", "Dorough");
        names[4] = new Name("Kevin", "Richardson");

        JsonCerealEngine engine = new JsonCerealEngine();
        String json = engine.writeToString(names);
        Name[] actual = engine.readFromString(json, Name[].class);

        assertEquals(actual, names);
    }
}
