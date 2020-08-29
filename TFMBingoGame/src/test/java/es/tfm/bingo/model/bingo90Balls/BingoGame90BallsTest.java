package es.tfm.bingo.model.bingo90Balls;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import es.tfm.bingo.model.Ball;
import es.tfm.bingo.model.BingoCard;
import es.tfm.bingo.model.Square;
import es.tfm.bingo.model.bingo90balls.*;
import es.tfm.bingo.utils.ClosedInterval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoGame90BallsTest {
	
	private static int ROWS = 3;
	private static int COLUMNS = 9;
	private static int NUMBERS_IN_BOARD = 90;

	
	private static long GAME_ID = 1;

	
	@Test 
	public void testGivenAJustCreated90BallsGame_WhenItisCheckedThatBallsInBoardAreNotMarkedIt_ThenReturnsTrue(){
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");
		
		List<Ball> ballsInBoard = game.getBalls();
		
		for (int i = 0 ; i < NUMBERS_IN_BOARD; i++)
	    	assertThat(ballsInBoard.get(i).getIsMarked()).isEqualTo(false);

	}

	
	@Test 
	public void testGivenA90BallsGame_WhenGenerateAleatoryCard_ThenReturnsACardf27CoordinatesWithNumbersfrom1To90() {
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");

		// When
		BingoCard newCard = game.generateAleatoryCard();
		
		// Then
		assertThat(newCard.getClass()).isEqualTo(BingoCard90Balls.class);

		
		for (Square square:newCard.getSquares())
		    assertThat(square.getClass()).isEqualTo(SquareCard90Balls.class);

		assertThat(newCard.getSquares().size()).isEqualTo(ROWS*COLUMNS);
		assertThat(numbersInColumnsInsideColumnsIntervals(newCard)).isEqualTo(true);
		assertThat(noRepeatedNumbersOnEachColumn(newCard)).isEqualTo(true);
		assertThat(newCard.getBingoGame().getGameName()).isEqualTo(game.getGameName());
		assertThat(newCard.getBingoGame().getGroupName()).isEqualTo(game.getGroupName());
	}
	
	
	@Test 
	public void testGivenA90BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForACorrectLine() {
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
			
		ClosedInterval closedInterval = new ClosedInterval(1,ROWS);
		int randomLine = closedInterval.generateXNumbersInInterval(1).get(0);
		
		for (int i=1; i<=COLUMNS; i++)
		{
			int number = newCard.getNumberInPosition(randomLine, i);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("LINE", newCard)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenA90BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsFalseForAnIncorrectLine() {
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
			
		ClosedInterval closedInterval = new ClosedInterval(1,ROWS);
		int randomLine = closedInterval.generateXNumbersInInterval(1).get(0);
		
		int lastMarkedNumber=1;

		for (int i=1; i<=COLUMNS; i++)
		{
			int number = newCard.getNumberInPosition(randomLine, i);
			if (number !=  0) {
		    	game.getBalls().get(number-1).setIsMarked(true);
		    	lastMarkedNumber = number;
			}
		}
		
    	game.getBalls().get(lastMarkedNumber-1).setIsMarked(false);

		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("LINE", newCard)).isEqualTo(false);

	}
	
	
	@Test 
	public void testGivenA90BallsGameWithAllNumbersInCardAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForBingo() {
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
		
		for (int i = 1 ; i <= ROWS ; i++)
	    	for (int j = 1; j <= COLUMNS; j++)
		    {
			    int number = newCard.getNumberInPosition(i, j);
			    if (number !=  0)
		    	    game.getBalls().get(number-1).setIsMarked(true);
		    }
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("BINGO", newCard)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenA90BallsGameWithAllNumbersInCardAlreadyMarkedInBoardExceptOne_WhenIsCorrectClaimedPrize_ThenReturnsFalseForBingo() {
        // Given
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
		
		int lastMarkedNumber=1;

		for (int i = 1 ; i <= ROWS ; i++)
	    	for (int j = 1; j <= COLUMNS; j++)
		    {
			    int number = newCard.getNumberInPosition(i, j);
				if (number !=  0) {
			    	game.getBalls().get(number-1).setIsMarked(true);
			    	lastMarkedNumber = number;
				}
		    }
		
    	game.getBalls().get(lastMarkedNumber-1).setIsMarked(false);

		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("BINGO", newCard)).isEqualTo(false);

	}
	
	
	
	private boolean numbersInColumnsInsideColumnsIntervals(BingoCard bingoCard) {
		
		List<ClosedInterval> columnsClosedIntervals = new ArrayList<ClosedInterval>();
		
		columnsClosedIntervals.add(new ClosedInterval(1,9));
		
		for (int i=1;i<COLUMNS-1; i++)
			columnsClosedIntervals.add(new ClosedInterval(i*10,(i*10)+COLUMNS));
		
		columnsClosedIntervals.add(new ClosedInterval(80,90));
		
		for (int i = 1; i <= ROWS ; i++) {
			for (int j = 1; j <= COLUMNS; j++) {
				int value = bingoCard.getNumberInPosition(i, j);
				
				if (value!=0)
					if (!columnsClosedIntervals.get(j-1).includes(value))
						return false;				
			}
		}
		
		return true;
	}
	
	private boolean noRepeatedNumbersOnEachColumn(BingoCard bingoCard) {
		
		for (int j=1; j <= COLUMNS ; j++) {
		  	Set<Integer> numbersInColumn = new HashSet<Integer>();

            for (int i=1; i <= ROWS; i++) {
            	int number = bingoCard.getNumberInPosition(i, j);
            	if (number != 0)
			    	if (!numbersInColumn.add(number))
					    return false;
            }  
		}
				
		return true;		
	}
}
