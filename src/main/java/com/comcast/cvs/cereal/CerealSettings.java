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
package com.comcast.cvs.cereal;

/**
 * This class provides a way to pass settings through all the different Cerealizers
 * It is given to the object cache for easy access.
 * @author Kevin Pearson
 *
 */
public class CerealSettings {
    private boolean includeClassName = true;
    private boolean useObjectReferences = true;

    public boolean shouldIncludeClassName() {
        return includeClassName;
    }

    public void setIncludeClassName(boolean includeClassName) {
        this.includeClassName = includeClassName;
    }

	public boolean shouldUseObjectReferences() {
		return useObjectReferences;
	}

	public void setUseObjectReferences(boolean useObjectReferences) {
		this.useObjectReferences = useObjectReferences;
	}
}
