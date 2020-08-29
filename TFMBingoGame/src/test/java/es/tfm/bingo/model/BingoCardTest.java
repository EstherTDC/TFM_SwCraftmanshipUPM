
package es.tfm.bingo.model;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


import es.tfm.bingo.model.bingo75balls.*;
import es.tfm.bingo.model.bingo90balls.*;

import es.tfm.bingo.websocket.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class BingoCardTest {

	private static long GAME_ID = 7;
	
	public class MyBingoCardTest extends EventBingoCard{
				
		public MyBingoCardTest(BingoCard baseCard) {
			this.id = baseCard.getId();
			this.setSquares(new ArrayList<EventSquare>());
			
			for (Square square:baseCard.getSquares()) {
				EventSquare eventSquare = new EventSquare(square.getCoordX(),square.getCoordY(),square.getNumber());
				this.getSquares().add(eventSquare);
			}

		}
	}
	
	
	@Test 
	public void testGivenTwoEqualCardsWithSameIdAndCardValues_WhenEqualsValues_ThenReturnsTrue() {
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame90");
		
		BingoCard card1 = game.generateAleatoryCard();
		
		MyBingoCardTest card2 = new MyBingoCardTest(card1);
		
		assertThat(card1.equalsValues(card2)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenTwoEqualCardsWithSameCardValuesButDifferentId_WhenEqualsValues_ThenReturnsFalse() {
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame75");
		
		BingoCard card1 = game.generateAleatoryCard();
		
		MyBingoCardTest card2 = new MyBingoCardTest(card1);
		
		card2.setId(card1.getId()+1);
		
		assertThat(card1.equalsValues(card2)).isEqualTo(false);
	}
	
	
	@Test 
	public void testGivenTwoEqualCardsWithSameIdButOneDifferentCardValue_WhenEqualsValues_ThenReturnsFalse() {
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame75");
		
		BingoCard card1 = game.generateAleatoryCard();
		
		MyBingoCardTest card2 = new MyBingoCardTest(card1);
		
		List<EventSquare> newSquares = card2.getSquares();
		
		EventSquare square = newSquares.get(0);
		
		if (square.getNumber()==1)
			square.setNumber(2);
		else
			square.setNumber(1);

		assertThat(card1.equalsValues(card2)).isEqualTo(false);
	}
	
}
	
	


