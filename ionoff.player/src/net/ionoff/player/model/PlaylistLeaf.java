package net.ionoff.player.model;

public class PlaylistLeaf {
	
	private static final String CURRENT = "current";

	public enum TYPE {
		file, youtube, karaoke;
	}
	
	private long id;
	private String name;
	private String type;
	private String image;
	private String duration;
	private String uri;
	private String current;
	private boolean played;
	private String author;
	private String artists;
	private String mrl;
	
	public PlaylistLeaf() {
		played = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}
	
	public boolean isCurrent() {
		return CURRENT.equals(current);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String img) {
		this.image = img;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public void setCurrent(boolean current) {
		if (current) {
			setCurrent(CURRENT);
		}
		else {
			setCurrent("");
		}
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

	public boolean isYoutube() {
		return TYPE.youtube.toString().equals(getType());
	}

	public String getMrl() {
		return mrl;
	}

	public void setMrl(String mrl) {
		this.mrl = mrl;
	}
}