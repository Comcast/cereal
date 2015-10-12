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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealSettings;

public class CollectionTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testCerealizeCollection() throws CerealException {
        Collection<String> coll = new ArrayList<String>();
        coll.add("kp");
        coll.add("is");
        coll.add("awesome");
        
        JsonCerealEngine engine = new JsonCerealEngine();
        String cereal = engine.writeToString(coll);
        Collection<String> newColl = engine.readFromString(cereal, ArrayList.class);
        Assert.assertTrue(newColl.contains("kp"));
        Assert.assertTrue(newColl.contains("is"));
        Assert.assertTrue(newColl.contains("awesome"));
    }
    
    @Test
    public void testCerealizeArraysArrayList() throws CerealException {
    	Collection<String> coll = Arrays.asList("kp", "is", "awesome");

        JsonCerealEngine engine = new JsonCerealEngine();
        CerealSettings settings = new CerealSettings();
        settings.setIncludeClassName(false);
        engine.setSettings(settings);
        String cereal = engine.writeToString(coll);
    	Assert.assertEquals(cereal, "[\"kp\",\"is\",\"awesome\"]");		
    }
}