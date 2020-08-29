package es.tfm.bingo.utils;

public enum ClaimedPrize {
	LINE("LINE"),
	BINGO("BINGO"),
	COLUMN("COLUMN"),
	DIAGONAL("DIAGONAL");
	
    private final String prize;
    
    private ClaimedPrize(String prize) {
        this.prize = prize;
    }

    public String get() {
        return this.prize;
    }
}
