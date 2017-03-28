package net.ionoff.player.model;

public class YoutubeVideo {
	
	private String id;
	private String mrl;
	private String title;
	private String thumbnail;
	private long duration;
	private String author;
	private long favoriteCount;
	private long viewCount;
	private String uploadedTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMrl() {
		return mrl;
	}

	public void setMrl(String mrl) {
		this.mrl = mrl;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUploadedTime() {
		return uploadedTime;
	}

	public void setUploadedTime(String uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
}
