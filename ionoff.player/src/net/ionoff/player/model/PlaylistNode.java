package net.ionoff.player.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlaylistNode {
	
	public enum TYPE {
		album, dir, youtube;
	}
	
	private long id;
	private String name;
	private String type;
	private String image;
	
	private List<PlaylistLeaf> leafs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<PlaylistLeaf> getLeafs() {
		if (leafs == null) {
			return Collections.emptyList();
		}
		return leafs;
	}

	public void setLeafs(List<PlaylistLeaf> playlistLeafs) {
		this.leafs = playlistLeafs;
	}

	public boolean hasLeaf() {
		return leafs != null && !leafs.isEmpty();
	}

	public boolean containsLeaf(PlaylistLeaf leaf) {
		if (!hasLeaf()) {
			return false;
		}
		return leafs.contains(leaf);
	}
	
	public boolean isCurrent() {
		if (!hasLeaf()) {
			return false;
		}
		for (PlaylistLeaf leaf : leafs) {
			if (leaf.isCurrent()) {
				return true;
			}
		}
		return false;
	}

	public List<PlaylistLeaf> getUnPlayedLeafs() {
		if (!hasLeaf()) {
			return Collections.emptyList();
		}
		List<PlaylistLeaf> unPlayedLeafs = new ArrayList<PlaylistLeaf>();
		for (PlaylistLeaf leaf : leafs) {
			if (!leaf.isPlayed()) {
				unPlayedLeafs.add(leaf);
			}
		}
		return unPlayedLeafs;
	}

	public Collection<? extends PlaylistLeaf> getPlayedLeafs() {
		if (!hasLeaf()) {
			return Collections.emptyList();
		}
		List<PlaylistLeaf> playedLeafs = new ArrayList<PlaylistLeaf>();
		for (PlaylistLeaf leaf : leafs) {
			if (leaf.isPlayed()) {
				playedLeafs.add(leaf);
			}
		}
		return playedLeafs;
	}
}
