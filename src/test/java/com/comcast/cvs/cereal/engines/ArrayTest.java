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
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.cvs.testclasses.Member;

public class ArrayTest {

    @Test
    public void testArrayDecerealization() throws CerealException {
        JsonCerealEngine jsonEngine = new JsonCerealEngine(true);
        
        Member[] members = jsonEngine.readFromClasspath("/array.json", Member[].class);
        
        assertEquals(members.length, 3);
        assertEquals(members[0].firstName, "Clark");
        assertEquals(members[1].firstName, "Matt");
        assertEquals(members[2].firstName, "David");
    }
}
