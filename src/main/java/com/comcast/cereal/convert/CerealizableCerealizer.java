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

import org.objenesis.ObjenesisHelper;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.CerealFactory;
import com.comcast.cereal.CerealFactoryAware;
import com.comcast.cereal.Cerealizable;
import com.comcast.cereal.Cerealizer;
import com.comcast.cereal.ObjectCache;

/**
 * A {@link Cerealizer} for wrapping objects that implement {@link Cerealizable}.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class CerealizableCerealizer<J extends Cerealizable<C>, C> implements Cerealizer<J, C>,
        CerealFactoryAware {

    private Class<J> type;
    private CerealFactory cerealFactory;

    /**
     * Construct a new {@link CerealizableCerealizer} for the given type.
     * 
     * @param type
     *            the type
     */
    public CerealizableCerealizer(Class<J> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#cerealize(java.lang.Object)
     */
    public C cerealize(J object, ObjectCache objectCache) throws CerealException {
        if (null == object) {
            return null;
        }

        if (object instanceof CerealFactoryAware) {
            ((CerealFactoryAware) object).setCerealFactory(cerealFactory);
        }

        return object.toCereal();
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.Cerealizer#deCerealize(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public J deCerealize(C cereal, ObjectCache objectCache) throws CerealException {
        J object = null;
        try {
            object = type.newInstance();
        } catch (Exception ex) {
            object = (J) ObjenesisHelper.newInstance(type);
        }

        if (object instanceof CerealFactoryAware) {
            ((CerealFactoryAware) object).setCerealFactory(cerealFactory);
        }

        object.applyCereal(cereal);
        return object;
    }

    /*
     * (non-Javadoc)
     * @see com.comcast.cereal.CerealFactoryAware#setCerealFactory(com.comcast.cereal.CerealFactory)
     */
    public void setCerealFactory(CerealFactory cerealFactory) {
        this.cerealFactory = cerealFactory;
    }
}
