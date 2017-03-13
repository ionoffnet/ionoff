package net.ionoff.player.model;

import java.util.List;

public class Album {
	
	private long id;
	private String name;
	private String title;
	private String image; // image url
	private String artist;
	private String author;
	private List<Song> songs;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public List<Song> getSongs() {
		return songs;
	}
	public void setSongs(List<Song> items) {
		this.songs = items;
	}
}
