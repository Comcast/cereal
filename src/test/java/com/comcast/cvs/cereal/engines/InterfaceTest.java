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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.CerealEngine;
import com.comcast.cvs.testclasses.Animal;
import com.comcast.cvs.testclasses.Dog;

public class InterfaceTest extends CerealEngineTest {

    @Test(dataProvider="engineData")
    public void testCerealizeInterface(CerealEngine engine) throws CerealException {
        Animal animal = new Dog("Black Lab");
        String cereal = engine.writeToString(animal);
        
        Animal newAnimal = engine.readFromString(cereal, Animal.class);
        Assert.assertNotNull(newAnimal);
        Assert.assertTrue(animal instanceof Dog);
        Assert.assertEquals("Black Lab", ((Dog) animal).getBreed());
    }
}
