package com.arcusapp.soundbox.model;

public class Song {
	private String id;
	private String title;
	private String artist;
	private String album;
	private String filePath;

	public Song(String id, String title, String artist, String album, String filePath) {
		this.setID(id);
		this.setName(title);
		this.setArtist(artist);
		this.setAlbum(album);
		this.setFilePath(filePath);
	}

	public Song() {
		this.setID(BundleExtra.DefaultValues.DEFAULT_ID);
		this.setName("");
		this.setArtist("");
		this.setAlbum("");
		this.setFilePath("");
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

	public String getFilePath() {
		return filePath;
	}

	private void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
