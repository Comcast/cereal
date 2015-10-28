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
package com.comcast.cereal.convert;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.ObjectCache;

/**
 * Either returns the primitive object. If it is a string then it will parse the primitive
 * @author Kevin Pearson
 *
 * @param <T>
 */
public abstract class PrimitiveCerealizer<T> extends SimpleCerealizer {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public T deCerealize(Object cereal, ObjectCache objectCache)
            throws CerealException {
        if (cereal == null) return null;
        if (cereal instanceof String) {
            return parsePrimitive((String) cereal);
        } else if (cereal instanceof Number) {
            return castPrimitive((Number) cereal);
        }
        return (T) cereal;
    }

    /**
     * Method to be called for each individual primitive to be parsed
     * from a string
     * @param cereal The string value to parse the primitive from
     * @return
     */
    protected abstract T parsePrimitive(String cereal);

    /**
     * Method called for each individual primitive to be parsed based on
     * a number so that it can be cast to the correct format.
     * @param number The number to be cast to the correct class
     * @return
     */
    protected abstract T castPrimitive(Number number);

    public static class IntegerCerealizer extends PrimitiveCerealizer<Integer> {
        @Override
        protected Integer parsePrimitive(String cereal) {
            return Integer.parseInt(cereal);
        }

        @Override
        protected Integer castPrimitive(Number number) {
            return number.intValue();
        }
    }

    public static class BooleanCerealizer extends PrimitiveCerealizer<Boolean> {
        @Override
        protected Boolean parsePrimitive(String cereal) {
            return Boolean.parseBoolean(cereal);
        }

        @Override
        protected Boolean castPrimitive(Number number) {
            return number.shortValue() != 0;
        }
    }

    public static class LongCerealizer extends PrimitiveCerealizer<Long> {
        @Override
        protected Long parsePrimitive(String cereal) {
            return Long.parseLong(cereal);
        }

        @Override
        protected Long castPrimitive(Number number) {
            return number.longValue();
        }
    }

    public static class CharCerealizer extends PrimitiveCerealizer<Character> {
        @Override
        protected Character parsePrimitive(String cereal) {
            /** If decerealizing a char from the string, just take the first char */
            if (cereal == null) return null;
            if (cereal.isEmpty()) return Character.UNASSIGNED;
            return cereal.charAt(0); 
        }

        @Override
        protected Character castPrimitive(Number number) {
            return (char) number.byteValue();
        }
    }

    public static class ShortCerealizer extends PrimitiveCerealizer<Short> {
        @Override
        protected Short parsePrimitive(String cereal) {
            return Short.parseShort(cereal);
        }

        @Override
        protected Short castPrimitive(Number number) {
            return number.shortValue();
        }
    }

    public static class FloatCerealizer extends PrimitiveCerealizer<Float> {
        @Override
        protected Float parsePrimitive(String cereal) {
            return Float.parseFloat(cereal);
        }

        @Override
        protected Float castPrimitive(Number number) {
            return number.floatValue();
        }
    }

    public static class DoubleCerealizer extends PrimitiveCerealizer<Double> {
        @Override
        protected Double parsePrimitive(String cereal) {
            return Double.parseDouble(cereal);
        }

        @Override
        protected Double castPrimitive(Number number) {
            return number.doubleValue();
        }
    }

}