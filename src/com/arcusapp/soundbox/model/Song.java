package com.arcusapp.soundbox.model;

import java.io.File;

public class Song {
	private String id;
	private String title;
	private String artist;
	private String album;
	private File file;

	public Song(String id, String title, String artist, String album, String filePath) {
		this.setID(id);
		this.setName(title);
		this.setArtist(artist);
		this.setAlbum(album);
		this.setFile(filePath);
	}

	public Song() {
		this.setID(BundleExtra.DefaultValues.DEFAULT_ID);
		this.setName("");
		this.setArtist("");
		this.setAlbum("");
		this.setFile("");
	}

	public String getID() {
		return id;
	}

	private void setID(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	private void setName(String name) {
		this.title = name;
	}

	public String getArtist() {
		return artist;
	}

	private void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	private void setAlbum(String album) {
		this.album = album;
	}

	public File getFile() {
		return file;
	}

	private void setFile(String filePath) {
		this.file = new File(filePath);
	}
}
