package net.ionoff.player;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bff.javampd.command.MPDCommandExecutor;
import org.bff.javampd.file.MPDFile;
import org.bff.javampd.playlist.PlaylistChangeEvent;
import org.bff.javampd.playlist.PlaylistChangeListener;
import org.bff.javampd.server.MPD;
import org.bff.javampd.song.MPDSong;

import net.ionoff.player.config.AppConfig;
import net.ionoff.player.config.UserConfig;
import net.ionoff.player.exception.MpdConnectException;
import net.ionoff.player.handler.MediaFileFilter;
import net.ionoff.player.model.Album;
import net.ionoff.player.model.MediaFile;
import net.ionoff.player.model.Playlist;
import net.ionoff.player.model.PlaylistLeaf;
import net.ionoff.player.model.PlaylistNode;
import net.ionoff.player.model.Song;
import net.ionoff.player.model.State;
import net.ionoff.player.model.Status;
import net.ionoff.player.model.YoutubeVideo;

public class DeamonPlayer {

	private MPD mpd;
	private Status status;
	private Playlist playlist;
	private long playlistVersion;

	private List<MPDSong> songs;
	private final MPDCommandExecutor commandExecutor;
	
	public DeamonPlayer() throws MpdConnectException {
		this.playlist = new Playlist();
		playlist.setNodes(new ArrayList<PlaylistNode>());
		try {
			mpd = new MPD.Builder().server(AppConfig.getInstance().MPD_HOST)
					.port(AppConfig.getInstance().MPD_PORT).build();
			commandExecutor = new MPDCommandExecutor();
			commandExecutor.setMpd(mpd);
			
			mpd.getPlaylist().addPlaylistChangeListener(new PlaylistChangeListener() {
				@Override
				public void playlistChanged(PlaylistChangeEvent event) {
					songs = mpd.getPlaylist().getSongList();
					playlistVersion = mpd.getPlaylist().getVersion();
				}
			});
			mpd.getPlaylist().clearPlaylist();
		} catch (UnknownHostException e) {
			throw new MpdConnectException(AppConfig.getInstance().MPD_HOST);
		}
	}

	public Status getStatus() {
		return status;
	}

	public Playlist getPlaylist() {
		if (playlistVersion != mpd.getPlaylist().getVersion()) {
			playlist.getNodes().clear();
			mpd.getPlaylist().clearPlaylist();
		}
		return playlist;
	}

	public Status loadStatus() {
		status = new Status();
		status.setState(toState(mpd.getPlayer().getStatus()));
		status.setTime(mpd.getPlayer().getElapsedTime());
		status.setLength(mpd.getPlayer().getTotalTime());
		status.setVolume(mpd.getPlayer().getVolume());
		if (status.getLength() == 0) {
			status.setPosition(0f);
		}
		else {
			status.setPosition((float) status.getTime() / status.getLength());
		}
		status.setFullscreen(false);
		status.setRandom(mpd.getPlayer().isRandom());
		status.setRepeat(mpd.getPlayer().isRepeat());
		status.setLoop(true);

		MPDSong currentSong = mpd.getPlayer().getCurrentSong();
		if (currentSong == null) {
			status.setPlaylistNode(null);
		} else {
			PlaylistNode currentNode = getCurrentNode(mpd, currentSong);
			status.setPlaylistNode(currentNode);
		}
		
		return status;
	}

	private PlaylistNode getCurrentNode(MPD mpd, MPDSong currentSong) {
		for (PlaylistLeaf leaf : playlist.getLeafs()) {
			leaf.setCurrent(false);
		}
		if (currentSong == null) {
			return null;
		}
		PlaylistNode currentNode = newPlaylistNode();
		PlaylistLeaf leaf = getPlaylistLeaf(currentSong);
		leaf.setCurrent(true);
		currentNode.getLeafs().add(leaf);
		for (PlaylistNode node : playlist.getNodes()) {
			for (PlaylistLeaf l : node.getLeafs()) {
				if (l.getId() == leaf.getId()) {
					currentNode.setType(node.getType());
					currentNode.setImage(node.getImage());
					currentNode.setName(node.getName());
				}
			}
		}
		return currentNode;
	}

