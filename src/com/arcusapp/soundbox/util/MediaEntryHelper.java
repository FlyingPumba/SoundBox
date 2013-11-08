/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013  Iván Arcuschin Moreno
 *
 * This file is part of SoundBox.
 *
 * SoundBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * SoundBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundBox.  If not, see <http://www.gnu.org/licenses/>.
 */

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