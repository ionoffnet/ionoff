package net.ionoff.player.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import net.ionoff.player.DeamonPlayer;
import net.ionoff.player.config.UserConfig;
import net.ionoff.player.exception.DateTimeFormatException;
import net.ionoff.player.exception.MpdConnectException;
import net.ionoff.player.exception.UnknownCommandException;
import net.ionoff.player.exception.UnknownContextException;
import net.ionoff.player.model.Album;
import net.ionoff.player.model.MediaFile;
import net.ionoff.player.model.PlayLeaf;
import net.ionoff.player.model.PlayList;
import net.ionoff.player.model.PlayNode;
import net.ionoff.player.model.Schedule;
import net.ionoff.player.model.Song;
import net.ionoff.player.model.Status;
import net.ionoff.player.model.YoutubeVideo;

public class RequestHandler {
	private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

	public static final String PLAYLEAF = "playleaf";
	public static final String PLAYNODE = "playnode";
	public static final String PLAYLIST = "playlist";
	
	protected static final String COMMAND = "command";
	private static final String CONTEXT_STATUS = "/status";
	private static final String CONTEXT_PLAYLIST = "/playlist";
	private static final String CONTEXT_BROWSE = "/browse";
	private static final String CONTEXT_SCHEDULE = "/schedule";

	private final Map<String, String> params;

	RequestHandler(TcpRequest tcpRequest) {
		gson = new Gson();
		params = tcpRequest.getParameters();
	}

	protected Map<String, String> getParammeters(final TcpRequest tcpRequest) {
		return tcpRequest.getParameters();
	}

	public String handleRequest() throws Exception {
		return handleRequest(params);
	}

	private String handleRequest(Map<String, String> params) throws Exception {
		final String context = params.get(RequestContext.CONTEXT);
		params.remove(RequestContext.CONTEXT);

		if (context.equals(RequestContext.REQUESTS + CONTEXT_STATUS)) {
			return handleStatusRequest(params);
		}
		if (context.equals(RequestContext.REQUESTS + CONTEXT_PLAYLIST)) {
			return handlePlaylistRequest(params);
		}
		if (context.equals(RequestContext.REQUESTS + CONTEXT_BROWSE)) {
			return handleBrowseRequest(params);
		}
		if (context.equals(RequestContext.REQUESTS + CONTEXT_SCHEDULE)) {
			return handleScheduleRequest(params);
		}
		throw new UnknownContextException(context);
	}

	private String handleScheduleRequest(Map<String, String> params)
			throws UnknownCommandException, DateTimeFormatException {
		Schedule schedule = new ScheduleRequestHandler().handleRequest(params);

		return new PlayerResponse("schedule", schedule).toJSONString();
	}

	private String handleStatusRequest(final Map<String, String> params) throws MpdConnectException {
		String command = params.get("command");
		final Status status = handleStatusRequest(command, params);
		return new PlayerResponse("status", status).toJSONString();
	}

	private String handlePlaylistRequest(final Map<String, String> params) throws Exception {
		String command = params.get("command");
		if (!params.isEmpty() && "pl_update".equals(command)) {
			updatePlaylist(params);
		}
		PlayList playlist = getPlayer().getPlaylist();
		return new PlayerResponse("playlist", playlist).toJSONString();
	}

	private String handleBrowseRequest(final Map<String, String> params) throws Exception {
		List<MediaFile> files = getBrowse(params);
		return new PlayerResponse("files", files).toJSONString();
	}

	private List<MediaFile> getBrowse(Map<String, String> params) throws MpdConnectException {
		String dir = params.get("dir");
		String command = params.get("command");
		return getMediaFiles(dir, command);
	}

