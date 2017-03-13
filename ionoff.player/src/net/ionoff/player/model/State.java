package net.ionoff.player.model;

public enum State {
	
    nothing_special(0), opening (1), buffering (2), playing (3), paused (4), stopped (5), ended (6), error (7), finished(8);
	
	private int value;
	
	private State(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static State getState(int stateValue) {
		for (State state : State.values()) {
			if (state.getValue() == stateValue) {
				return state;
			}
		}
		return null;
	}
	
	public static State getState(String stateName) {
		for (State state : State.values()) {
			if (state.toString().equals(stateName)) {
				return state;
			}
		}
		return null;
	}
}