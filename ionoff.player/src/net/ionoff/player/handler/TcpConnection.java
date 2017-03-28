package net.ionoff.player.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import net.ionoff.player.AppProperties;
import net.ionoff.player.config.UserConfig;
import net.ionoff.player.exception.PlayerException;

public class TcpConnection extends Thread {

	private static final Logger LOGGER = Logger.getLogger(TcpConnection.class.getName());

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private String status;
	private RestartConnectThread restartConnectThread;
	
	private static final String REJECTED =   "Rejected!";
	private static final String CONNECTING =   "Connecting";
	private static final String CONNECTED =    "Connected";
	private static final String DISCONNECTED = "Disconnected";

	@Override
	public void run() {
		restartConnectThread = new RestartConnectThread();
		restartConnectThread.startListen();
		restartConnectThread.start();
		restart();
	}

	private void restart() {
		close();
		connect();
		for (; true; ) {
			try {
				listen();
			}
			catch (final Throwable t) {
				if (t instanceof SocketException) {
					LOGGER.error(t);
					informStatus(DISCONNECTED);
					close();
					break;
				}
				LOGGER.error(t.getMessage(), t);
			}
		}
		restart();
	}

	private void listen() throws IOException {
		restartConnectThread.startListen();
		final String request = reader.readLine();
		final String response = handleTcpRequest(request);
		writer.println(response);
	}

	private synchronized String handleTcpRequest(String request) throws SocketException {
		try {
			if (request == null) {
				throw new SocketException("Request is null");
			}
			final TcpRequest tcpRequest = new TcpRequest(request);
			if (tcpRequest.getParameters() == null) {
				throw new SocketException("Request parametter is null");
			}
			return new RequestHandler(tcpRequest).handleRequest();
		}
		catch (final Throwable e) {
			if (e instanceof PlayerException) {
				LOGGER.error(e.getMessage());
			}
			else {
				LOGGER.error(e.getMessage(), e);
				throw new SocketException();
			}
			return new PlayerResponse(e.getMessage(), e).toJSONString();
		}
	}

	public void connect() {
		try {
			informStatus(CONNECTING);

			socket = new Socket(UserConfig.getInstance().SERVER_HOST,
					UserConfig.getInstance().SERVER_PORT);

			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// -----Step 3

			final JSONObject param = new JSONObject();

			param.put("mac", UserConfig.getInstance().LICENSE_KEY);
			param.put("pass", AppProperties.getRandomPassword());
			param.put("password", UserConfig.getInstance().LOGIN_PASSWORD);

			writer.println(param.toString());

			final String received = reader.readLine();
			
			onRecievedResponse(received);
		}
		catch (final Throwable e) {
			try {
				sleep(10000);
			}
			catch (final InterruptedException ie) {
				LOGGER.error(ie);
			}
			LOGGER.error(e);
			connect();
		}
	}

	private void onRecievedResponse(String received) {
		LOGGER.info("Response: " + received);
		try {
			final JSONObject info = new JSONObject(received);
			if ("connected".equals(info.get("status"))) {
				informStatus(CONNECTED);
			}
			else {
				informStatus(REJECTED);
			}
		}
		catch (final JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void informStatus(String status) {
		LOGGER.info("Status: " + status + " (" + UserConfig.getInstance().SERVER_HOST + ":" + UserConfig.getInstance().SERVER_PORT + ")");
		this.status = status;
	}

	public void close() {
		try {
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
		catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public String getStatus() {
		return status;
	}

	public boolean isConected() {
		return CONNECTED.equals(status);
	}
	
	private class RestartConnectThread extends Thread {
		
		private long startListen;
		@Override
		public void run() {
			try {
				for (; true; ) {
					sleep(5000);
					long currentTime = System.currentTimeMillis();
					if ((currentTime - startListen) > 300000) { // 5mins
						close();
					}
				}
			}
			catch (Throwable t) {
				LOGGER.error(t.getMessage(), t);
			}
		}
		public void startListen() {
			startListen = System.currentTimeMillis();
		}
	}
}
