package com.comcast.testclasses;

import java.util.Objects;

import com.comcast.cereal.annotations.CerealClass;

@CerealClass(StringWrapperCerealizer.class)
public class StringWrapper {

	private String str;

	public StringWrapper(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return this.str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		StringWrapper other = (StringWrapper) obj;
		if (!Objects.equals(str, other.str)) return false;
		return true;
	}
}
