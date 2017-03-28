package net.ionoff.player.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayList {
	
	private long id;
	private String name;
	private long leafIndex;
	private long nodeIndex;
	private List<PlayNode> nodes;

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

	public boolean hasNode() {
		return nodes != null || !nodes.isEmpty();
	}
	
	public List<PlayLeaf> getLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlayLeaf> leafs = new ArrayList<PlayLeaf>();
		for (PlayNode node : nodes) {
			leafs.addAll(node.getLeafs());
		}
		return leafs;
	}
	
	public List<PlayLeaf> getNotPlayedLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlayLeaf> notPlayedLeafs = new ArrayList<PlayLeaf>();
		for (PlayNode node : nodes) {
			notPlayedLeafs.addAll(node.getNotPlayedLeafs());
		}
		return notPlayedLeafs;
	}
	
	public List<PlayLeaf> getPlayedLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlayLeaf> playedLeafs = new ArrayList<PlayLeaf>();
		for (PlayNode node : nodes) {
			playedLeafs.addAll(node.getPlayedLeafs());
		}
		return playedLeafs;
	}
	
	public List<PlayNode> getNodes() {
		if (nodes == null) {
			return Collections.emptyList();
		}
		return nodes;
	}

	public void setNodes(List<PlayNode> playlistNodes) {
		this.nodes = playlistNodes;
	}
	
	public int getNodeIndex(PlayNode node) {
		if (nodes == null || nodes.isEmpty()) {
			return -1;
		}
		return nodes.indexOf(node);
	}
	
	public int getNodeIndex(PlayLeaf leaf) {
		if (nodes == null || nodes.isEmpty()) {
			return -1;
		}
		int nodesListSize = nodes.size();
		for (int i = 0; i < nodesListSize; i++) {
			PlayNode node = nodes.get(i);
			if (node.containsLeaf(leaf)) {
				return i;
			}
		}
		return -1;
	}

	public PlayLeaf getRandomUnPlayedLeaf() {
		List<PlayLeaf> leafs = getNotPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		int random = (int) (Math.random()*(leafs.size()));
		return leafs.get(random);
	}
	
	public PlayLeaf getDefaultUnPlayedLeaf(long idx) {
		List<PlayLeaf> leafs = getNotPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		for (PlayLeaf leaf : leafs) {
			if (leaf.getIdx() > idx) {
				return leaf;
			}
		}
		return leafs.get(0);
	}

	public boolean isEmpty() {
		return getLeafs().isEmpty();
	}
	
	public boolean isFinished() {
		return getNotPlayedLeafs().isEmpty();
	}
	
	public long getMaxNodeIdx() {
		if (getNodes().isEmpty()) {
			return 0;
		}
		long maxNodeId = 0;
		for (PlayNode node : getNodes()) {
			if (node.getIdx() > maxNodeId) {
				maxNodeId = node.getIdx();
			}
		}
		return maxNodeId;
	}
	
	public long getMaxLeafIdx() {
		List<PlayLeaf> leafs = getLeafs();
		if (leafs.isEmpty()) {
			return 0;
		}
		long maxLeafIdx = 0;
		for (PlayLeaf leaf : leafs) {
			if (leaf.getIdx() > maxLeafIdx) {
				maxLeafIdx = leaf.getIdx();
			}
		}
		return maxLeafIdx;
	}

	public void removeLeaf(long leafIdx) {
		for (PlayNode node : getNodes()) {
			for (PlayLeaf leaf : node.getLeafs()) {
				if (leaf.getIdx() == leafIdx) {
					node.getLeafs().remove(leaf);
					return;
				}
			}
		}
	}
	
	public void removeNode(long nodeIdx) {
		for (PlayNode node : getNodes()) {
			if (node.getIdx() == nodeIdx) {
				getNodes().remove(node);
				return;
			}
		}
	}

	public PlayLeaf getRandomPlayedLeaf() {
		List<PlayLeaf> leafs = getPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		int random = (int) (Math.random()*(leafs.size()));
		return leafs.get(random);
	}

	public PlayLeaf getDefaultPlayedLeaf() {
		List<PlayLeaf> leafs = getPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		return leafs.get(leafs.size() - 1);
	}

	public PlayNode getCurrentNode() {
		for (PlayNode node : getNodes()) {
			if (node.isCurrent()) {
				return node;
			}
		}
		return null;
	}

	public PlayLeaf getLeaf(long leafIdx) {
		for (PlayLeaf leaf : getLeafs()) {
			if (leaf.getIdx() == leafIdx) {
				return leaf;
			}
		}
		return null;
	}

	public PlayNode getNode(long nodeIdx) {
		for (PlayNode node : getNodes()) {
			if (node.getIdx() == nodeIdx) {
				return node;
			}
		}
		return null;
	}

	public void resetPlayed() {
		for (PlayLeaf leaf : getLeafs()) {
			leaf.setPlayed(false);
		}
	}
	
	public void resetCurrent() {
		for (PlayLeaf leaf : getLeafs()) {
			leaf.setCurrent(false);
		}
	}

	public boolean hasNode(PlayNode node) {
		for (PlayNode plNode : getNodes()) {
			if (node.getIdx() == plNode.getIdx() && plNode.getIdx() != 0) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		getNodes().clear();
	}

	public long getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(long nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public long getLeafIndex() {
		return leafIndex;
	}

	public void setLeafIndex(long leafIndex) {
		this.leafIndex = leafIndex;
	}

}
