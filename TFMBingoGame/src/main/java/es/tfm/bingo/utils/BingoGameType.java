package es.tfm.bingo.utils;

public enum BingoGameType {
	BINGO_75_BALLS("BINGO75"),
	BINGO_90_BALLS("BINGO90");
	
    private final String value;
    
    private BingoGameType(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
    
    public boolean equals(String valueToCheck) {
    	return this.value.equals(valueToCheck);
    }
}
