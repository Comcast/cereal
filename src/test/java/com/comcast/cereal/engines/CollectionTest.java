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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealSettings;
import com.comcast.testclasses.StringWrapper;
import com.comcast.testclasses.StringWrapperContainer;

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
    
    @Test
    public void testCollectionContainingObjectAnnotatedWithCerealClass() throws CerealException {
    	StringWrapperContainer container = new StringWrapperContainer();
    	container.setWrappers(Arrays.asList(new StringWrapper("kp"), new StringWrapper(null)));
    	Map<String, Set<StringWrapper>> wrapperMap = new HashMap<String, Set<StringWrapper>>();
    	wrapperMap.put("1", Sets.newHashSet(Arrays.asList(new StringWrapper("mp"))));
    	Map<String, Set<String>> wrapperMap2 = new HashMap<String, Set<String>>();
    	wrapperMap2.put("y", Sets.newHashSet(Arrays.asList("x")));
    	container.setWrapperMap(wrapperMap);
    	container.setWrapperMapNoSubtype(wrapperMap2);
    	
    	JsonCerealEngine engine = new JsonCerealEngine();
        CerealSettings settings = new CerealSettings();
        settings.setIncludeClassName(false);
        engine.setSettings(settings);
        String cereal = engine.writeToString(container);
        StringWrapperContainer decereal = engine.readFromString(cereal, StringWrapperContainer.class);
        Collection<StringWrapper> wrappers = new ArrayList<StringWrapper>(decereal.getWrappers());
        wrappers.addAll(decereal.getWrapperMap().get("1"));
        Assert.assertEquals(wrappers.size(), 3);
        for (Object obj : wrappers) {
        	Assert.assertEquals(obj.getClass(), StringWrapper.class);
        }
        Assert.assertEquals(decereal, container);
    }
}