	private PlaylistLeaf getPlaylistLeaf(MPDSong currentSong) {
		if (playlist == null) {
			return null;
		}
		for (PlaylistLeaf leaf : playlist.getLeafs()) {
			if (leaf.getUri().equals(currentSong.getFile())) {
				return leaf;
			}
		}
		return null;
	}

	private PlaylistNode newPlaylistNode() {
		PlaylistNode node = new PlaylistNode();
		node.setLeafs(new ArrayList<PlaylistLeaf>());
		return node;
	}

	private String toState(org.bff.javampd.player.Player.Status status) {
		if (org.bff.javampd.player.Player.Status.STATUS_PLAYING.equals(status)) {
			return State.playing.toString();
		} else if (org.bff.javampd.player.Player.Status.STATUS_PAUSED.equals(status)) {
			return State.paused.toString();
		}
		return State.stopped.toString();
	}

	public void playPlaylistLeaf(long leafId) {
		PlaylistLeaf leaf = playlist.getLeaf(leafId);
		if (leaf == null || songs == null || songs.isEmpty()) {
			return;
		}
		MPDSong leafSong = getMPDSong(leaf);
		if (leafSong != null) {
			mpd.getPlayer().playSong(leafSong);
		}
	}

	private MPDSong getMPDSong(PlaylistLeaf leaf) {
		for (MPDSong song : songs) {
			if (song.getFile().equals(leaf.getUri())) {
				return song;
			}
		}
		return null;
	}

	public void playPlaylistNode(long nodeId) {
		PlaylistNode node = playlist.getNode(nodeId);
		if (node == null || !node.hasLeaf() || songs == null || songs.isEmpty()) {
			return;
		}
		for (MPDSong song : songs) {
			if (song.getFile().equals(node.getLeafs().get(0).getUri())) {
				mpd.getPlayer().playSong(song);
			}
		}
	}

	public void playPlaylist() {
		mpd.getPlayer().play();
	}

	public void inEnqueue(Album album, boolean isPlay) {
		PlaylistNode newNode = createNode(album);
		addPlaylistNode(newNode);
		List<MPDSong> newSongs = new ArrayList<>();
		for (Song song : album.getSongs()) {
			MPDSong mpdSong = new MPDSong(song.getUri(), song.getName());
			newSongs.add(mpdSong);
		}
		mpd.getPlaylist().addSongs(newSongs);
		if (isPlay) {
			playPlaylistNode(newNode.getId());
		}
	}

	private void addPlaylistNode(PlaylistNode node) {
		if (playlist.hasNode(node)) {
			return;
		}
		long maxNodeId = playlist.getNodeIndex();
		node.setId(maxNodeId + 1);
		long maxLeafId = playlist.getLeafIndex();
		int nodeLeafs = node.getLeafs().size();
		for (int i = 0; i < nodeLeafs; i++) {
			long leafId = maxLeafId + i + 1;
			node.getLeafs().get(i).setId(leafId);
			playlist.setLeafIndex(leafId);
		}
		playlist.getNodes().add(node);
		playlist.setNodeIndex(node.getId());
	}

	private PlaylistNode createNode(Album album) {
		PlaylistNode node = new PlaylistNode();
		node.setName(album.getTitle());
		node.setType(PlaylistNode.TYPE.album.toString());
		node.setImage(album.getImage());
		List<PlaylistLeaf> leafs = new ArrayList<PlaylistLeaf>();
		for (Song albumItem : album.getSongs()) {
			leafs.add(createLeaf(albumItem));
		}
		node.setLeafs(leafs);
		return node;
	}

