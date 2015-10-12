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

import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.testclasses.Animal;
import com.comcast.cvs.testclasses.AnimalShelter;
import com.comcast.cvs.testclasses.Cat;
import com.comcast.cvs.testclasses.Dog;

public class InterfaceCollectionTest extends CerealEngineTest {
    
    @Test(dataProvider="engineData")
    public void testCerealize(CerealEngine engine) throws CerealException {
        AnimalShelter shelter = new AnimalShelter();
        shelter.addAnimal(new Cat(true));
        shelter.addAnimal(new Dog("Black Lab"));
        shelter.setLastAdopted(new Dog("Bulldog"));
        
        String cereal = engine.writeToString(shelter, AnimalShelter.class);
        AnimalShelter as = engine.readFromString(cereal, AnimalShelter.class);
        Collection<Animal> animals = as.getAnimals();
        Assert.assertEquals(animals.size(), 2);
        Dog dog = null;
        Cat cat = null;
        for (Animal animal : animals) {
            if (animal instanceof Dog) {
                dog = (Dog) animal;
            } else if (animal instanceof Cat) {
                cat = (Cat) animal;
            } else {
                Assert.fail("Contained unknown animal type " + animal.getClass());
            }
        }
        Assert.assertNotNull(dog);
        Assert.assertEquals("Black Lab", dog.getBreed());
        Assert.assertEquals("woof", dog.speak());
        
        Assert.assertNotNull(cat);
        Assert.assertEquals(true, cat.isIndoor());
        Assert.assertEquals("meow", cat.speak());
        
        Animal lastAdopted = as.getLastAdopted();
        Assert.assertNotNull(lastAdopted);
        Assert.assertTrue(lastAdopted instanceof Dog);
        Assert.assertEquals("Bulldog", ((Dog)lastAdopted).getBreed());
    }
}
