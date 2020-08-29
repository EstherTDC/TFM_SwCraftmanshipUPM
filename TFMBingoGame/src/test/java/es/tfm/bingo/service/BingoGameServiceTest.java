package es.tfm.bingo.service;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;      
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tfm.bingo.model.*;
import es.tfm.bingo.model.bingo75balls.*;
import es.tfm.bingo.model.bingo90balls.*;
import es.tfm.bingo.repository.*;
import es.tfm.bingo.utils.*;

import es.tfm.bingo.websocket.*;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DataJpaTest
public class BingoGameServiceTest {
	
	
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
	

	
	static final long GAME1_ID = 1;
	static final long GAME2_ID = 2;
	
	static final long PLAYER1_ID = 3;
	static final long PLAYER2_ID = 4;
	
	static final long NOT_ASSIGNED_YET_ID1 = 10;


    @TestConfiguration
    static class BingoGameServiceTestContextConfiguration {
 
        @Bean
        public BingoGameService gameService() {
            return new BingoGameService();
        }
    }
     
    @Autowired
	private BingoGameService gameService;
    
	@MockBean
	private BingoGameRepository bingoGameRepository;

	@MockBean
	private BallRepository ballRepository;
	
	@MockBean
	private PlayerRepository playerRepository;
	
	@MockBean
	private BingoCardRepository bingoCardRepository;
	
	@MockBean
	private SquareRepository squareRepository;  
	
	@MockBean
	private BingoEventController bingoEventController;

