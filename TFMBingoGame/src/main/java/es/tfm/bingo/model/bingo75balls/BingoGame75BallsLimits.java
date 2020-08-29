package es.tfm.bingo.model.bingo75balls;

import es.tfm.bingo.model.*;
import es.tfm.bingo.model.bingo75balls.BingoGame75BallsLimits;
import es.tfm.bingo.utils.*;


public class BingoGame75BallsLimits extends BingoGameLimits{
	
	private static BingoGame75BallsLimits bingoGame75BallsLimits;
	
	private int centralRow;
	private int centralColumn;

	public BingoGame75BallsLimits(){
		minRow = Bingo75NumberLimits.MIN_CARD_ROW.get();
		maxRow = Bingo75NumberLimits.MAX_CARD_ROW.get();
		
		minColumn = Bingo75NumberLimits.MIN_CARD_COLUMN.get();
		maxColumn = Bingo75NumberLimits.MAX_CARD_COLUMN.get();

	    minNumber = Bingo75NumberLimits.MIN_NUMBER.get();
	    maxNumber = Bingo75NumberLimits.MAX_NUMBER.get();
	    
	    numbersInRange = Bingo75NumberLimits.NUMBERS_IN_RANGE.get();
	    
	    centralRow = Bingo75NumberLimits.CENTRAL_SQUARE.get();
	    centralColumn = Bingo75NumberLimits.CENTRAL_SQUARE.get();
	}
	
	
	public static BingoGame75BallsLimits instance() {
		if (bingoGame75BallsLimits == null) 
			bingoGame75BallsLimits = new BingoGame75BallsLimits();

		return bingoGame75BallsLimits;
	}

	
	public int getCentralRow() {
		return centralRow;
	}

	
	public int getCentralColumn() {
		return centralColumn;
	}	
}
