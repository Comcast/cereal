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
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.testclasses.PrimitiveHolder;
import com.comcast.pantry.test.TestList;

public class PrimitiveAsStringsTest {
    private JsonCerealEngine engine = new JsonCerealEngine();

    @Test
    public void testAllPrimitives() throws CerealException {
        Map<String, Object> cereal = new HashMap<String, Object>();
        cereal.put("i", Arrays.asList("1"));
        cereal.put("b", Arrays.asList("false"));
        cereal.put("l", Arrays.asList("3"));
        cereal.put("s", Arrays.asList("5"));
        cereal.put("c", Arrays.asList("h"));
        cereal.put("f", Arrays.asList("2.2"));
        cereal.put("d", Arrays.asList("6.77"));
        
        JsonCerealEngine engine = new JsonCerealEngine();
        PrimitiveHolder ph = engine.deCerealize(cereal, PrimitiveHolder.class);
        assertVal(ph.getI(), Integer.class, 1);
        assertVal(ph.getB(), Boolean.class, false);
        assertVal(ph.getL(), Long.class, 3L);
        assertVal(ph.getS(), Short.class, (short) 5);
        assertVal(ph.getC(), Character.class, 'h');
        assertVal(ph.getF(), Float.class, 2.2f);
        assertVal(ph.getD(), Double.class, 6.77);
    }
    
    @DataProvider(name="testPrimitiveCastData")
    public TestList testPrimitiveCastData() {
        TestList tests = new TestList();
        tests.add(4.5, Integer.class, 4);
        tests.add(5, Boolean.class, true);
        tests.add(0, Boolean.class, false);
        tests.add(5, Long.class, 5l);
        tests.add(6l, Short.class, (short) 6);
        tests.add((byte)('k'), Character.class, 'k');
        tests.add(7, Float.class, 7.0f);
        tests.add(8, Double.class, 8.0d);
        tests.add(null, Integer.class, null);
        return tests;
    }
    
    @Test(dataProvider="testPrimitiveCastData")
    public <T> void testPrimitiveCast(Object cereal, Class<T> clazz, T expected) throws CerealException {
        T actual = engine.deCerealize(cereal, clazz);
        assertVal(actual, clazz, expected);
    }
    
    private <T> void assertVal(Collection<T> coll, Class<T> clazz, T val) {
        assertVal(coll.iterator().next(), clazz, val);
    }
    
    private <T> void assertVal(T obj, Class<T> clazz, T val) {
        if (obj == null) {
            assertNull(val);
        } else {
            assertEquals(obj.getClass(), clazz);
            assertEquals(obj, val);
        }
    }
}
