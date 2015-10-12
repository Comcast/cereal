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

import java.util.Collection;

public class PrimitiveHolder {
    private Collection<Integer> i;
    private Collection<Boolean> b;
    private Collection<Long> l;
    private Collection<Character> c;
    private Collection<Short> s;
    private Collection<Float> f;
    private Collection<Double> d;
    
    public Collection<Integer> getI() {
        return i;
    }
    
    public void setI(Collection<Integer> i) {
        this.i = i;
    }
    
    public Collection<Boolean> getB() {
        return b;
    }
    
    public void setB(Collection<Boolean> b) {
        this.b = b;
    }
    
    public Collection<Long> getL() {
        return l;
    }
    
    public void setL(Collection<Long> l) {
        this.l = l;
    }
    
    public Collection<Character> getC() {
        return c;
    }
    
    public void setC(Collection<Character> c) {
        this.c = c;
    }
    
    public Collection<Short> getS() {
        return s;
    }
    
    public void setS(Collection<Short> s) {
        this.s = s;
    }

    public Collection<Float> getF() {
        return f;
    }

    public void setF(Collection<Float> f) {
        this.f = f;
    }

    public Collection<Double> getD() {
        return d;
    }

    public void setD(Collection<Double> d) {
        this.d = d;
    }
}
