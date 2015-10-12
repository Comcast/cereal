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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealSettings;
import com.comcast.cvs.testclasses.Dog;
import com.comcast.cvs.testclasses.DogMap;
import com.comcast.cvs.testclasses.ExtendsMap;
import com.comcast.cvs.testclasses.Member;
import com.comcast.cvs.testclasses.Title;

public class MapTest {
	
	public class MappedMember {

		public Map<String, List<Member>> map = new HashMap<String, List<Member>>();
	}

	@Test
	public void testMapOfList() throws AddressException, CerealException {
		MappedMember mem = new MappedMember();
		List<Member> members = new ArrayList<Member>();
		Member member = new Member();
		member.firstName = "kevin";
		member.lastName = "pearson";
		member.title = Title.DIRECTOR;
		member.email = new InternetAddress("kevin_pearson@cable.comcast.com");
		
		members.add(member);
		mem.map.put("kp", members);
		
		JsonCerealEngine engine = new JsonCerealEngine(true);
		
		String cereal = engine.writeToString(mem);
		
		MappedMember newMem = engine.readFromString(cereal, MappedMember.class);
		Assert.assertEquals(newMem.map.size(), mem.map.size());
		Assert.assertTrue(newMem.map.containsKey("kp"));
		List<Member> newMembers = newMem.map.get("kp");
		Assert.assertEquals(newMembers.size(), 1);
		Assert.assertEquals(newMembers.get(0).firstName, "kevin");
	}
	
	@DataProvider(name="testCerealizeMapData")
	public Object[][] testCerealizeMapData() {
	    return new Object[][] {
	            { new HashMap<String, String>() },
	            { new TreeMap<String, String>() }
	    };
	}
	
	@Test(dataProvider="testCerealizeMapData")
	public void testCerealizeMap(Map<String, String> map) throws CerealException {
	    map.put("kevin", "pearson");
	    map.put("clark", "malmgren");
	    
	    JsonCerealEngine engine = new JsonCerealEngine();
	    String cereal = engine.writeToString(map);
	    
	    Map newMap = engine.readFromString(cereal, Map.class);
	    Assert.assertTrue(newMap instanceof Map);
	    Assert.assertEquals(newMap.size(), 2);
        Assert.assertEquals(newMap.get("kevin"), "pearson");
        Assert.assertEquals(newMap.get("clark"), "malmgren");
	}
	
	
	@Test
	public void testCerealizeObjectAsAMap() throws CerealException {
	    Dog nova = new Dog("Black Lab");
	    JsonCerealEngine engine = new JsonCerealEngine();
	    String cereal = engine.writeToString(nova);
	    Map<?,?> map = engine.readFromString(cereal, Map.class);
        Assert.assertNotNull(map);
        Assert.assertTrue(map instanceof Map);
        Assert.assertEquals(map.get("breed"), "Black Lab");
	    
	}
	
	@Test
    public void testCerealizeMapWithObjectValues() throws CerealException {
        Dog nova = new Dog("Black Lab");
        Dog jax = new Dog("Yellow Lab");
        
        DogMap dogMap = new DogMap();
        Map<String, Dog> map = new HashMap<String, Dog>();
        map.put("nova", nova);
        map.put("jax", jax);
        Map<String, Integer> counts = new HashMap<String, Integer>();
        counts.put("lab", 2);
        counts.put("poodle", 0);

        dogMap.setDogsWithoutType(new HashMap<String, Dog>(map));
        dogMap.setDogs(map);
        dogMap.setCounts(counts);
        CerealSettings settings = new CerealSettings();
        settings.setIncludeClassName(false);
        settings.setUseObjectReferences(false);
        JsonCerealEngine engine = new JsonCerealEngine();
        engine.setSettings(settings);
        
        String json = engine.writeToString(dogMap);
        DogMap dogMap2 = engine.readFromString(json, DogMap.class);
        Assert.assertEquals(dogMap2.getDogs().get("nova").getBreed(), "Black Lab");
        Assert.assertEquals(dogMap2.getDogs().get("jax").getBreed(), "Yellow Lab");
        Assert.assertEquals(dogMap2.getDogsWithoutType().get("nova").getBreed(), "Black Lab");
        Assert.assertEquals(dogMap2.getDogsWithoutType().get("jax").getBreed(), "Yellow Lab");
        Assert.assertEquals(dogMap2.getCounts().get("lab").intValue(), 2);
        Assert.assertEquals(dogMap2.getCounts().get("poodle").intValue(), 0);
    }
	
	@Test
	public void testExtendsMap() throws CerealException {
	    ExtendsMap map = new ExtendsMap();
	    map.put("key", "val");
        JsonCerealEngine engine = new JsonCerealEngine();
        String json = engine.writeToString(map);
        ExtendsMap newMap = engine.readFromString(json, ExtendsMap.class);
        Assert.assertEquals(newMap.getClass(), ExtendsMap.class);
        Assert.assertEquals(newMap, map);
	}
}
