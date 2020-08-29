
package es.tfm.bingo.model;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


import es.tfm.bingo.model.bingo75balls.BingoGame75Balls;
import es.tfm.bingo.websocket.EventBingoCard;
import es.tfm.bingo.websocket.EventSquare;

import java.util.ArrayList;


public class PlayerTest {
	
	private static long GAME_ID = 6;
	private static long PLAYER_ID = 5;


	public class BingoCard75BallsTest extends EventBingoCard{
		
		public BingoCard75BallsTest(BingoCard baseCard) {
			this.id = baseCard.getId();
			this.setSquares(new ArrayList<EventSquare>());
			
			for (Square square:baseCard.getSquares()) {
				EventSquare eventSquare = new EventSquare(square.getCoordX(),square.getCoordY(),square.getNumber());
				this.getSquares().add(eventSquare);
			}
		}
	}
	
	
	@Test 
	public void testGivenACardEqualToOneBelongingToUser_WhenDoesCardBelongToUser_ThenReturnsTrue() {
		// Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame75");
		
		BingoCard card1 = game.generateAleatoryCard();
		BingoCard card2 = game.generateAleatoryCard();

		
		Player player = new Player(game,PLAYER_ID,"John");
		player.getBingoCards().add(card1);
		player.getBingoCards().add(card2);

		// When
		BingoCard75BallsTest cardToTest = new BingoCard75BallsTest(card1);
		
		// Then
		assertThat(player.doesCardBelongToUser(cardToTest)).isEqualTo(true);
	}
	
	
	@Test 
	public void testGivenACardNotEqualToAnyBelongingToUser_WhenDoesCardBelongToUser_ThenReturnsFalse() {
		// Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame75");
		
		BingoCard card1 = game.generateAleatoryCard();
		BingoCard card2 = game.generateAleatoryCard();

		
		Player player = new Player(game,PLAYER_ID,"John");
		player.getBingoCards().add(card1);
		player.getBingoCards().add(card2);

		// When
		BingoCard75BallsTest cardToTest = new BingoCard75BallsTest(card1);
		cardToTest.getSquares().get(0).setNumber(75);
		
		// Then
		assertThat(player.doesCardBelongToUser(cardToTest)).isEqualTo(false);
	}	

}

