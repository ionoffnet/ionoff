package net.ionoff.player.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayNode {
	
	public enum TYPE {
		album, dir, youtube;
	}
	
	private long id;
	private long idx;
	private String name;
	private String type;
	private String image;
	
	private List<PlayLeaf> leafs;

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

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
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

	public List<PlayLeaf> getLeafs() {
		if (leafs == null) {
			return Collections.emptyList();
		}
		return leafs;
	}

	public void setLeafs(List<PlayLeaf> playlistLeafs) {
		this.leafs = playlistLeafs;
	}

	public boolean hasLeaf() {
		return leafs != null && !leafs.isEmpty();
	}

	public boolean containsLeaf(PlayLeaf leaf) {
		if (!hasLeaf()) {
			return false;
		}
		return leafs.contains(leaf);
	}
	
	public boolean isCurrent() {
		if (!hasLeaf()) {
			return false;
		}
		for (PlayLeaf leaf : leafs) {
			if (leaf.isCurrent()) {
				return true;
			}
		}
		return false;
	}

	public List<PlayLeaf> getNotPlayedLeafs() {
		if (!hasLeaf()) {
			return Collections.emptyList();
		}
		List<PlayLeaf> notPlayedLeafs = new ArrayList<PlayLeaf>();
		for (PlayLeaf leaf : leafs) {
			if (!leaf.isPlayed()) {
				notPlayedLeafs.add(leaf);
			}
		}
		return notPlayedLeafs;
	}

	public Collection<? extends PlayLeaf> getPlayedLeafs() {
		if (!hasLeaf()) {
			return Collections.emptyList();
		}
		List<PlayLeaf> playedLeafs = new ArrayList<PlayLeaf>();
		for (PlayLeaf leaf : leafs) {
			if (leaf.isPlayed()) {
				playedLeafs.add(leaf);
			}
		}
		return playedLeafs;
	}
}
