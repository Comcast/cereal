package com.comcast.testclasses;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.comcast.cereal.annotations.Cereal;

public class StringWrapperContainer {
	
	@Cereal(type=StringWrapper.class)
	private Collection<StringWrapper> wrappers;

	@Cereal(type=Set.class, subtype=StringWrapper.class)
	private Map<String, Set<StringWrapper>> wrapperMap;
	
	@Cereal(type=Set.class)
	private Map<String, Set<String>> wrapperMapNoSubtype;

	public Collection<StringWrapper> getWrappers() {
		return wrappers;
	}

	public void setWrappers(Collection<StringWrapper> wrappers) {
		this.wrappers = wrappers;
	}

	public Map<String, Set<StringWrapper>> getWrapperMap() {
		return wrapperMap;
	}

	public void setWrapperMap(Map<String, Set<StringWrapper>> wrapperMap) {
		this.wrapperMap = wrapperMap;
	}

	public Map<String, Set<String>> getWrapperMapNoSubtype() {
		return wrapperMapNoSubtype;
	}

	public void setWrapperMapNoSubtype(Map<String, Set<String>> wrapperMapNoSubtype) {
		this.wrapperMapNoSubtype = wrapperMapNoSubtype;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null)return false;
		if (getClass() != obj.getClass()) return false;
		StringWrapperContainer other = (StringWrapperContainer) obj;
		if (!Objects.equals(wrappers, other.wrappers)) return false;
		if (!Objects.equals(wrapperMap, other.wrapperMap)) return false;
		if (!Objects.equals(wrapperMapNoSubtype, other.wrapperMapNoSubtype)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "wrapperMap=" + wrapperMap + " wrapperMapNoSubtype=" + wrapperMapNoSubtype + " wrappers=" + wrappers;
	}
	
}
