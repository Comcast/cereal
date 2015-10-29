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
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.engines.CerealEngine;
import com.comcast.cereal.engines.JsonCerealEngine;
import com.comcast.cereal.engines.XmlCerealEngine;
import com.comcast.cereal.engines.YamlCerealEngine;
import com.comcast.pantry.test.RandomProvider;
import com.comcast.pantry.test.TestList;

/**
 * The {@link TypeTest} provides a home for doing testing of specific {@link Cerealizer}s,
 * especially ones that are included as "defaults".
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class TypeTest {

    @DataProvider(name = "dateTests")
    public Iterator<Object[]> getDateTests() {
        List<Object[]> tests = new ArrayList<Object[]>();
        Random r = new Random(298374091359l);

        for (int i = 0; i < 50; i++) {
            tests.add(new Object[] { new JsonCerealEngine(), Math.abs(r.nextLong()) });
            tests.add(new Object[] { new YamlCerealEngine(), Math.abs(r.nextLong()) });
            tests.add(new Object[] { new XmlCerealEngine(), Math.abs(r.nextLong()) });
        }

        tests.add(new Object[] { new JsonCerealEngine(), 0 });
        tests.add(new Object[] { new YamlCerealEngine(), 0 });
        tests.add(new Object[] { new XmlCerealEngine(), 0 });

        /*
         * JotaTime actually fails on Long.MAX_VALUE, but that is so far in the future that we will
         * be lucky if people still exist at that time
         */
        final long MAX_VALUE = Long.MAX_VALUE / 2;
        tests.add(new Object[] { new JsonCerealEngine(), MAX_VALUE });
        tests.add(new Object[] { new YamlCerealEngine(), MAX_VALUE });
        tests.add(new Object[] { new XmlCerealEngine(), MAX_VALUE });

        return tests.iterator();
    }

    @Test(dataProvider = "dateTests")
    public void testDate(CerealEngine engine, long epoch) throws CerealException {
        DateContainer expected = new DateContainer();
        expected.date = new Date(epoch);

        String string = engine.writeToString(expected);
        DateContainer actual = engine.readFromString(string, DateContainer.class);

        assertEquals(actual.date, expected.date);
    }

    public static class DateContainer {
        private Date date;
    }

    @DataProvider(name = "byteArrayTests")
    public Iterator<Object[]> getByteArrayTests() {
        List<Object[]> tests = new ArrayList<Object[]>();
        Random r = new Random(298379058739854l);
        
        final long MAX_SIZE = 1024 * 1024; // 1MB
        for (int i = 8; i <= MAX_SIZE; i <<= 3) {
            tests.add(new Object[] { new JsonCerealEngine(), r, i });
            tests.add(new Object[] { new YamlCerealEngine(), r, i  });
            tests.add(new Object[] { new XmlCerealEngine(), r, i  });
        }

        tests.add(new Object[] { new JsonCerealEngine(), r, 0 });
        tests.add(new Object[] { new YamlCerealEngine(), r, 0 });
        tests.add(new Object[] { new XmlCerealEngine(), r, 0 });

        return tests.iterator();
    }

    @Test(dataProvider = "byteArrayTests")
    public void testByteArray(CerealEngine engine, Random r, int size) throws CerealException {
        byte[] array = random(r, size);
        ByteArrayContainer expected = new ByteArrayContainer();
        expected.array = array;

        String string = engine.writeToString(expected);
        ByteArrayContainer actual = engine.readFromString(string, ByteArrayContainer.class);

        assertEquals(actual.array, expected.array);
    }

    public static class ByteArrayContainer {
        private byte[] array;
    }

    public static byte[] random(Random r, int size) {
        byte[] array = new byte[size];
        r.nextBytes(array);
        return array;
    }
    


    @DataProvider(name = "primitiveTests")
    public TestList getPrimitiveTests() {
        TestList tests = new TestList();
        RandomProvider r = new RandomProvider(6854984168548l);

        for (int i = 0; i < 50; i++) {
            tests.add(r.nextBoolean(), (byte) r.nextInt(), (char) r.nextInt(0, 255), (short) r.nextInt(), r.nextInt(),
                    r.nextLong(), r.nextFloat(), r.nextDouble());
        }

        return tests;
    }

    @Test(dataProvider = "primitiveTests")
    public void testPrimitives(boolean _boolean, byte _byte, char _char, short _short, int _int, long _long,
            float _float, double _double) throws CerealException {
        PrimitiveContainer pc = new PrimitiveContainer();
        
        pc._boolean = _boolean;
        pc._byte = _byte;
        pc._char = _char;
        pc._short = _short;
        pc._int = _int;
        pc._long = _long;
        pc._float = _float;
        pc._double = _double;
        
        CerealEngine engine = new JsonCerealEngine();
        String json = engine.writeToString(pc);
        PrimitiveContainer actual = engine.readFromString(json, PrimitiveContainer.class);

        assertEquals(actual._boolean, _boolean);
        assertEquals(actual._byte, _byte);
        assertEquals(actual._char, _char);
        assertEquals(actual._short, _short);
        assertEquals(actual._int, _int);
        assertEquals(actual._long, _long);
        assertEquals(actual._float, _float);
        assertEquals(actual._double, _double);
    }

    public static class PrimitiveContainer {
        boolean _boolean;
        byte _byte;
        char _char;
        short _short;
        int _int;
        long _long;
        float _float;
        double _double;
    }

    @Test
    public void testString() throws CerealException, IOException {
        String s = "\u140000";
        process(s);
        
        StringContainer sc = new StringContainer();
        sc.s = s;
        
        CerealEngine engine = new JsonCerealEngine(false);
        String actual = engine.writeToString(sc);

        assertTrue(actual.contains("\"s\":\"\\u140000\"") ||
                actual.contains("\"s\":\""+"\u1400"+"00\""),
                "Actual["+actual+"]");
        assertTrue(actual.contains("\"--class\":\"" + StringContainer.class.getName() + "\""));
    }
    
    
    public static class StringContainer {
        String s;
    }
    
    private static void process(String original) throws IOException {
        for (int i = 0; i < original.length(); i++) {
            int cp = original.codePointAt(i);
            System.out.println("["+i+"]: " + original.charAt(i) + " \\u" + Integer.toHexString(cp));
        }
    }
}
