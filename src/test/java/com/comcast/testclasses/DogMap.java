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
package com.comcast.testclasses;

import java.util.HashMap;
import java.util.Map;

import com.comcast.cereal.annotations.Cereal;

public class DogMap {
	@Cereal(type=Dog.class)
    private Map<String, Dog> dogs = new HashMap<String, Dog>();
    private Map<String, Dog> dogsWithoutType = new HashMap<String, Dog>();
	private Map<String, Integer> counts = new HashMap<String, Integer>();

	public Map<String, Dog> getDogs() {
		return dogs;
	}

	public void setDogs(Map<String, Dog> dogs) {
		this.dogs = dogs;
	}

	public Map<String, Dog> getDogsWithoutType() {
        return dogsWithoutType;
    }

    public void setDogsWithoutType(Map<String, Dog> dogsWithoutType) {
        this.dogsWithoutType = dogsWithoutType;
    }

    public Map<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(Map<String, Integer> counts) {
		this.counts = counts;
	}
}