	private List<MediaFile> getMediaFiles(String dir, String command) throws MpdConnectException {
		if (dir == null || dir.isEmpty()) {
			if ("refresh".equals(command)) {
				getPlayer().updateFileDatabase();
			}
			List<MediaFile> mediaFiles = new ArrayList<>();
			MediaFile mFile = new MediaFile();
			mFile.setName(".albums");
			mFile.setPath(".albums");
			mFile.setType(MediaFile.TYPE.dir.toString());
			mediaFiles.add(mFile);
			mediaFiles.addAll(getPlayer().browseFiles(dir));
			return mediaFiles;
		}
		else if (".albums".equals(dir)) {
			List<MediaFile> mediaFiles = new ArrayList<>();
			File file = new File(UserConfig.getInstance().ROOT_BROWSE_DIR + File.separator + dir);

			if (!file.exists() || file.isFile()) {
				return mediaFiles;
			}
			MediaFile parent = new MediaFile();
			parent.setName("..");
			parent.setPath("");
			parent.setType(MediaFile.TYPE.dir.toString());
			mediaFiles.add(parent);

			for (File f : file.listFiles()) {
				MediaFile mFile = new MediaFile();
				mFile.setName(f.getName());
				mFile.setPath(f.getAbsolutePath());
				if (f.isDirectory()) {
					mFile.setType(MediaFile.TYPE.dir.toString());
				} else {
					mFile.setType(MediaFile.TYPE.file.toString());
				}
				if (mFile.isAlbum()) {
					mediaFiles.add(mFile);
				}
			}
			return mediaFiles;
		}
		else {
			if ("refresh".equals(command)) {
				getPlayer().updateFileDatabase();
			}
			return getPlayer().browseFiles(dir);
		}
	}

	private final Gson gson;
	private static DeamonPlayer deamonPlayer;

	private DeamonPlayer getPlayer() throws MpdConnectException {
		if (deamonPlayer == null) {
			deamonPlayer = new DeamonPlayer();
		}
		return deamonPlayer;
	}

	private Status handleStatusRequest(String command, Map<String, String> parameters) throws MpdConnectException {
		if (command == null || parameters.isEmpty()) {
			return getPlayer().loadStatus();
		}
		if ("pl_play".equals(command)) {
			pl_play(parameters);
		} else if ("in_play".equals(command)) {
			in_play(parameters);
		} else if ("in_enqueue".equals(command)) {
			in_enqueue(parameters, false);
		} else if ("pl_previous".equals(command)) {
			pl_previous(parameters);
		} else if ("pl_next".equals(command)) {
			pl_next(parameters);
		} else if ("pl_delete".equals(command)) {
			pl_delete(parameters);
		} else if ("pl_repeat".equals(command)) {
			pl_repeat(parameters);
		} else if ("seek".equals(command)) {
			seek(parameters);
		} else if ("pl_pause".equals(command)) {
			pl_pause(parameters);
		} else if ("pl_forceresume".equals(command)) {
			pl_forceresume(parameters);
		} else if ("pl_resume".equals(command)) {
			pl_resume(parameters);
		} else if ("pl_forcepause".equals(command)) {
			pl_forcepause(parameters);
		} else if ("pl_stop".equals(command)) {
			pl_stop(parameters);
		} else if ("pl_empty".equals(command)) {
			pl_empty(parameters);
		} else if ("volume".equals(command)) {
			volume(parameters);
		} else if ("pl_loop".equals(command)) {
			pl_loop(parameters);
		} else if ("pl_random".equals(command)) {
			pl_random(parameters);
		} else if ("fullscreen".equals(command)) {
			fullscreen(parameters);
		}
		return getPlayer().loadStatus();
	}

	private void fullscreen(Map<String, String> parameters) {
		return;
	}

	private void volume(Map<String, String> parameters) throws MpdConnectException {
		String val = parameters.get("val");
		if (val.equals("mute")) {
			getPlayer().mute();
		} else if (val.equals("+")) {
			int vol = getPlayer().getVolume() + 5;
			getPlayer().setVolume(vol);
		} else if (val.equals("-")) {
			int vol = getPlayer().getVolume() - 5;
			getPlayer().setVolume(vol);
		} else {
			getPlayer().setVolume(Integer.parseInt(val));
		}
	}

	private void pl_repeat(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().plRepeat();
	}

	private void pl_random(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().randomizePlay();
	}

	private void pl_loop(Map<String, String> parameters) {
		// does nothing // ???
	}

	private void pl_empty(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().emtyPlaylist();
		getPlayer().getPlaylist().setId(0L);
		getPlayer().getPlaylist().setName(null);
	}

	private void pl_stop(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().stop();
	}

	private void pl_forcepause(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().pause();
	}

	private void pl_resume(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().play();
	}

	private void pl_forceresume(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().play();
	}

	private void pl_pause(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().pause();
	}

	private void seek(Map<String, String> parameters) throws MpdConnectException {
		String val = parameters.get("val");
		if (val.equals("+")) {
			getPlayer().seekFw();
		} else if (val.equals("-")) {
			getPlayer().seekRw();
		}
	}

