package es.tfm.bingo.model.bingo75balls;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import es.tfm.bingo.model.*;
import es.tfm.bingo.utils.*;

@Entity
public class BingoGame75Balls extends BingoGame{
	
	public BingoGame75Balls() {}
	
	public BingoGame75Balls(Long id,String groupName,String gameName) {
        super(id,groupName,gameName,BingoGame75BallsLimits.instance());
		
        List<Ball> listOfBalls = new ArrayList<Ball>();

		for (int i=bingoLimits.getMinNumber(); i<=bingoLimits.getMaxNumber(); i++) {
			BallBingo75Balls ball = new BallBingo75Balls(gameName,i); 		
			listOfBalls.add(ball);
		}
		
		this.setBalls(listOfBalls);
	}
	
	@Override
	public void setBalls(List<Ball> balls) {
		for (Ball ball:this.balls)
			assert ball.getClass() == BallBingo75Balls.class;	
		
		if (this.balls == null) {
		    this.balls = balls;
		} else {
		    this.balls.retainAll(balls);
		    this.balls.addAll(balls);
		}		
	}
	
	
	@Override
	public void setBingoCards(List<BingoCard> bingoCards) {
		for (BingoCard bingoCard:this.bingoCards)
			assert bingoCard.getClass() == BingoCard75Balls.class;
		
		if (this.bingoCards == null) {
		    this.bingoCards = bingoCards;
		} else {
		    this.bingoCards.retainAll(bingoCards);
		    this.bingoCards.addAll(bingoCards);
		}
    }


	@Override
	public BingoCard generateAleatoryCard() {
		BingoCard75Balls card = new BingoCard75Balls();
		
		return card.generateAleatoryCard(this);
	}


	@Override
	public boolean isCorrectClaimedPrize(String prizeType,BingoCard bingoCard) {
		
		assert (ClaimedPrize.BINGO.get().equals(prizeType)) || (ClaimedPrize.LINE.get().equals(prizeType)) ||
		       (ClaimedPrize.COLUMN.get().equals(prizeType)) || (ClaimedPrize.DIAGONAL.get().equals(prizeType));
		
		this.setBingoLimits(BingoGame75BallsLimits.instance());

		switch (prizeType) {
		    case "LINE": return checkLinePrize(bingoCard);
		               
		    case "BINGO": return checkBingo(bingoCard);
		                
		    case "COLUMN": return checkColumnPrize(bingoCard);
		                 
		    case "DIAGONAL": return checkDiagonalPrize(bingoCard);
		               
		    default: break;
		}

		return false;
	}
	
	
	private boolean checkColumnPrize(BingoCard bingoCard) {
		
		if (minorPrizeAlreadyClaimed == true)
			return false;
				
		for (int i = bingoLimits.getMinColumn(); i<=bingoLimits.getMaxColumn();i++) {
			int markedNumbersInColumn = 0;
		
			for (int j=bingoLimits.getMinRow(); j<=bingoLimits.getMaxRow();j++) {
				int number = bingoCard.getNumberInPosition(j,i);
			    if (number != 0) {
				   if (balls.get(number-1).getIsMarked() == true) 
                       markedNumbersInColumn++;
			    }
			    else
			    	markedNumbersInColumn++;
			}
			
			if (markedNumbersInColumn == bingoLimits.getMaxRow())
				return true;
		}		

		return false;
	}
	
	
	private boolean checkDiagonalPrize(BingoCard bingoCard) {
		
		int markedCellsInDiagonalLeftToRight = 0;
		int markedCellsInDiagonalRightToLeft = 0;

		for (int i = bingoLimits.getMinColumn(); i <= bingoLimits.getMaxColumn(); i++){
			   
			   int number = bingoCard.getNumberInPosition(i, i);
			   
			   if (number != 0 ) {
				   if (this.balls.get(number-1).getIsMarked())
			       	   markedCellsInDiagonalLeftToRight++;
			   }
			   else 
		       	   markedCellsInDiagonalLeftToRight++;
		 }
			   
		 
		 for (int i = bingoLimits.getMinColumn(); i <= bingoLimits.getMaxColumn(); i++){
			   
			   int number = bingoCard.getNumberInPosition(bingoLimits.getMaxRow()-i+1, i);
			   if (number != 0 ) {
				   if (this.balls.get(number-1).getIsMarked())
					   markedCellsInDiagonalRightToLeft++;
			   }
			   else 
				   markedCellsInDiagonalRightToLeft++;
		 }


		 if ((markedCellsInDiagonalLeftToRight==bingoLimits.getMaxRow()) ||
		     (markedCellsInDiagonalRightToLeft==bingoLimits.getMaxRow()))
		    	   return true;

		 return false;
	}
}
