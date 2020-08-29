package es.tfm.bingo.utils;

public enum Bingo75NumberLimits {
	MIN_NUMBER(1),
	MAX_NUMBER(75),
	
	MIN_CARD_ROW(1),
	MAX_CARD_ROW(5),
	
	MIN_CARD_COLUMN(1),
	MAX_CARD_COLUMN(5),
	
	CENTRAL_SQUARE(3),
	
	NUMBERS_IN_RANGE(15);
	
    private final int limit;
    
    private Bingo75NumberLimits(int limit) {
        this.limit = limit;
    }

    public int get() {
        return this.limit;
    }
}
