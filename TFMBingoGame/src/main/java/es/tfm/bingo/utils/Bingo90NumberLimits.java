package es.tfm.bingo.utils;

public enum Bingo90NumberLimits {
	MIN_NUMBER(1),
	MAX_NUMBER(90),
	
	MIN_CARD_ROW(1),
	MAX_CARD_ROW(3),
	
	MIN_CARD_COLUMN(1),
	MAX_CARD_COLUMN(9),
	
	WHITES_PER_ROW(4),
	
	NUMBERS_IN_RANGE(10);
	
    private final int limit;
    
    private Bingo90NumberLimits(int limit) {
        this.limit = limit;
    }

    public int get() {
        return this.limit;
    }
}
