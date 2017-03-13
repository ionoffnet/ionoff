package net.ionoff.player.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AppUtil {
	
	public static String getCurrentDir() {
		String dir = ClassLoader.getSystemClassLoader().getResource(".").getPath();
		URL source = AppUtil.class.getProtectionDomain().getCodeSource().getLocation();
		if (dir.equals(source.getPath())) {
			return new File(dir).getParent();
		}
		return dir;
	}
	
	public static String getUserHomeDir() {
		return System.getProperty("user.home");
	}
	
	public static String getAppDataFile() throws IOException {
		String appDataFile = System.getProperty("user.home") + "/.appdata/imp.properties";
		File file = new File(appDataFile);
		if (!file.exists()) {
			mkDir(file.getParentFile());
			file.createNewFile();
		}
		return appDataFile;
	}
	
	public static void mkDir(File dir) {
		File parent = dir.getParentFile();
		if (parent.exists()) {
			dir.mkdir();
		}
		else {
			try {
				mkDir(parent);
			}
			catch (Exception e) {
				// ignore
			}
			mkDir(dir);
		}
	}
}
