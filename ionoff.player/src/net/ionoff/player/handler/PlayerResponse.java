package net.ionoff.player.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class PlayerResponse {
	public static String ERROR = "error";
	
	private String status;
	private String message;
	private String object;	
	private String clazz;	
	private final Gson gson;
	
	public PlayerResponse(String message, Object obj) {
		this.gson = new Gson();
		this.message = message;
		this.clazz = obj.getClass().getSimpleName();
		if (obj instanceof Exception) {			
			this.status = "error";
		}
		else {
			this.status = "success";
			if (obj instanceof String) {
				this.object = (String)obj;
			}
			else {
				this.object = gson.toJson(obj);
			}
		}
	}
	
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("status", status);
			json.put("message", message);
			json.put("object", object);
			json.put("clazz", clazz);
			return json.toString();
		}
		catch (JSONException e) {
			return "{\"status\":\"error\",\"clazz\":\"JSONException\",\"message\":\"" + e.getMessage() + "\"}";
		}
	}
}
