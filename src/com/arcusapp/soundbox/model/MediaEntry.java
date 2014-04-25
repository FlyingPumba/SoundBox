/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013 Iván Arcuschin Moreno
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

package com.arcusapp.soundbox.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaEntry implements Entry<MediaEntry>, Parcelable {
    private String mId;
    private MediaType mType;
    private String mValue;

    public MediaEntry(String id, MediaType type, String value) {
        this.mId = id;
        this.mType = type;
        this.mValue = value;
    }

    public MediaEntry(Parcel parcel) {
        String[] strings = new String[2];
        parcel.readStringArray(strings);
        mId = strings[0];
        mValue = strings[1];

        mType = (MediaType) parcel.readSerializable();
    }

    public String getID() {
        return mId;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public MediaType getMediaType() { return mType; }

    @Override
    public int compareTo(MediaEntry another) {
        return this.mValue.compareToIgnoreCase(another.getValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mId, mValue});
        dest.writeSerializable(mType);
    }

    public static final Parcelable.Creator<MediaEntry> CREATOR = new Parcelable.Creator<MediaEntry>() {
        @Override
        public MediaEntry createFromParcel(Parcel parcel) {
            return new MediaEntry(parcel);
        }

        @Override
        public MediaEntry[] newArray(int size) {
            return new MediaEntry[size];
        }
    };

}
