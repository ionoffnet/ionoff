package net.ionoff.player.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import net.ionoff.player.config.UserConfig;

public class InformStatusThread extends Thread {
	private static final Logger LOGGER = Logger.getLogger(InformStatusThread.class.getName());
	private static final long INTERVAL = 50000; // 50 seconds
	
	private static InformStatusThread instance;
	
	@Override 
	public void run() {
		for (; true; ) {
			try {
				sleep(INTERVAL);
				sendStatusToCenter();
			}
			catch (Throwable t) {
				LOGGER.error(t.getMessage(), t);
			}
		}
	}
	
	private void sendStatusToCenter() throws UnknownHostException, IOException {
		Socket socket = new Socket(UserConfig.getInstance().SERVER_HOST,
				UserConfig.getInstance().SERVER_PORT);
		socket.setSoTimeout(4*1000); // 4 seconds
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// -----Step 3
		final JSONObject param = new JSONObject();

		param.put("mac", UserConfig.getInstance().LICENSE_KEY);
		param.put("status", "running");
		writer.println(param.toString());
		
		final String received = reader.readLine();
		LOGGER.debug("Response from center: " + received);
		
		if (writer != null) {
			writer.close();
		}
		if (reader != null) {
			reader.close();
		}
		if (socket != null) {
			socket.close();
		}
		
	}

	public static InformStatusThread getInstance() {
		if (instance == null) {
			instance = new InformStatusThread();
		}
		return instance;
	}
}
