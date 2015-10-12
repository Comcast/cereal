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

import com.comcast.cvs.cereal.annotations.Cereal;
import com.comcast.cvs.cereal.annotations.CerealObject;

@CerealObject
public class Employee {

    public int startYear;
    public String firstName;
    public String lastName;

    @Cereal(name = "starting-year")
    public int getStartYear() {
        return startYear;
    }

    @Cereal(name = "starting-year")
    public void setStartingYear(int startYear) {
        this.startYear = startYear;
    }

    @Cereal(name = "name")
    public String getName() {
        return firstName + " " + lastName;
    }

    @Cereal(name = "name")
    public void setName(String name) {
        String[] names = name.split(" +");
        this.firstName = names[0];
        this.lastName = names[1];
    }

}
