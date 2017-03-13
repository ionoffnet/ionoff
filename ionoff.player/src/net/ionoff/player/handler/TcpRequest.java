package net.ionoff.player.handler;

import java.util.Map;

import org.json.JSONException;

import com.google.gson.Gson;

public class TcpRequest {
	
	private Gson gson;
	private Map<String, String> params;
	
	@SuppressWarnings("unchecked")
	public TcpRequest(String request) throws JSONException {
		gson = new Gson();
		params = gson.fromJson(request, Map.class);
	}

	public String getContext() throws JSONException {
		return params.get("context");
	}

	public String getCommand() throws JSONException {
		return params.get("command");
	}
	
	public String getParameter(String parametter) throws JSONException {
		return params.get(parametter);
	}
	
	public Map<String, String> getParameters() {
		return params;
	}
}