	private PlaylistLeaf createLeaf(Song albumItem) {
		PlaylistLeaf leaf = new PlaylistLeaf();
		leaf.setName(albumItem.getTitle());
		leaf.setType(PlaylistLeaf.TYPE.file.toString());
		leaf.setImage(albumItem.getImage());
		leaf.setUri(albumItem.getUri());
		leaf.setArtists(albumItem.getArtists());
		leaf.setAuthor(albumItem.getAuthor());
		return leaf;
	}

	private PlaylistNode createNode(String dir, Collection<MPDFile> files) {
		PlaylistNode node = new PlaylistNode();
		node.setName(dir);
		node.setType(PlaylistNode.TYPE.dir.toString());
		List<PlaylistLeaf> leafs = new ArrayList<PlaylistLeaf>();
		for (MPDFile file : files) {
			leafs.add(createLeaf(file.getPath()));
		}
		node.setLeafs(leafs);
		return node;
	}

	private PlaylistNode createNode(String file) {
		PlaylistNode node = new PlaylistNode();
		if (file.contains("/")) {
			node.setName(file.substring(0, file.lastIndexOf("/")));
		}
		else {
			node.setName("/");
		}
		node.setType(PlaylistNode.TYPE.dir.toString());
		List<PlaylistLeaf> leafs = new ArrayList<PlaylistLeaf>();
		leafs.add(createLeaf(file));
		node.setLeafs(leafs);
		return node;
	}

	private PlaylistLeaf createLeaf(String file) {
		PlaylistLeaf leaf = new PlaylistLeaf();
		leaf.setName(file);
		leaf.setUri(file);
		leaf.setType(PlaylistLeaf.TYPE.file.toString());
		return leaf;
	}

	public void inEnqueue(String file, boolean isDirectory, boolean isPlay) {
		PlaylistNode newNode;
		if (isDirectory) {
			MPDFile dir = new MPDFile(file);
			dir.setDirectory(true);
			Collection<MPDFile> files = mpd.getMusicDatabase().getFileDatabase().listDirectory(dir);
			newNode = createNode(file, files);
			addPlaylistNode(newNode);
		} else {
			newNode = createNode(file);
			addPlaylistNode(newNode);
		}
		List<MPDSong> newSongs = new ArrayList<>();
		for (PlaylistLeaf leaf : newNode.getLeafs()) {
			MPDSong mpdSong = new MPDSong(leaf.getUri(), leaf.getName());
			newSongs.add(mpdSong);
		}
		mpd.getPlaylist().addSongs(newSongs);
		if (isPlay) {
			playPlaylistNode(newNode.getId());
		}
	}	

	public void inEnqueue(YoutubeVideo video, boolean isPlay) {
		PlaylistNode newNode = createNode(video);
		addPlaylistNode(newNode);
		List<MPDSong> newSongs = new ArrayList<>();
		for (PlaylistLeaf leaf : newNode.getLeafs()) {
			MPDSong mpdSong = new MPDSong(leaf.getUri(), leaf.getName());
			newSongs.add(mpdSong);
		}
		mpd.getPlaylist().addSongs(newSongs);
		if (isPlay) {
			playPlaylistNode(newNode.getId());
		}
	}

	private PlaylistNode createNode(YoutubeVideo youtubeVideo) {
		PlaylistNode node = new PlaylistNode();
		node.setType(PlaylistNode.TYPE.youtube.toString());
		List<PlaylistLeaf> leafs = new ArrayList<PlaylistLeaf>();
		PlaylistLeaf leaf = new PlaylistLeaf();
		leaf.setName(youtubeVideo.getTitle());
		leaf.setType(PlaylistLeaf.TYPE.youtube.toString());
		leaf.setUri(youtubeVideo.getUri());
		leafs.add(leaf);
		node.setLeafs(leafs);
		return node;
	}

	public void playNext() {
		mpd.getPlayer().playNext();
	}

	public void plRepeat() {
		if (mpd.getPlayer().isRepeat()) {
			mpd.getPlayer().setRepeat(false);
		}
		mpd.getPlayer().setRepeat(true);
	}

