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
package com.comcast.cvs.testclasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.comcast.cereal.annotations.Cereal;

public class AnimalShelter {
	@Cereal(defaultObjectClass=ArrayList.class, type=Animal.class)
    private Collection<Animal> animals = new ArrayList<Animal>();
	@Cereal(defaultObjectClass=HashMap.class, type=Animal.class)
    private Map<String, Animal> animalMap = new HashMap<String, Animal>();
    private Animal lastAdopted;

    public Collection<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(Collection<Animal> animals) {
        this.animals = animals;
    }
    
    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public Animal getLastAdopted() {
        return lastAdopted;
    }

    public void setLastAdopted(Animal lastAdopted) {
        this.lastAdopted = lastAdopted;
    }

	public Map<String, Animal> getAnimalMap() {
		return animalMap;
	}

	public void setAnimalMap(Map<String, Animal> animalMap) {
		this.animalMap = animalMap;
	}
    
}
