package net.ionoff.player.thread;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.log4j.Logger;

import net.ionoff.player.AppProperties;
import net.ionoff.player.FileManager;
import net.ionoff.player.HttpRequestUtil;
import net.ionoff.player.config.AppConfig;
import net.ionoff.player.config.AppUtil;

public class LatestVersionDownloader extends Thread {
	
	private static final Logger LOGGER = Logger.getLogger(LatestVersionDownloader.class.getName());
	
	private static final long INTERVAL = 3600000; // 1 hour

	private static LatestVersionDownloader instance;

	@Override
	public void run() {
		for (; true;) {
			try {
				dowloadLatestVersion();
				sleep(INTERVAL);
			} catch (Throwable t) {
				LOGGER.error(t.getMessage(), t);
				try {
					sleep(INTERVAL);
				} catch (InterruptedException e) {
					LOGGER.error(t.getMessage(), t);
				}
			}
		}
	}

	private void dowloadLatestVersion() throws IOException {
		String latestVersion = retrieveLatestVersion();
		if (AppProperties.getVersion().equals(latestVersion)) {
			LOGGER.info("Current version is the latest version");
			return;
		}
		String targetDirectory = AppProperties.getDowloadFolder();
		File targetDir = new File(targetDirectory);
		if (targetDir.exists() && targetDir.isDirectory()) {
			for (File f : targetDir.listFiles()) {
				if (f.getName().equals(latestVersion + ".zip")) {
					LOGGER.info("The latest version was downloaded");
					return;
				}
			}
		}
		LOGGER.info("Downloading new version: " + latestVersion);
		if (!targetDir.exists()) {
			AppUtil.mkDir(targetDir);
		}
		String sourceUrl = AppConfig.getInstance().UPDATE_SITE + "/" + latestVersion + ".zip";
		URL url = new URL(sourceUrl);
		String fileName = latestVersion + ".zip";
		Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
		Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		
		
		String updateFolder = AppProperties.getUpdateFolder();
		FileManager.deleteFile(updateFolder);
		LOGGER.info("Extracting file " + latestVersion + ".zip to " + updateFolder);
		FileManager.unzip(targetPath.toString(), updateFolder);
	}
	
	private String retrieveLatestVersion() throws IOException {
		String sourceUrl = AppConfig.getInstance().UPDATE_SITE + "/latest.json";
		return HttpRequestUtil.sendHttpGETRequest(sourceUrl);
	}

	public static LatestVersionDownloader getInstance() {
		if (instance == null) {
			instance = new LatestVersionDownloader();
		}
		return instance;
	}
}
