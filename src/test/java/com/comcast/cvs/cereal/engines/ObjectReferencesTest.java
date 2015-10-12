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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cvs.cereal.CerealException;
import com.comcast.cvs.cereal.CerealSettings;
import com.comcast.cvs.testclasses.Member;
import com.comcast.cvs.testclasses.Team;
import com.comcast.cvs.testclasses.Title;

public class ObjectReferencesTest {
	
	@DataProvider(name="testReferenceSettingsData")
	public Object[][] testReferenceSettingsData() {
		return new Object[][] {
				{true}, {false}	
		};
	}

	@Test(dataProvider="testReferenceSettingsData")
	public void testReferenceSettings(boolean useReferences) throws CerealException, AddressException {
		JsonCerealEngine engine = new JsonCerealEngine();
		CerealSettings settings = new CerealSettings();
		settings.setUseObjectReferences(useReferences);
		engine.setSettings(settings);
		Member kevin = new Member();
		kevin.firstName = "Kevin";
		kevin.lastName = "Pearson";
		kevin.title = Title.ENGINEER;
		kevin.email = new InternetAddress("kevin@cable.comcast.com");
		Member kevin2 = new Member();
		kevin2.firstName = "Kevin";
		kevin2.lastName = "Pearson";
		kevin2.title = Title.ENGINEER;
		kevin2.email = new InternetAddress("kevin@cable.comcast.com");
		
		Team team = new Team();
		team.members = new ArrayList<Member>();
		team.members.add(kevin);
		team.members.add(kevin2);
		
		String json = engine.writeToString(team);
		Assert.assertEquals(useReferences, json.contains("object-ref"));
	}
}
