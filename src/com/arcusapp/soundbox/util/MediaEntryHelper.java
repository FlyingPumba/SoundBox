package com.arcusapp.soundbox.util;

import java.util.ArrayList;
import java.util.List;

import com.arcusapp.soundbox.model.MediaEntry;

public class MediaEntryHelper<T extends MediaEntry> {
	public List<String> getValues(List<T> list) {
		List<String> values = new ArrayList<String>();
		for (MediaEntry me : list) {
			values.add(me.getValue());
		}

		return values;
	}

	public List<String> getIDs(List<T> list) {
		List<String> ids = new ArrayList<String>();
		for (MediaEntry me : list) {
			ids.add(me.getID());
		}

		return ids;
	}
}
