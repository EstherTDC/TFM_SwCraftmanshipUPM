package es.tfm.bingo.model.bingo90balls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import es.tfm.bingo.model.*;
import es.tfm.bingo.utils.ClosedInterval;

@Entity
public class BingoCard90Balls extends BingoCard{

		
	public BingoCard90Balls() {
		super(BingoGame90BallsLimits.instance());
	}
	
	
	@Override
	protected Map<Coordinate,Integer> generateCardNumbers(){
		// First column is special because the range of numbers is 1 to 9 (a range of 9 numbers)
		List<Integer> generatedNumbers = new ClosedInterval(bingoLimits.getMinNumber(),
				                                            bingoLimits.getNumbersInRange()-1).
				                                            generateXNumbersInInterval(bingoLimits.getMaxColumn());
		
		Map<Coordinate,Integer> card = new HashMap<Coordinate,Integer>();
		
		for (int i = 0; i< bingoLimits.getMaxRow(); i++)
	    	card.put(new Coordinate(bingoLimits.getMinRow()+i,bingoLimits.getMinColumn()),generatedNumbers.get(i));
	       
		// Intermediate columns have a range of 10 numbers (from x0 to x9)
	    for (int i = bingoLimits.getMinColumn()+1; i<=bingoLimits.getMaxColumn()-1; i++) {
            generatedNumbers = new ClosedInterval((i-1)*bingoLimits.getNumbersInRange(),
            		                              (i*bingoLimits.getNumbersInRange())-1).
            		                              generateXNumbersInInterval(bingoLimits.getMaxRow());
            
    		for (int j = 0; j< bingoLimits.getMaxRow(); j++)
    	    	card.put(new Coordinate(bingoLimits.getMinRow()+j,i),generatedNumbers.get(j));                
        }

	    // Last column is also special because the range of numbers is 80-90 (a range of 11 numbers)
        generatedNumbers = new ClosedInterval(bingoLimits.getMaxNumber()-bingoLimits.getNumbersInRange(),
        		                              bingoLimits.getMaxNumber()).
        		                              generateXNumbersInInterval(bingoLimits.getMaxRow());

		for (int i = 0; i< bingoLimits.getMaxRow(); i++)
    		card.put(new Coordinate(bingoLimits.getMinRow()+i,bingoLimits.getMaxColumn()),generatedNumbers.get(i));
		
	    return card;
	}
	

	@Override
	protected Map<Coordinate,Integer> setWhitePositions(Map<Coordinate,Integer> card){

		List<Integer> whiteYPositions = new ArrayList<Integer>();
		
		int whitesPerRow = BingoGame90BallsLimits.instance().getWhitesPerRow();
		
		for (int i = bingoLimits.getMinRow();i <= bingoLimits.getMaxRow(); i++) {
			whiteYPositions = new ClosedInterval(bingoLimits.getMinColumn(),
					                             bingoLimits.getMaxColumn()). 
					                             generateXNumbersInInterval(whitesPerRow);
			
			for (int j = 0 ; j < whitesPerRow; j++) {
				card.put(new Coordinate(i,whiteYPositions.get(j)), 0);
			}
		}
		
		return card;
	}
	
	
	@Override
	protected BingoCard provideBaseCard() {
		return new BingoCard90Balls();
	}


	@Override
	protected Square provideSquare(int coordX,int coordY,int number) {
		return new SquareCard90Balls(coordX,coordY,number);
	}
	
	
	@Override
	protected void checkSquareTypeCorrectness(Square square) {
    	assert square.getClass() == SquareCard90Balls.class;
	}	
}
