package net.ionoff.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import net.ionoff.player.config.AppConfig;
import net.ionoff.player.config.AppUtil;
import net.ionoff.player.model.Schedule;
import net.ionoff.player.util.TimeUtil;

public class AppProperties {
	
	private static final Logger LOGGER = Logger.getLogger(AppProperties.class.getName());
	
	private static String playerName;
	private static String randomPassword;
	private static int volume = -1;
	private static Schedule schedule;
	private static Gson gson = new Gson();

	public static String getLocalAddress() throws Exception {
		if (isWinPlatform()) {
			return InetAddress.getLocalHost().getHostAddress();
		}
		
		final NetworkInterface ni = NetworkInterface.getByName("eth0");
		final Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

		while (inetAddresses.hasMoreElements()) {
			final InetAddress ia = inetAddresses.nextElement();
			if (!ia.isLinkLocalAddress()) {
				return ia.getHostAddress();
			}
		}
		throw new Exception("Unknown platform");
	}
	
	public static boolean isWinPlatform() {
		final String os = System.getProperty("os.name");
		return os.contains("Win");
	}
	
	public static String getUpdateFolder() {
		return AppUtil.getCurrentDir() + File.separator + "ext";
	}
	
	public static String getDowloadFolder() {
		return AppUtil.getCurrentDir() + File.separator + "dist";
	}
	 
	public static String getVersion() {
		return AppConfig.getInstance().VERSION;
	}

	public static String getRandomPassword() {
		if (randomPassword == null) {
			int pass = (int) (Math.random()*10000);
			randomPassword = pass + "";
		}
		return randomPassword;
	}

	public static void loadProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		int vol = -1;
		schedule = null;
		try {
			input = new FileInputStream(AppUtil.getAppDataFile());
			// load a properties file
			prop.load(input);
			vol = Integer.parseInt(prop.getProperty("volume"));
			String scheduleJson = prop.getProperty("schedule");
			schedule = gson.fromJson(scheduleJson, Schedule.class);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			volume = vol;
			if (schedule != null) {
				schedule = newSchedule();
			}
		}
	}
	

	public static Schedule newSchedule() {
		Schedule schedule = new Schedule();
		schedule.setDateTime(TimeUtil.formatScheduleDateTime(new Date()));
		schedule.setAction(Schedule.NONE);
		return schedule;
	}

	public static void setVolume(int vol) {
		volume = vol;
	}
	
	public static int getVolume() {
		return volume;
	}
	
	@SuppressWarnings("resource")
	public static void writeProperties() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(AppUtil.getAppDataFile());
			// set the properties value
			prop.setProperty("volume", volume + "");
			prop.setProperty("schedule", gson.toJson(schedule));
			output = new FileOutputStream(AppUtil.getAppDataFile());
			// save properties to project root folder
			prop.store(output, "");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public static String getAppSettingFile() {
		return playerName;
	}

	public static Schedule getSchedule() {
		if (schedule == null) {
			schedule = newSchedule();
		}
		return schedule;
	}
}