	private void pl_delete(Map<String, String> parameters) throws NumberFormatException, MpdConnectException {
		String id = parameters.get("id");
		String type = parameters.get("type");
		if (PLAYLEAF.equals(type)) {
			getPlayer().deleteLeaf(Long.parseLong(id));
		} else if (PLAYNODE.equals(type)) {
			getPlayer().deleteNode(Long.parseLong(id));
		}
	}

	private void pl_next(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().playNext();
	}

	private void pl_previous(Map<String, String> parameters) throws MpdConnectException {
		getPlayer().playPrevious();
	}

	private void in_enqueue(Map<String, String> parameters, boolean isPlay) throws MpdConnectException {
		String input = parameters.get("input");
		String inputType = parameters.get("input_type");
		
		if (PlayNode.TYPE.album.toString().equals(inputType)) {
			Album album = gson.fromJson(input, Album.class);
			addAlbumFile(album);
			getPlayer().inEnqueue(album, isPlay);
		} 
		else if (PlayNode.TYPE.dir.toString().equals(inputType)) {
			getPlayer().inEnqueue(input, true, isPlay);
		} 
		else if (MediaFile.TYPE.file.toString().equals(inputType)) { 
			getPlayer().inEnqueue(input, false, isPlay);
		}
		else if (PlayLeaf.TYPE.track.toString().equals(inputType)) {
			Song song = gson.fromJson(input, Song.class);
			getPlayer().inEnqueue(song, isPlay);
		}
		else if (PlayLeaf.TYPE.youtube.toString().equals(inputType)) {
			YoutubeVideo video = gson.fromJson(input, YoutubeVideo.class);
			getPlayer().inEnqueue(video, isPlay);
		}
		
		else if (PLAYLIST.equals(inputType)) {
			PlayList playlist = gson.fromJson(input, PlayList.class);
			getPlayer().inEnqueue(playlist, isPlay);
		}
		else if (PLAYNODE.equals(inputType)) {
			PlayNode playnode = gson.fromJson(input, PlayNode.class);
			getPlayer().inEnqueue(playnode, isPlay);
		}
		else if (PLAYLEAF.equals(inputType)) {
			PlayLeaf playleaf = gson.fromJson(input, PlayLeaf.class);
			getPlayer().inEnqueue(playleaf, isPlay);
		}
	}

	private void in_play(Map<String, String> parameters) throws MpdConnectException {
		in_enqueue(parameters, true);
	}

	private void addAlbumFile(Album album) {
		File albumFolder = new File(UserConfig.getInstance().getAlbumFolder());
		String albumFileName = getAlbumFileName(album);
		if (!albumFolder.exists()) {
			albumFolder.mkdir();
		}
		if (!hasAlbum(albumFolder, albumFileName)) {
			File file = new File(albumFolder + File.separator + albumFileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private boolean hasAlbum(File albumFolder, String albumFileName) {
		for (File file : albumFolder.listFiles()) {
			if (file.getName().equals(albumFileName)) {
				return true;
			}
		}
		return false;
	}

	private String getAlbumFileName(Album album) {
		return AccentRemover.removeAccent(album.getTitle()) + "#" + album.getId() + ".album";
	}


	private void pl_play(Map<String, String> parameters) throws MpdConnectException {
		String id = parameters.get("id");
		String type = parameters.get("type");
		if (PLAYLEAF.equals(type)) {
			getPlayer().playPlaylistLeaf(Long.parseLong(id));
		} else if (PLAYNODE.equals(type)) {
			getPlayer().playPlaylistNode(Long.parseLong(id));
		} else {
			getPlayer().playPlaylist();
		}
	}
	
	private void updatePlaylist(Map<String, String> params) throws MpdConnectException {
 		String plJson = params.get("playlist");
 		PlayList playlist = gson.fromJson(plJson, PlayList.class);
		PlayList playingList = getPlayer().getPlaylist();
		playingList.setId(playlist.getId());
		playingList.setName(playlist.getName());
		for (PlayNode node : playlist.getNodes()) {
			for (PlayNode playingNode : playingList.getNodes()) {
				if (playingNode.getIdx() == node.getIdx()) {
					updatePlayNode(playingNode, node);
				}
			}
		}
	}

	private void updatePlayNode(PlayNode playingNode, PlayNode node) {
		playingNode.setId(node.getId());
		for (PlayLeaf leaf : node.getLeafs()) {
			for (PlayLeaf playingLeaf : playingNode.getLeafs()) {
				if (playingLeaf.getIdx() == leaf.getIdx()) {
					playingLeaf.setId(leaf.getId());
				}
			}
		}
 	}
}