    private ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void setUp() {
          
	    BingoGame game1 = new BingoGame90Balls(GAME1_ID,"JohnFriends","FridayGame");
        Player player1 = new Player(game1,PLAYER1_ID,"John");
        Player player2 = new Player(game1,PLAYER2_ID,"Michael");
        game1.getPlayers().add(player1);
        game1.getPlayers().add(player2);
  
	    BingoGame game2 = new BingoGame75Balls(GAME2_ID,"MaryFamily","SundayGame");

         
	    Mockito.when(bingoGameRepository.findById(game1.getId()))
	      .thenReturn(Optional.of(game1));
	    
	    Mockito.when(bingoGameRepository.findById(game2.getId()))
	      .thenReturn(Optional.of(game2));
	    
	    Mockito.when(playerRepository.findById(player1.getId()))
	      .thenReturn(Optional.of(player1));
	    
	    Mockito.when(playerRepository.findById(player2.getId()))
	      .thenReturn(Optional.of(player2));

	}
	
	
	@Test
	public void testGivenNonExistingGameId_WhenAddNewGame_ThenReturnsNewGame() throws Exception {
	    //Given
		String newGameName = "myGame";
		String newGameGroup = "PepeGroup";
		BingoGame game = new BingoGame90Balls(NOT_ASSIGNED_YET_ID1,newGameGroup,newGameName);
		
	    Mockito.when(bingoGameRepository.findById(NOT_ASSIGNED_YET_ID1))
	      .thenReturn(Optional.empty());

	    Mockito.when(bingoGameRepository.save(Mockito.any(BingoGame.class)))
	      .thenReturn(game);
		
	    //When
		BingoGame createdGame = gameService.addNewGame(BingoGameType.BINGO_90_BALLS, NOT_ASSIGNED_YET_ID1, newGameGroup,newGameName);
		
		//Then
	    assertThat(createdGame.getGameName()).isEqualTo(newGameName);
		assertThat(createdGame.getGroupName()).isEqualTo(newGameGroup);	
	}
	
	
	@Test
	public void testGivenExistingGameForGivenId_WhenAddNewGame_ThenExceptionIsThrown() throws Exception {
		    	                    
		//When/Then
		assertThatThrownBy(() -> {
			gameService.addNewGame(BingoGameType.BINGO_90_BALLS, GAME1_ID, "PepeGroup","myGame");

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_ALREADY_EXISTS.get());
	}
	
	
	@Test
	public void testGivenExistingGameId_WhenDeleteGame_ThenReturnsDeletedGame() throws Exception {
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
			
		//When
		BingoGame deletedGame = gameService.deleteGame(GAME1_ID);
		
		//Then
	    assertThat(deletedGame.getId()).isEqualTo(game.getId());
	    assertThat(deletedGame.getGameName()).isEqualTo(game.getGameName());
		assertThat(deletedGame.getGroupName()).isEqualTo(game.getGroupName());	
	}
	
	
	@Test
	public void testGivenNonExistingGameId_WhenDeleteGame_ThenExceptionIsThrown() throws Exception {
		//Given
	    Mockito.when(bingoGameRepository.findById(NOT_ASSIGNED_YET_ID1))
	      .thenReturn(Optional.empty());
		
		//When/Then
		assertThatThrownBy(() -> {
			gameService.deleteGame(NOT_ASSIGNED_YET_ID1);

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_NOT_FOUND.get());
	}
	
	
	@Test
	public void testGivenExistingGameIdIsInInitialStateAndTwoUsers_WhenStartGame_ThenReturnsUpdatedGame() throws Exception {
	    		
		//Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
				
	    Mockito.when(bingoGameRepository.save(Mockito.any(BingoGame.class)))
	      .thenReturn(game);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);

	    mockServer.expect(ExpectedCount.once(), 
	                requestTo(new URI("http://localhost:8082/internalgamemgt/setStatusInGame/1")))
	                .andExpect(method(HttpMethod.PUT))
	                .andRespond(withStatus(HttpStatus.OK)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(mapper.writeValueAsString(game))
	              );             	    
	    
	    //When
		BingoGame updatedGame = gameService.startGame(GAME1_ID);
		
        //Then
	    assertThat(updatedGame.getId()).isEqualTo(game.getId());
	    assertThat(updatedGame.getGameName()).isEqualTo(game.getGameName());
		assertThat(updatedGame.getGroupName()).isEqualTo(game.getGroupName());
		
		//When
		game = bingoGameRepository.findById(GAME1_ID).get();
		
		//Then
		assertThat(game.getStatus()).isEqualTo(BingoStateValues.IN_GAME_RUNNING);

        mockServer.verify();

	}
	
	
	@Test
	public void testGivenExistingGameIdIsInInitialStateAndLessThanTwoUsers_WhenStartGame_ThenExceptionIsThrown() throws Exception {
		
	    //When/Then
		assertThatThrownBy(() -> {
			gameService.startGame(GAME2_ID);

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.NO_MINIMUM_NUMBER_OF_PLAYERS_IN_GAME.get());
	}
	
	
	@Test
	public void testGivenExistingGameIdIsInInGameRunningState_WhenStartGame_ThenExceptionIsThrown() throws Exception {
		
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);
		
		//When/Then 
		assertThatThrownBy(() -> {
			gameService.startGame(GAME1_ID);

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_ALREADY_STARTED.get());
	}
	
	
	@Test
	public void testGivenExistingGameIdIsInInitialStateAndTwoUsers_WhenStopGame_ThenExceptionIsThrown() throws Exception {
		
		//When/Then 
		assertThatThrownBy(() -> {
			gameService.stopGame(GAME2_ID);	

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_NOT_RUNNING.get());

	}
	
	
	@Test
	public void testGivenExistingGameIdIsInInGameRunningState_WhenStopGame_ThenReturnsUpdatedGame() throws Exception {
		
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);
				
	    Mockito.when(bingoGameRepository.save(Mockito.any(BingoGame.class)))
	      .thenReturn(game);
	    
	    //When
		BingoGame updatedGame = gameService.stopGame(GAME1_ID);
		
        //Then
	    assertThat(updatedGame.getId()).isEqualTo(game.getId());
	    assertThat(updatedGame.getGameName()).isEqualTo(game.getGameName());
		assertThat(updatedGame.getGroupName()).isEqualTo(game.getGroupName());
		
		game = bingoGameRepository.findById(GAME1_ID).get();
		
		assertThat(game.getStatus()).isEqualTo(BingoStateValues.IN_GAME_STOPPED);

	}	
	
	
	@Test
	public void testGivenExistingGameIdAndNewPlayer_WhenJoinGame_ThenReturnsAPlayerWithRequestedCards() throws Exception {
		
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME2_ID).get();
		int requestedNumberOfCards = 2;
		
		Player player = new Player(game,NOT_ASSIGNED_YET_ID1,"Pepe");
			        
	    Mockito.when(playerRepository.findByForeignIdAndGame_Id(NOT_ASSIGNED_YET_ID1,GAME2_ID))
	      .thenReturn(null);
	    
	    Mockito.when(playerRepository.save(Mockito.any(Player.class)))
	      .thenReturn(player);
	    
	    //When
		Player updatedPlayer = gameService.joinGame(GAME2_ID,NOT_ASSIGNED_YET_ID1,"Pepe",requestedNumberOfCards);
		
        //Then
	    assertThat(updatedPlayer.getForeignId()).isEqualTo(NOT_ASSIGNED_YET_ID1);
	    assertThat(updatedPlayer.getPlayerName()).isEqualTo("Pepe");	    
	    assertThat(updatedPlayer.getBingoCards().size()).isEqualTo(requestedNumberOfCards);
	}	
	
	
	@Test
	public void testGivenSamePlayerIdentityReceivedForDifferentGames_WhenoinGame_ThenReturnsAPlayerWithRequestedCards() throws Exception {
		
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME2_ID).get();
		int requestedNumberOfCards = 2;
		
		//Player 1 already belongs to game with d GAME1_ID.
		Player player = new Player(game,PLAYER1_ID,"John");
			        
	    Mockito.when(playerRepository.findByForeignIdAndGame_Id(PLAYER1_ID,GAME2_ID))
	      .thenReturn(null);
	    
	    Mockito.when(playerRepository.save(Mockito.any(Player.class)))
	      .thenReturn(player);
	    
	    //When
		Player updatedPlayer = gameService.joinGame(GAME2_ID,PLAYER1_ID,"John",requestedNumberOfCards);
		
        //Then
	    assertThat(updatedPlayer.getForeignId()).isEqualTo(PLAYER1_ID);
	    assertThat(updatedPlayer.getPlayerName()).isEqualTo("John");	    
	    assertThat(updatedPlayer.getBingoCards().size()).isEqualTo(requestedNumberOfCards);
	}	
	
	
	
