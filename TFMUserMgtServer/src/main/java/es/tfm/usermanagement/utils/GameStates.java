package es.tfm.usermanagement.utils;

public enum GameStates {
	INITIAL("INITIAL"),
	IN_GAME("IN_GAME"),
	FINISHED("FINISHED");
	
    private final String value;
    
    private GameStates(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
    
    public boolean equals(String valueToCheck) {
    	return this.value.equals(valueToCheck);
    }
}