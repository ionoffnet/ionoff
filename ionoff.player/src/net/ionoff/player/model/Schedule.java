package net.ionoff.player.model;

public class Schedule {
	
	public static final String NONE = "none";
	public static final String STOP = "stop";
	public static final String SHUTDOWN = "shutdown";
	
	private String action;
	private String dateTime;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		if (STOP.equals(action) || SHUTDOWN.equals(action)) {
			this.action = action;
		}
		else {
			this.action = NONE;
		}
	}
	
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}