	@Test
	public void testGivenExistingGameIdAndExistingPlayer_WhenJoinGame_ThenExceptionIsThrown() throws Exception {
		//Given
		int requestedNumberOfCards = 2;
		
		Optional<BingoGame> game = bingoGameRepository.findById(GAME1_ID);
		
		Player storedPlayer = new Player(game.get(),PLAYER2_ID,"Pepe");
		
	    Mockito.when(playerRepository.findByForeignIdAndGame_Id(PLAYER2_ID,GAME1_ID))
	      .thenReturn(storedPlayer);
	    
	    //When/Then 
		assertThatThrownBy(() -> {
		    gameService.joinGame(GAME1_ID,PLAYER2_ID,"Pepe",requestedNumberOfCards);

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_ALREADY_EXISTS_FOR_THIS_GAME.get());
	}	
	
	
	@Test
	public void testGivenGameAlreadyStarted_WhenJoinGameThrowsException() throws Exception {
	    //Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);
		int requestedNumberOfCards = 2;
   
	    //When/Then
		assertThatThrownBy(() -> {
		    gameService.joinGame(GAME1_ID,NOT_ASSIGNED_YET_ID1,"Pepe",requestedNumberOfCards);

		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_ALREADY_INITITATED_CANNOT_JOIN.get());
	}
	
	
	@Test
	public void testGivenACorrectBingoEventReceivedAndCardBelongsToUser_WhenIsCalledPrizeCorrect_ThenReturnsTrue() throws Exception {
		//Given
		
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);

		BingoGame game = bingoGameRepository.findById(GAME1_ID).get(); 
        BingoCard card = game.generateAleatoryCard();
        
       
        Player player = new Player(game,PLAYER1_ID,"Pepe");
        game.getPlayers().add(player);
        game.getBingoCards().add(card);
        player.getBingoCards().add(card);
        
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8082/internalgamemgt/awardMajorPrize/"
	                                     +game.getId()+"/"+player.getForeignId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
              ); 
	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8082/internalgamemgt/setStatusFinished/"
	                                     +game.getId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
              ); 
        
        List<BingoCard> listOfCards = new ArrayList<BingoCard>();
        listOfCards.add(card);
        
        game.setBingoCards(listOfCards);
        player.setBingoCards(listOfCards);
        	
		for (int i=0; i<card.getSquares().size(); i++)
		{
			int number = card.getSquares().get(i).getNumber();
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
		MyBingoCardTest card2 = new MyBingoCardTest(card);
        
	    Mockito.when(bingoCardRepository.findById(card.getId()))
	      .thenReturn(Optional.of(card));
	    
	    Mockito.when(playerRepository.findById(player.getId()))
	      .thenReturn(Optional.of(player));
		
        // When/Then
		assertThat(gameService.isCalledPrizeCorrect(game.getId(),player.getId(),"BINGO",card2)).isEqualTo(true);        
	}	

	
	@Test
	public void testGivenACorrectLineEventReceivedAndCardBelongsToUser_WhenIsCalledPrizeCorrect_ThenReturnsTrue() throws Exception {
		//Given
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);

            	    
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get(); 
        BingoCard card = game.generateAleatoryCard();
        
