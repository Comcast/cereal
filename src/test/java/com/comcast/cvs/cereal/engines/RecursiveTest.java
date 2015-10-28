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

import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.JsonCerealEngine;

public class RecursiveTest {

    @Test
    public void testRecursiveReferences() throws CerealException {
        MyObject object = new MyObject();
        object.source = object;
        object.inner = object;
        
        JsonCerealEngine engine = new JsonCerealEngine();
        String json = engine.writeToString(object);
        
        MyObject converted = engine.readFromString(json, MyObject.class);

        assertSame(converted.source, converted);
        assertSame(converted.inner, converted);
    }
    
    public static class MyObject {
        public Object source;
        public MyObject inner;
    }
}
