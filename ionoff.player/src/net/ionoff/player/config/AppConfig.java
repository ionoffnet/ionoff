package net.ionoff.player.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppConfig {
	
	private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
	
	private static final AppConfig INSTANCE = new AppConfig();
	
	public final String UPDATE_SITE;
	public final String DATA_SERVER_URL;
	public String MPD_HOST;
	public int MPD_PORT;
	public boolean INTERVAL_UPDATE;
	public String VERSION;
	
	public AppConfig() {
		
		final Properties appProperties = new Properties();
		try {
			// load a properties file
			appProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		}
		catch (final IOException ex) {
			ex.printStackTrace();
		}
		UPDATE_SITE = appProperties.getProperty("update_site");
		DATA_SERVER_URL = appProperties.getProperty("data_server_url");
		MPD_HOST = appProperties.getProperty("mpd_host");
		try {
			MPD_PORT = Integer.parseInt(appProperties.getProperty("mpd_port"));
		}
		catch (Exception e) {
			MPD_PORT = 6600;
			LOGGER.info("MPD_PORT is not set. Use default value");
		}
		try {
			INTERVAL_UPDATE = Boolean.parseBoolean(appProperties.getProperty("interval_update"));
		}
		
		catch (Exception e) {
			INTERVAL_UPDATE = false;
			LOGGER.info("INTERVAL_UPDATE is not set. Use default value");
		}
		VERSION = appProperties.getProperty("version");
	}
	
	public static AppConfig getInstance() {
		return INSTANCE;
	}
}
