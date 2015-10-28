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

import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;
import com.comcast.cereal.annotations.CerealClass;
import com.comcast.cereal.engines.JsonCerealEngine;

public class CerealClassTest {

    @Test
    public void testCerealization() throws CerealException {
        JsonCerealEngine engine = new JsonCerealEngine();
        Band[] bands = { new Band("Bela Fleck"), new Band("Alison Krauss") };
        
        String json = engine.writeToString(bands);
        
        assertEquals(json, "[\"Bela Fleck\",\"Alison Krauss\"]");
    }
    
    @Test
    public void testDeCerealization() throws CerealException {
        JsonCerealEngine engine = new JsonCerealEngine();
        Band[] bands = engine.readFromString("[\"Bela Fleck\",\"Alison Krauss\"]", Band[].class);

        assertEquals(bands[0].name, "Bela Fleck");
        assertEquals(bands[1].name, "Alison Krauss");
    }
    
    
    
    @CerealClass(BandCerealizer.class)
    public static class Band {
        public String name;
        
        public Band(String name) {
            this.name = name;
        }
    }
    
    public static class BandCerealizer implements Cerealizer<Band, String> {

        @Override
        public Band deCerealize(String cereal, ObjectCache objectCache) throws CerealException {
            return new Band(cereal);
        }

        @Override
        public String cerealize(Band object, ObjectCache objectCache) throws CerealException {
            return object.name;
        }
        
        
    }
}
