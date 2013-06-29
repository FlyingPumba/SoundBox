package com.arcusapp.soundbox;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class associated to the SongEntry lists.
 */
public class SongEntryHelper {
	public static List<String> getValuesList(List<SongEntry> list) {
		List<String> values = new ArrayList<String>();
		for (SongEntry se : list) {
			values.add(se.getValue());
		}

		return values;
	}

	public static List<String> getIDsList(List<SongEntry> list) {
		List<String> keys = new ArrayList<String>();
		for (SongEntry se : list) {
			keys.add(se.getID());
		}

		return keys;
	}
}
