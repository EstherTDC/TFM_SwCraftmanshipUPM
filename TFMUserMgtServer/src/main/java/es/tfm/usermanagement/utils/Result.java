package es.tfm.usermanagement.utils;

public enum Result {
	OK("OK"),
	ADMIN_DOES_NOT_BELONG_TO_GROUP("ADMIN DOES NOT BELONG TO GROUP"),	
	ERROR_UPON_GAME_CREATION("ERROR UPON GAME CREATION"),
	ERROR_UPON_JOINING_PLAYER_TO_GAME("ERROR UPON JOINING PLAYER TO GAME"),
	GAME_ALREADY_EXISTS("GAME ALREADY EXISTS"),
	GAME_ALREADY_STARTED("GAME ALREADY STARTED. NO PLAYER CAN BE ADDED"),
	GAME_ONGOING_CANNOT_BE_DELETED("GAME ONGOING. CAN NOT BE DELETED"),
	GAME_NOT_FOUND("GAME NOT FOUND"),
	GROUP_ALREADY_EXISTS("GROUP ALREADY EXISTS"),
	GROUP_NOT_FOUND("GROUP NOT FOUND"),
	NO_MINIMUM_NUMBER_OF_PLAYERS_IN_GAME("NO MINIMUM NUMBER OF PLAYERS IN GAME"),
	PLAYER_NOT_FOUND("PLAYER NOT FOUND"),
	PLAYER_ALREADY_IN_GROUP("PLAYER ALREADY BELONGS TO GROUP"),
	PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GROUP_ADMIN("PLAYER CANNOT BE DELETED FROM GROUP IS GROUP ADMIN"),
	PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GAME_ADMIN("PLAYER CANNOT BE DELETED FROM GROUP IS GAME ADMIN"),	
	PLAYER_DOES_NOT_BELONG_TO_GROUP("PLAYER DOES NOT BELONG TO GROUP"),	
	PLAYER_DOES_NOT_BELONG_TO_GAME("PLAYER DOES NOT BELONG TO GAME"),
	PROPOSED_ADMIN_DOES_NOT_BELONG_TO_GROUP("PROPOSED ADMIN DOES NOT BELONG TO GROUP"),
	USER_ALREADY_EXISTS("USER ALREADY EXISTS"),
	USER_CANNOT_BE_DELETED_IS_ADMIN("USER CANNOT BE DELETED. IS GROUP OR GAME ADMIN"),
	USER_DOES_NOT_BELONG_TO_GROUP("USER DOES NOT BELONG TO GROUP"),
	USER_NOT_FOUND("USER NOT FOUND");

	
    private final String value;
    
    private Result(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
    
    public boolean equals(String valueToCheck) {
    	return this.value.equals(valueToCheck);
    }
}
