package net.ionoff.player.handler;

import java.util.Map;

import net.ionoff.player.AppProperties;
import net.ionoff.player.exception.DateTimeFormatException;
import net.ionoff.player.exception.UnknownCommandException;
import net.ionoff.player.model.Schedule;

public class ScheduleRequestHandler {
	
	public Schedule handleRequest(Map<String, String> parameters) throws UnknownCommandException, DateTimeFormatException {
		String command = parameters.get("command");
		return handleRequest(command, parameters);
	}
	
	private Schedule handleRequest(String command, Map<String, String> parameters) throws UnknownCommandException, DateTimeFormatException {
		if ("get".equals(command)) {
			return getSchedule();
		}
		if ("update".equals(command)) {
			return updateSchedule(parameters);
		}
		throw new UnknownCommandException(command);
	}
	
	private Schedule updateSchedule(Map<String, String> params) throws DateTimeFormatException {
		String action = params.get("action");
		String dateTime = params.get("dateTime");
		AppProperties.getSchedule().setAction(action);
		AppProperties.getSchedule().setDateTime(dateTime);
		AppProperties.writeProperties();
		return AppProperties.getSchedule();
	}

	private Schedule getSchedule() {
		return AppProperties.getSchedule();
	}
}