	public void seekFw() {
		mpd.getPlayer().seek(mpd.getPlayer().getElapsedTime() + 10);
	}

	public void seekRw() {
		mpd.getPlayer().seek(mpd.getPlayer().getElapsedTime() - 10);
	}

	public void pause() {
		mpd.getPlayer().pause();
	}

	public void play() {
		mpd.getPlayer().play();
	}

	public void stop() {
		mpd.getPlayer().stop();
	}

	public void mute() {
		mpd.getPlayer().mute();
	}

	public void setVolume(int vol) {
		mpd.getPlayer().setVolume(vol);
	}

	public int getVolume() {
		if (status == null) {
			return mpd.getPlayer().getVolume();
		}
		return status.getVolume();
	}

	public void emtyPlaylist() {
		playlist.getNodes().clear();
		mpd.getPlaylist().clearPlaylist();
	}

	public void randomizePlay() {
		mpd.getPlayer().randomizePlay();
	}

	public void playPrevious() {
		mpd.getPlayer().playPrevious();
	}

	public void deleteLeaf(long leafId) {
		PlaylistLeaf leaf = playlist.getLeaf(leafId);
		if (leaf != null) {
			MPDSong song = getMPDSong(leaf);
			if (song != null) {
				mpd.getPlaylist().removeSong(song);
			}
			for (PlaylistNode node : playlist.getNodes()) {
				for (PlaylistLeaf l : node.getLeafs()) {
					if (l.getId() == leaf.getId()) {
						node.getLeafs().remove(l);
						if (node.getLeafs().isEmpty()) {
							playlist.getNodes().remove(node);
						}
						break;
					}
				}
			}
		}
	}

	public void deleteNode(long nodeId) {
		for (PlaylistNode node : playlist.getNodes()) {
			if (node.getId() == nodeId) {
				for (int i = 0; i < node.getLeafs().size();) {
					MPDSong song = getMPDSong(node.getLeafs().get(i));
					if (song != null) {
						mpd.getPlaylist().removeSong(song);
					}
					node.getLeafs().remove(i);
				}
				break;
			}
		}
	}

	public List<MediaFile> browseFiles(String dir) {
		if (dir != null && dir.endsWith("/..")) {
			dir = dir.substring(0, dir.lastIndexOf("/"));
			if (dir.contains("/")) {
				dir = dir.substring(0, dir.lastIndexOf("/"));
			}
			else {
				dir = ""; // root
			}
		}
		if (dir == null || dir.isEmpty()) {
			Collection<MPDFile> files = mpd.getMusicDatabase().getFileDatabase().listRootDirectory();
			return getMediaFiles("", files);
		}
		MPDFile folder = new MPDFile(dir);
		folder.setDirectory(true);
		Collection<MPDFile> files = mpd.getMusicDatabase().getFileDatabase().listDirectory(folder);
		return getMediaFiles(dir, files);
	}

	private static List<MediaFile> getMediaFiles(String dir, Collection<MPDFile> files) {
		
		List<MediaFile> mediaFiles = new ArrayList<>();

		MediaFile parent = new MediaFile();
		parent.setName("..");
		parent.setType("dir");
		mediaFiles.add(parent);
		
		if (dir == null || dir.isEmpty()) {
			parent.setPath("");
		}
		else {
			parent.setPath(dir + "/..");
		}
		
		for (MPDFile f : files) {
			MediaFile mFile = new MediaFile();
			mFile.setName(f.getPath());
			mFile.setPath(f.getPath());
			if (f.isDirectory()) {
				mFile.setType("dir");
			} else {
				mFile.setType("file");
			}
			if (!".albums".equals(mFile.getName())) {
				mediaFiles.add(mFile);
			}
		}
		return mediaFiles;
	}

	public void updateFileDatabase() {
		commandExecutor.sendCommand("update");
	}
}
