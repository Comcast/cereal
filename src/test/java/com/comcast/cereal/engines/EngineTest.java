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

import static org.testng.Assert.assertEquals;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealSettings;
import com.comcast.cereal.engines.AbstractCerealEngine;
import com.comcast.cereal.engines.CerealEngine;
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.cereal.engines.XmlCerealEngine;
import com.comcast.cereal.engines.YamlCerealEngine;
import com.comcast.pantry.test.TestList;
import com.comcast.testclasses.Member;
import com.comcast.testclasses.Team;
import com.comcast.testclasses.Title;

public class EngineTest extends CerealEngineTest {
	
	
	@DataProvider(name="testConversionData")
	public TestList testConversionData() {
		TestList list = new TestList();

		list.add(JsonCerealEngine.class, "/team.json");
		list.add(XmlCerealEngine.class, "/team.xml");
		list.add(YamlCerealEngine.class, "/team.yaml");
		
		return list;
	}
	
	@Test(dataProvider="testConversionData")
	public void testConversion(Class<? extends AbstractCerealEngine> engineClass, String resourcePath) throws CerealException, InstantiationException, IllegalAccessException {

        AbstractCerealEngine engine = engineClass.newInstance();
        Reader reader = new InputStreamReader(
                EngineTest.class.getResourceAsStream(resourcePath));

        Team team = engine.read(reader, Team.class);
        assertEquals(team.name, "ETV Java User Agent");
        assertEquals(team.location, "Mill Valley, CA");
        assertEquals(team.members.size(), 3);
        assertEquals(team.projects.size(), 2);

        Member clark = team.members.get(0);
        assertEquals(clark.firstName, "Clark");
        assertEquals(clark.lastName, "Malmgren");
        assertEquals(clark.title, Title.MANAGER);
        assertEquals(clark.email.getAddress(), "cmalmgren@gmail.com");

        Assert.assertTrue(team.projects instanceof Set);
        Assert.assertTrue(team.projects.contains("DTA"));
        Assert.assertTrue(team.projects.contains("Channelstore"));
	}
	
	private Member createSampleMember() throws AddressException {
        Member m = new Member();
        m.firstName = "First";
        m.lastName = "Last";
        m.email = new InternetAddress("first_last@company.com");
        m.title = Title.VICE_PRESIDENT;
        return m;
	}
    
    @Test(dataProvider="engineData")
    public void testEquality(CerealEngine engine) throws Exception {
        testEquality(engine, new HashSet<String>());
    }
    
    @Test(description="Uses a TreeSet instead of a HashSet for this test")
    public void testEqualityWithTreeSet() throws Exception {
        testEquality(new JsonCerealEngine(), new TreeSet<String>());
    }

    private void testEquality(CerealEngine engine, Set<String> projColl) throws Exception {
        Member m = createSampleMember();

        Team team = new Team();
        team.name = "Awesome";
        team.location = "Awesomeness";

        team.members = new ArrayList<Member>();
        team.members.add(m);
        
        team.projects = projColl;
        team.projects.add("DAWG");

        String str = engine.writeToString(team, Team.class);
        Team team2 = engine.readFromString(str, Team.class);

        assertEquals(team, team2);
    }
	
	@Test
	public void testShouldIncludeClassName() throws CerealException, AddressException {
	    JsonCerealEngine engine = new JsonCerealEngine();
	    
	    Member m = createSampleMember();
        Assert.assertTrue(engine.writeToString(m).contains("--class"));

        CerealSettings settings = new CerealSettings();
        settings.setIncludeClassName(false);
        engine.setSettings(settings);
        Assert.assertFalse(engine.writeToString(m).contains("--class"));
	}
}
