package net.ionoff.player.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
	
	private long nodeIndex;
	private long leafIndex;
	
	private List<PlaylistNode> nodes;

	public boolean hasNode() {
		return nodes != null || !nodes.isEmpty();
	}
	
	public List<PlaylistLeaf> getLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlaylistLeaf> leafs = new ArrayList<PlaylistLeaf>();
		for (PlaylistNode node : nodes) {
			leafs.addAll(node.getLeafs());
		}
		return leafs;
	}
	
	public List<PlaylistLeaf> getUnPlayedLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlaylistLeaf> unPlayedLeafs = new ArrayList<PlaylistLeaf>();
		for (PlaylistNode node : nodes) {
			unPlayedLeafs.addAll(node.getUnPlayedLeafs());
		}
		return unPlayedLeafs;
	}
	
	public List<PlaylistLeaf> getPlayedLeafs() {
		if (!hasNode()) {
			return Collections.emptyList();
		}
		List<PlaylistLeaf> playedLeafs = new ArrayList<PlaylistLeaf>();
		for (PlaylistNode node : nodes) {
			playedLeafs.addAll(node.getPlayedLeafs());
		}
		return playedLeafs;
	}
	
	public List<PlaylistNode> getNodes() {
		if (nodes == null) {
			return Collections.emptyList();
		}
		return nodes;
	}

	public void setNodes(List<PlaylistNode> playlistNodes) {
		this.nodes = playlistNodes;
	}
	
	public int getNodeIndex(PlaylistNode node) {
		if (nodes == null || nodes.isEmpty()) {
			return -1;
		}
		return nodes.indexOf(node);
	}
	
	public int getNodeIndex(PlaylistLeaf leaf) {
		if (nodes == null || nodes.isEmpty()) {
			return -1;
		}
		int nodesListSize = nodes.size();
		for (int i = 0; i < nodesListSize; i++) {
			PlaylistNode node = nodes.get(i);
			if (node.containsLeaf(leaf)) {
				return i;
			}
		}
		return -1;
	}

	public PlaylistLeaf getRandomUnPlayedLeaf() {
		List<PlaylistLeaf> leafs = getUnPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		int random = (int) (Math.random()*(leafs.size()));
		return leafs.get(random);
	}
	
	public PlaylistLeaf getDefaultUnPlayedLeaf(long id) {
		List<PlaylistLeaf> leafs = getUnPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		for (PlaylistLeaf leaf : leafs) {
			if (leaf.getId() > id) {
				return leaf;
			}
		}
		return leafs.get(0);
	}

	public boolean isEmpty() {
		return getLeafs().isEmpty();
	}
	
	public boolean isFinished() {
		return getUnPlayedLeafs().isEmpty();
	}
	
	public long getMaxNodeId() {
		if (getNodes().isEmpty()) {
			return 0;
		}
		long maxNodeId = 0;
		for (PlaylistNode node : getNodes()) {
			if (node.getId() > maxNodeId) {
				maxNodeId = node.getId();
			}
		}
		return maxNodeId;
	}
	
	public long getMaxLeafId() {
		List<PlaylistLeaf> leafs = getLeafs();
		if (leafs.isEmpty()) {
			return 0;
		}
		long maxLeafId = 0;
		for (PlaylistLeaf leaf : leafs) {
			if (leaf.getId() > maxLeafId) {
				maxLeafId = leaf.getId();
			}
		}
		return maxLeafId;
	}

	public void removeLeaf(long leafId) {
		for (PlaylistNode node : getNodes()) {
			for (PlaylistLeaf leaf : node.getLeafs()) {
				if (leaf.getId() == leafId) {
					node.getLeafs().remove(leaf);
					return;
				}
			}
		}
	}
	
	public void removeNode(long nodeId) {
		for (PlaylistNode node : getNodes()) {
			if (node.getId() == nodeId) {
				getNodes().remove(node);
				return;
			}
		}
	}

	public PlaylistLeaf getRandomPlayedLeaf() {
		List<PlaylistLeaf> leafs = getPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		int random = (int) (Math.random()*(leafs.size()));
		return leafs.get(random);
	}

	public PlaylistLeaf getDefaultPlayedLeaf() {
		List<PlaylistLeaf> leafs = getPlayedLeafs();
		if (leafs.isEmpty()) {
			return null;
		}
		return leafs.get(leafs.size() - 1);
	}

	public PlaylistNode getCurrentNode() {
		for (PlaylistNode node : getNodes()) {
			if (node.isCurrent()) {
				return node;
			}
		}
		return null;
	}

	public PlaylistLeaf getLeaf(long leafId) {
		for (PlaylistLeaf leaf : getLeafs()) {
			if (leaf.getId() == leafId) {
				return leaf;
			}
		}
		return null;
	}

	public PlaylistNode getNode(long nodeId) {
		for (PlaylistNode node : getNodes()) {
			if (node.getId() == nodeId) {
				return node;
			}
		}
		return null;
	}

	public void resetPlayed() {
		for (PlaylistLeaf leaf : getLeafs()) {
			leaf.setPlayed(false);
		}
	}
	
	public void resetCurrent() {
		for (PlaylistLeaf leaf : getLeafs()) {
			leaf.setCurrent(false);
		}
	}

	public boolean hasNode(PlaylistNode node) {
		for (PlaylistNode plNode : getNodes()) {
			if (node.getId() == plNode.getId() && plNode.getId() != 0) {
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
