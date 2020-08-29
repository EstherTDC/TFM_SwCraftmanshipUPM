package es.tfm.bingo.model.bingo90balls;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import es.tfm.bingo.model.*;
import es.tfm.bingo.utils.*;


@Entity
public class BingoGame90Balls extends BingoGame{
	
	public BingoGame90Balls(){}
	
	public BingoGame90Balls(Long id,String groupName,String gameName) {
        super(id,groupName,gameName,BingoGame90BallsLimits.instance());
		
        List<Ball> listOfBalls = new ArrayList<Ball>();
		for (int i=bingoLimits.getMinNumber(); i<=bingoLimits.getMaxNumber(); i++) {
			BallBingo90Balls ball = new BallBingo90Balls(gameName,i);
			listOfBalls.add(ball);
		}		
		
		this.setBalls(listOfBalls);
	}
	

	@Override
	public void setBalls(List<Ball> balls) {
		for (Ball ball:this.balls)
			assert ball.getClass() == BallBingo90Balls.class;	
		
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
			assert bingoCard.getClass() == BingoCard90Balls.class;
		
		if (this.bingoCards == null) {
		    this.bingoCards = bingoCards;
		} else {
		    this.bingoCards.retainAll(bingoCards);
		    this.bingoCards.addAll(bingoCards);
		}
	}


	@Override
	public BingoCard generateAleatoryCard() {
		BingoCard90Balls card = new BingoCard90Balls();
			
		return card.generateAleatoryCard(this);
	}
	
	
	@Override
	public boolean isCorrectClaimedPrize(String prizeType,BingoCard bingoCard) {
		
		assert (ClaimedPrize.BINGO.get().equals(prizeType)) || (ClaimedPrize.LINE.get().equals(prizeType));
		
		this.setBingoLimits(BingoGame90BallsLimits.instance());
		
		if (ClaimedPrize.BINGO.get().equals(prizeType))
			return checkBingo(bingoCard);	
		
		return checkLinePrize(bingoCard);
	}
}
