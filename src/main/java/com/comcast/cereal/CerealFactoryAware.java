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
package com.comcast.cereal;

import com.comcast.cereal.engines.CerealEngine;

/**
 * Interface that all {@link Cerealizer}s should implement if they need access to the
 * {@link CerealFactory} associated with this {@link CerealEngine}.
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public interface CerealFactoryAware {

    /**
     * Notify this instance what {@link CerealFactory} is in use for this {@link CerealEngine}.
     * 
     * @param cerealFactory
     *            the {@link CerealFactory} instance
     */
    void setCerealFactory(CerealFactory cerealFactory);
}
