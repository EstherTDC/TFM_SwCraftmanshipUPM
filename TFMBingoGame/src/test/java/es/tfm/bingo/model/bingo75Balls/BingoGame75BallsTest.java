package es.tfm.bingo.model.bingo75Balls;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import es.tfm.bingo.model.Ball;
import es.tfm.bingo.model.BingoCard;
import es.tfm.bingo.model.Square;
import es.tfm.bingo.model.bingo75balls.*;
import es.tfm.bingo.model.bingo75balls.BingoGame75Balls;
import es.tfm.bingo.utils.ClosedInterval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoGame75BallsTest {
	
	private static int ROWS = 5;
	private static int COLUMNS = 5;
	private static int COLUMNS_INTERVAL_SIZE = 15;
	private static long GAME_ID = 15;
	
	private static int NUMBERS_IN_BOARD = 75;

	@Test 
	public void testGivenAJustCreated75BallsGame_whenItisCheckedThatBallsInBoardAreNotMarked_ThenReturnsTrue(){
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		
		List<Ball> ballsInBoard = game.getBalls();
		
		for (int i = 0 ; i < NUMBERS_IN_BOARD; i++)
	    	assertThat(ballsInBoard.get(i).getIsMarked()).isEqualTo(false);
	}
	
	
	@Test 
	public void testGivenA75BallsGame_WhenGenerateAleatoryCard_ThenReturnsACardf25CoordinatesWithNumbersfrom1To75() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame75Balls");

		// When
		BingoCard newCard = game.generateAleatoryCard();
		
		// Then
		assertThat(newCard.getClass()).isEqualTo(BingoCard75Balls.class);
	
		for (Square square:newCard.getSquares())
		    assertThat(square.getClass()).isEqualTo(SquareCard75Balls.class);

		assertThat(newCard.getSquares().size()).isEqualTo(ROWS*COLUMNS);
		assertThat(numbersInColumnsInsideColumnsIntervals(newCard)).isEqualTo(true);
		assertThat(noRepeatedNumbersOnEachColumn(newCard)).isEqualTo(true);
		assertThat(newCard.getBingoGame().getGameName()).isEqualTo(game.getGameName());
		assertThat(newCard.getBingoGame().getGroupName()).isEqualTo(game.getGroupName());
	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForACorrectLine() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
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
	public void testGivenA75BallsGameWithAllNumbersInCardAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForBingo() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
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
	public void testGivenA75BallsGameWithAllNumbersInCardAlreadyMarkedInBoardExceptOne_WhenIsCorrectClaimedPrize_ThenReturnsFalseForBingo() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
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
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ReturnsTrueForACorrectColumn() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
			
		ClosedInterval closedInterval = new ClosedInterval(1,COLUMNS);
		int randomColumn = closedInterval.generateXNumbersInInterval(1).get(0);
		
		for (int i=1; i<=ROWS; i++)
		{
			int number = newCard.getNumberInPosition(i,randomColumn);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("COLUMN", newCard)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsFalseForAnIncorrectColumn() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
			
		ClosedInterval closedInterval = new ClosedInterval(1,COLUMNS);
		int randomColumn = closedInterval.generateXNumbersInInterval(1).get(0);
		
		for (int i=1; i<=ROWS; i++)
		{
			int number = newCard.getNumberInPosition(i,randomColumn);

			if (number !=  0) {
		    	game.getBalls().get(number-1).setIsMarked(true);
			}
		}
		
		int numberToUnmark = newCard.getNumberInPosition(1,randomColumn);

    	game.getBalls().get(numberToUnmark-1).setIsMarked(false);

		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("COLUMN", newCard)).isEqualTo(false);
	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForACorrectLeftToRightDiagonal() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
				
		for (int i=1; i<=ROWS; i++)
		{
			int number = newCard.getNumberInPosition(i,i);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("DIAGONAL", newCard)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsFalseForAnIncorrectLeftToRightDiagonal() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
				
		for (int i=1; i<ROWS; i++)
		{
			int number = newCard.getNumberInPosition(i,i);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
		int number = newCard.getNumberInPosition(ROWS,ROWS);
    	game.getBalls().get(number-1).setIsMarked(false);

        // When/Then
		assertThat(game.isCorrectClaimedPrize("DIAGONAL", newCard)).isEqualTo(false);

	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenIsCorrectClaimedPrize_ThenReturnsTrueForACorrectRightToLeftDiagonal() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
				
		for (int i=1; i<=COLUMNS; i++)
		{
			int number = newCard.getNumberInPosition(ROWS-i+1,i);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("DIAGONAL", newCard)).isEqualTo(true);

	}
	
	
	@Test 
	public void testGivenA75BallsGameWithCertainNumbersAlreadyMarkedInBoard_WhenisCorrectClaimedPrize_ThenReturnsFalseForAnIncorrectRightToLeftDiagonal() {
        // Given
		BingoGame75Balls game = new BingoGame75Balls(GAME_ID,"TestGroup","TestGame");
		BingoCard newCard = game.generateAleatoryCard();
				
		for (int i=1; i<=COLUMNS; i++)
		{
			int number = newCard.getNumberInPosition(ROWS-i+1,i);
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
		int number = newCard.getNumberInPosition(ROWS,1);
    	game.getBalls().get(number-1).setIsMarked(false);
		
        // When/Then
		assertThat(game.isCorrectClaimedPrize("DIAGONAL", newCard)).isEqualTo(false);

	}
	
	
	
	private boolean numbersInColumnsInsideColumnsIntervals(BingoCard bingoCard) {
		
		List<ClosedInterval> columnsClosedIntervals = new ArrayList<ClosedInterval>();
				
		for (int i=0;i<COLUMNS; i++)
			columnsClosedIntervals.add(new ClosedInterval((i*COLUMNS_INTERVAL_SIZE)+1,(i+1)*COLUMNS_INTERVAL_SIZE));
				
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
