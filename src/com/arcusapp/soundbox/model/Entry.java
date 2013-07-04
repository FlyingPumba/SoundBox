package com.arcusapp.soundbox.model;

public interface Entry<T> extends Comparable<T> {

	public String getID();

	public String getValue();

	public void setValue(String value);

	public int compareTo(T another);
}