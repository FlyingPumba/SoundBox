package com.arcusapp.arcusmusic;

import java.util.ArrayList;
import java.util.List;

public class SongEntry implements Comparable<SongEntry>{
    private Integer id;
    private String info;
    
    public SongEntry(Integer id, String info) {
        this.id = id;
        this.info = info;
    }
    public Integer getKey() {
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
	
	public static List<String> getStringList(List<SongEntry> lista){
		List<String> values = new ArrayList<String>();
		for(SongEntry se : lista){
			values.add(se.getValue());
		}
		
		return values;
	}
}
