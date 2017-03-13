package net.ionoff.player.model;

public class Song {
	
	private Long id;
	private String name;
	private String title;
	private String folder;
	private String folderUrl;
	private String duration;
	private String author;
	private String artists;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getFolderUrl() {
		return folderUrl;
	}
	public void setFolderUrl(String folderUrl) {
		this.folderUrl = folderUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getArtists() {
		return artists;
	}
	public void setArtists(String artists) {
		this.artists = artists;
	}
	public String getUri() {
		return new StringBuilder().append(folderUrl).append('/').append(name).toString();
	}
	public String getImage() {
		return new StringBuilder().append(folderUrl).append('/').append("album.jpg").toString();
	}
}
