package es.tfm.bingo.model.bingo90balls;

import es.tfm.bingo.model.*;
import es.tfm.bingo.utils.*;


public class BingoGame90BallsLimits extends BingoGameLimits{
	
	private static BingoGame90BallsLimits bingoGame90BallsLimits;

	private int whitesPerRow;
	
	public BingoGame90BallsLimits(){
		minRow = Bingo90NumberLimits.MIN_CARD_ROW.get();
		maxRow = Bingo90NumberLimits.MAX_CARD_ROW.get();
		
		minColumn = Bingo90NumberLimits.MIN_CARD_COLUMN.get();
		maxColumn = Bingo90NumberLimits.MAX_CARD_COLUMN.get();

	    minNumber = Bingo90NumberLimits.MIN_NUMBER.get();
	    maxNumber = Bingo90NumberLimits.MAX_NUMBER.get();
	    
	    numbersInRange = Bingo90NumberLimits.NUMBERS_IN_RANGE.get();
	    
	    whitesPerRow = Bingo90NumberLimits.WHITES_PER_ROW.get();
	}
	
	public static BingoGame90BallsLimits instance() {
		if (bingoGame90BallsLimits == null) 
			bingoGame90BallsLimits = new BingoGame90BallsLimits();

		return bingoGame90BallsLimits;
	}
	
	public int getWhitesPerRow() {
		return whitesPerRow;
	}
}
