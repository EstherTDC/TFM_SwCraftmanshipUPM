package es.tfm.bingo.model.bingo75balls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import es.tfm.bingo.model.*;
import es.tfm.bingo.utils.ClosedInterval;

@Entity
public class BingoCard75Balls extends BingoCard {

	public BingoCard75Balls() {
		super(BingoGame75BallsLimits.instance());
	}
	
	
	@Override
	protected Map<Coordinate,Integer> generateCardNumbers(){
	       
		Map<Coordinate,Integer> card = new HashMap<Coordinate,Integer>();
		List<Integer> generatedNumbers = null;
		
	    for (int i = bingoLimits.getMinColumn(); i<=bingoLimits.getMaxColumn(); i++) {
	    	generatedNumbers = new ClosedInterval(((i-1)*bingoLimits.getNumbersInRange())+1,
	    			                                i*bingoLimits.getNumbersInRange()).
	    			                                generateXNumbersInInterval(bingoLimits.getMaxRow());
            
    		for (int j = 0; j< bingoLimits.getMaxRow(); j++)
    	    	card.put(new Coordinate(bingoLimits.getMinRow()+j,i),generatedNumbers.get(j));                
        }
	    	    
	    return card;
	}
	
	
	@Override
	protected Map<Coordinate,Integer> setWhitePositions(Map<Coordinate,Integer> card){

		card.put(new Coordinate(BingoGame75BallsLimits.instance().getCentralRow(),
				                BingoGame75BallsLimits.instance().getCentralColumn()),0);

		return card;
	}
	
	
	@Override
	protected BingoCard provideBaseCard() {
		return new BingoCard75Balls();
	}
	
	
	@Override
	protected Square provideSquare(int coordX,int coordY,int number) {
		return new SquareCard75Balls(coordX,coordY,number);
	}
	
	
	@Override
	protected void checkSquareTypeCorrectness(Square square) {
    	assert square.getClass() == SquareCard75Balls.class;
	}	
}
