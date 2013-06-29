package com.arcusapp.soundbox;

import java.util.ArrayList;
import java.util.List;

public class SongEntry implements Comparable<SongEntry> {
	private String id;
	private String info;

	public SongEntry(String id, String info) {
		this.id = id;
		this.info = info;
	}

	public String getKey() {
		return id;
	}

	public String getValue() {
		return info;
	}

	public void setValue(String newInfo) {
		this.info = newInfo;
	}

	@Override
	public int compareTo(SongEntry another) {
		return this.info.compareToIgnoreCase(another.getValue());
	}

	public static List<String> getValuesList(List<SongEntry> lista) {
		List<String> values = new ArrayList<String>();
		for (SongEntry se : lista) {
			values.add(se.getValue());
		}

		return values;
	}

	public static List<String> getKeysList(List<SongEntry> lista) {
		List<String> keys = new ArrayList<String>();
		for (SongEntry se : lista) {
			keys.add(se.getKey());
		}

		return keys;
	}
}
