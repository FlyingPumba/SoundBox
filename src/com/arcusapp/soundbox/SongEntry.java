package com.arcusapp.soundbox;

/**
 * Data class to store the id of a song associated with a specific string value for that song (For example: Name, Artist, Album, Filename, etc.)
 * Implements Comparable to allow sorting in SongEntry lists.
 */
public class SongEntry implements Comparable<SongEntry> {
	private String id;
	private String value;

	public SongEntry(String id, String info) {
		this.id = id;
		this.value = info;
	}

	public String getID() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		this.value = newValue;
	}

	@Override
	public int compareTo(SongEntry another) {
		return this.value.compareToIgnoreCase(another.getValue());
	}

}
