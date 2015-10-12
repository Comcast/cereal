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
package com.comcast.cvs.cereal.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.comcast.cvs.cereal.CerealException;

/**
 * Helper class for converting the cereal-supported set of objects to and from JSON.
 * 
 * @see com.comcast.cvs.cereal.engines.JsonCerealEngine
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class JsonHelper {

    /**
     * Read the next value and return it.
     * 
     * @param reader
     *            the reader to read the JSON string from
     * 
     * @return the JSON converted to cereal objects
     * 
     * @throws CerealException
     *             if the read failed
     */
    public Object read(Reader reader) throws CerealException {
        try {
            JSONTokener tokener = new JSONTokener(reader);
            Object val = tokener.nextValue();

            if (JSONObject.NULL.equals(val)) {
                return null;
            } else if (val instanceof JSONObject) {
                return toMap((JSONObject) val);
            } else if (val instanceof JSONArray) {
                return toList((JSONArray) val);
            } else {
                return val;
            }
        } catch (Exception ex) {
            throw new CerealException("Failed to read JSON", ex);
        }
    }

    /**
     * Write the given cereal to the given writer. If <code>prettyPrint</code> is <code>true</code>,
     * the JSON output will be indented.
     * 
     * @param writer
     *            where to write the JSON output to
     * @param cereal
     *            the cereal to write
     * @param prettyPrint
     *            if <code>true</code>, the JSON output will be indented, otherwise, whitespace will
     *            be conserved
     * 
     * @throws CerealException
     *             if the write fails
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void write(Writer writer, Object cereal, boolean prettyPrint) throws CerealException {
        try {
            String val = null;
            if (null == cereal) {
                val = "null";
            } else if (cereal instanceof Map) {
                JSONObject o = fromMap((Map) cereal);
                val = prettyPrint ? o.toString(2) : o.toString();
            } else if (cereal instanceof List) {
                JSONArray a = fromList((List) cereal);
                val = prettyPrint ? a.toString(2) : a.toString();
            } else if (cereal instanceof String) {
                val = JSONObject.quote((String) cereal);
            } else {
                val = cereal.toString();
            }

            writer.write(val);
        } catch (Exception ex) {
            throw new CerealException("Failed while writing JSON", ex);
        }
    }

    /**
     * Convert a {@link JSONObject} to a {@link Map}.
     * 
     * @param o
     *            the object to convert
     * 
     * @return the converted map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(JSONObject o) {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> i = o.keys();
        while (i.hasNext()) {
            String key = i.next();
            Object val = o.opt(key);
            if (JSONObject.NULL.equals(val)) {
                val = null;
            } else if (val instanceof JSONObject) {
                val = toMap((JSONObject) val);
            } else if (val instanceof JSONArray) {
                val = toList((JSONArray) val);
            }
            map.put(key, val);
        }

        return map;
    }

    /**
     * Convert a {@link JSONArray} to a {@link List}.
     * 
     * @param array
     *            the array to convert
     * 
     * @return the converted list
     */
    private static List<?> toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>(array.length());

        for (int i = 0; i < array.length(); i++) {
            Object val = array.opt(i);
            if (JSONObject.NULL.equals(val)) {
                val = null;
            } else if (val instanceof JSONObject) {
                val = toMap((JSONObject) val);
            } else if (val instanceof JSONArray) {
                val = toList((JSONArray) val);
            }
            list.add(val);
        }

        return list;
    }

    /**
     * Convert a {@link Map} to a {@link JSONObject}.
     * 
     * @param map
     *            the map to convert
     * 
     * @return the converted object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static JSONObject fromMap(Map<String, Object> map) throws JSONException {
        JSONObject o = new JSONObject();

        for (Entry<String, Object> e : map.entrySet()) {
            Object val = e.getValue();
            if (null == val) {
                val = JSONObject.NULL;
            } else if (val instanceof Map) {
                val = fromMap((Map) val);
            } else if (val instanceof List) {
                val = fromList((List) val);
            }
            o.put(e.getKey(), val);
        }

        return o;
    }

    /**
     * Convert a {@link List} to a {@link JSONArray}.
     * 
     * @param list
     *            the list to convert
     * 
     * @return the converted array
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static JSONArray fromList(List<?> list) throws JSONException {
        JSONArray array = new JSONArray();

        for (Object val : list) {
            if (null == val) {
                val = JSONObject.NULL;
            } else if (val instanceof Map) {
                val = fromMap((Map) val);
            } else if (val instanceof List) {
                val = fromList((List) val);
            }
            array.put(val);
        }

        return array;
    }
}
