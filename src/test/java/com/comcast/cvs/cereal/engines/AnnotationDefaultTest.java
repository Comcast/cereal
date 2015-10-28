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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.cvs.testclasses.Animal;
import com.comcast.cvs.testclasses.AnimalShelter;
import com.comcast.cvs.testclasses.AnimalWrapper;
import com.comcast.cvs.testclasses.Cat;

public class AnnotationDefaultTest {

	@Test
	public void testDefaultObjectClass() throws CerealException {
		String json = "{\"lastAdopted\":{\"--class\":\"com.comcast.cvs.testclasses.Cat\",\"indoor\":false}}";
		JsonCerealEngine engine = new JsonCerealEngine();
		AnimalShelter shelter = engine.readFromString(json, AnimalShelter.class);
		Animal la = shelter.getLastAdopted();
		Assert.assertTrue(la instanceof Cat);	
		Assert.assertNotNull(shelter.getAnimals());
		Assert.assertTrue(shelter.getAnimals() instanceof ArrayList);
		Assert.assertTrue(shelter.getAnimals().isEmpty());	
		Assert.assertNotNull(shelter.getAnimalMap());
		Assert.assertTrue(shelter.getAnimalMap() instanceof HashMap);
		Assert.assertTrue(shelter.getAnimalMap().isEmpty());
	}

	@Test
	public void testDefaultObjectClassNotUsed() throws CerealException {
		String catJson = "{\"--class\":\"com.comcast.cvs.testclasses.Cat\",\"indoor\":false}";
		String animalsJson = "[{\"--class\":\"com.comcast.cvs.testclasses.Cat\",\"indoor\":true}]";
		String json = "{\"lastAdopted\":" + catJson + ",\"animals\":" + animalsJson + "}";
		JsonCerealEngine engine = new JsonCerealEngine();
		AnimalShelter shelter = engine.readFromString(json, AnimalShelter.class);
		Animal la = shelter.getLastAdopted();
		Assert.assertTrue(la instanceof Cat);	
		Assert.assertNotNull(shelter.getAnimals());
		Assert.assertTrue(shelter.getAnimals() instanceof ArrayList);
		Assert.assertFalse(shelter.getAnimals().isEmpty());
	}
	
	@Test(expectedExceptions=CerealException.class)
	public void testDefaultObjectClassInvalid() throws CerealException {
		String json = "{}";
		JsonCerealEngine engine = new JsonCerealEngine();
		engine.readFromString(json, AnimalWrapper.class);
	}
}
