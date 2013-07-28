package com.arcusapp.soundbox.model;

public class MediaEntry implements Entry<MediaEntry> {
    private String id;
    private String value;

    public MediaEntry(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getID() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(MediaEntry another) {
        return this.value.compareToIgnoreCase(another.getValue());
    }
}