        Player player = new Player(game,PLAYER1_ID,"Pepe");
        game.getPlayers().add(player);
        game.getBingoCards().add(card);
        player.getBingoCards().add(card);
        
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8082/internalgamemgt/awardMinorPrize/"
	                                     +game.getId()+"/"+player.getForeignId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
              ); 
	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8082/internalgamemgt/setStatusFinished/"
	                                     +game.getId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
              ); 
        
        List<BingoCard> listOfCards = new ArrayList<BingoCard>();
        listOfCards.add(card);
        
        game.setBingoCards(listOfCards);
        player.setBingoCards(listOfCards);
        	
		for (int i=0; i<card.getSquares().size(); i++)
		{
			int number = card.getSquares().get(i).getNumber();
			if (number !=  0)
		    	game.getBalls().get(number-1).setIsMarked(true);
		}
		
		MyBingoCardTest card2 = new MyBingoCardTest(card);
        
	    Mockito.when(bingoCardRepository.findById(card.getId()))
	      .thenReturn(Optional.of(card));
	    
	    Mockito.when(playerRepository.findById(player.getId()))
	      .thenReturn(Optional.of(player));
		
        // When/Then
		assertThat(gameService.isCalledPrizeCorrect(game.getId(),player.getId(),"LINE",card2)).isEqualTo(true);        

	}	

	
	@Test
	public void testGivenAnEventIsReceivedAndCardDoesNotBelongToUser_WhenIsCalledPrizeCorrect_ThenReturnsFalse() throws Exception {
		//Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get(); 
        BingoCard card = game.generateAleatoryCard();
                
		MyBingoCardTest card2 = new MyBingoCardTest(card);

	    Mockito.when(bingoCardRepository.findById(card.getId()))
	      .thenReturn(Optional.of(card));
		
        // When/Then
		assertThat(gameService.isCalledPrizeCorrect(game.getId(),PLAYER1_ID,"BINGO",card2)).isEqualTo(false);        

	}	
	
	
	@Test
	public void testGivenGameExistsAndThereAreAvailableNumbers_WhenGenerateNumber_ThenReturnsANumberInRange() throws Exception {
		//Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);

		Ball ball = new BallBingo90Balls(game.getGameName(),5);
		
	    Mockito.when(ballRepository.findById((long)1))
	      .thenReturn(Optional.of(ball));
	    
		List<Ball> balls = game.getBalls();
		
		for (Ball ballToSearch:balls)
		{
		    Mockito.when(ballRepository.findById(ballToSearch.getId()))
		      .thenReturn(Optional.of(ball));
		}
		
        // When
		int number = gameService.generateNumber(game.getId());
		
		//Then
		assertThat(new ClosedInterval(1,90).includes(number)).isEqualTo(true); 

			    
		Ball updatedBall = ballRepository.findById(ball.getId()).get();
		
		assertThat(updatedBall.getIsMarked()).isEqualTo(true);

	}	
	
	
	@Test
	public void testGivenGameExistsAndThereAreNoAvailableNumbers_WhenGenerateNumber_ThenReturnsMinus1() throws Exception {
		//Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);
		Ball ball = new BallBingo90Balls(game.getGameName(),5);
		
	    Mockito.when(ballRepository.findById((long)1))
	      .thenReturn(Optional.of(ball));
	    
		List<Ball> balls = game.getBalls();
		
		for (Ball ballToSearch:balls)
		{
		    Mockito.when(ballRepository.findById(ballToSearch.getId()))
		      .thenReturn(Optional.of(ball));
		    
		    ballToSearch.setIsMarked(true);
		}		
						
		//When/Then
		assertThat(gameService.generateNumber(game.getId())).isEqualTo(-1); 

	}	
	
	
	@Test
	public void testGivenAvailableNumbersAndLineAlreadyClaimed_WhenGenerateNumber_ThenReturnsANumberAndNewMinorPrizeIsNotLongerAllowed() throws Exception {
		//Given
		BingoGame game = bingoGameRepository.findById(GAME1_ID).get();
		game.setStatus(BingoStateValues.IN_GAME_RUNNING);

		Ball ball = new BallBingo90Balls(game.getGameName(),5);
		game.setMinorPrizeAlreadyClaimed(true);
		
	    Mockito.when(ballRepository.findById((long)1))
	      .thenReturn(Optional.of(ball));
	    
		List<Ball> balls = game.getBalls();
		
		for (Ball ballToSearch:balls)
		{
		    Mockito.when(ballRepository.findById(ballToSearch.getId()))
		      .thenReturn(Optional.of(ball));
		}
		
        // When
		int number = gameService.generateNumber(game.getId());
		
		//Then
		assertThat(new ClosedInterval(1,90).includes(number)).isEqualTo(true);

		
		game = bingoGameRepository.findById(GAME1_ID).get();
		
		assertThat(game.getMinorPrizeNoLongerAvailable()).isEqualTo(true);
			    
		Ball updatedBall = ballRepository.findById(ball.getId()).get();
		
		assertThat(updatedBall.getIsMarked()).isEqualTo(true);
	}	
}

