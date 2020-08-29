package es.tfm.usermanagement.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
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
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.utils.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class GameServiceTest {
	
	static final long GROUP1_ID = 1;
	static final long GROUP2_ID = 14;
	
	static final long USER1_ID = 2;
	static final long USER2_ID = 3;
	
	static final long PLAYER1_ID = 4;
	static final long PLAYER2_ID = 5;

	static final long USER3_ID = 6;
	static final long PLAYER3_ID = 7;
	
	static final long GAME1_ID = 8;
	static final long GAME2_ID = 9;
	static final long GAME3_ID = 15;

	static final long NOT_ASSIGNED_YET_ID1 = 10;

	
	static final int MINOR_PRIZE = 15;
	static final int MAJOR_PRIZE = 40;

    @TestConfiguration
    static class GameServiceTestContextConfiguration {
 
        @Bean
        public GameService gameService() {
            return new GameService();
        }
    }
     
	
    @Autowired
	private GameService gameService;
    
	@MockBean
	private GameRepository gameRepository;

	@MockBean
	private GroupRepository groupRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private PlayerRepository playerRepository;
	
    private ObjectMapper mapper = new ObjectMapper();

	
	@Before
	public void setUp() {
		
		//Create two users
	    GameUser user1 = new GameUser("User1");
	    user1.setId(USER1_ID);
        
	    Mockito.when(userRepository.findById(user1.getId()))
	      .thenReturn(Optional.of(user1));
	    
	    Mockito.when(userRepository.findByUserName(user1.getUserName()))
	      .thenReturn(user1);
	    
	    Mockito.when(userRepository.save(Mockito.any(GameUser.class)))
	      .thenReturn(user1);
	    
	    
	    GameUser user2 = new GameUser("User2");
	    user2.setId(USER2_ID);
	    
	    Mockito.when(userRepository.findById(user2.getId()))
	      .thenReturn(Optional.of(user2));
	    
	    Mockito.when(userRepository.findByUserName(user2.getUserName()))
	      .thenReturn(user2);
		
		// Create two groups 
	    UserGroup group = new UserGroup("TestGroup");
        group.setId(GROUP1_ID);
        
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	    
	    Mockito.when(groupRepository.findByGroupName(group.getGroupName()))
	      .thenReturn(group);
	    
	    Mockito.when(groupRepository.save(Mockito.any(UserGroup.class)))
	      .thenReturn(group);
	 
	    
	    UserGroup group2 = new UserGroup("AnotherTestGroup");
        group2.setId(GROUP2_ID);
        
	    Mockito.when(groupRepository.findById(group2.getId()))
	      .thenReturn(Optional.of(group2));
	    
	    Mockito.when(groupRepository.findByGroupName(group2.getGroupName()))
	      .thenReturn(group2);
	    

	    //Create two players belonging to group1
	    
        Player player1 = new Player(user1.getUserName());
        player1.setId(PLAYER1_ID);
        player1.setGroup(group);
        player1.setIsGroupAdmin(true);
        group.setGroupAdmin(player1);
	       
        Player player2 = new Player(user2.getUserName());	    
        player2.setId(PLAYER2_ID);
        player2.setGroup(group);

	    Mockito.when(playerRepository.findById(player2.getId()))
	       .thenReturn(Optional.of(player2));
	    
	    Mockito.when(playerRepository.findById(player1.getId()))
	       .thenReturn(Optional.of(player1));
	    
        group.getGroupPlayers().add(player1);
        group.getGroupPlayers().add(player2);
        
	    
	    //Create one player belonging to group2
	    
        Player player3 = new Player(user2.getUserName());	    
        player3.setId(PLAYER3_ID);
        player3.setIsGroupAdmin(true);
        player3.setGroup(group2);
	
	    Mockito.when(playerRepository.findById(player3.getId()))
	      .thenReturn(Optional.of(player3));
	    
        group2.getGroupPlayers().add(player3);      
	    
	    //Create two games for group1
	    Game game1 = new Game("TestGame");
        game1.setId(GAME1_ID);
        game1.getPlayers().add(player1);
        game1.getPlayers().add(player2);
        game1.setGameAdmin(player1);
        game1.setGameGroup(group);
        
	    Game game2 = new Game("TestGame2");
        game2.setId(GAME2_ID);        
        game2.setGameAdmin(player1);
        game2.setGameGroup(group);
        
        group.getGroupGames().add(game1);
        group.getGroupGames().add(game2);
  
	    Mockito.when(gameRepository.findById(game1.getId()))
	      .thenReturn(Optional.of(game1));
	    
	    Mockito.when(gameRepository.findByGameNameAndUserGroup_GroupName(game1.getGameName(),group.getGroupName()))
	      .thenReturn(game1);
	    
	    Mockito.when(gameRepository.findById(game2.getId()))
	      .thenReturn(Optional.of(game2));
	    
	    Mockito.when(gameRepository.save(Mockito.any(Game.class)))
	      .thenReturn(game1);
	    
	}
	
	
	@Test
	public void testGivenAnExistingGameForGroup_WhenCreateGame_ThenExceptionIsThrown() throws Exception {

        //Given   	
	    Game game = gameRepository.findById(GAME1_ID).get();
        
	    UserGroup group = groupRepository.findById(GROUP1_ID).get();
	    
        GameUser user = userRepository.findById(USER1_ID).get();
	        
        //When/Then
		assertThatThrownBy(() -> {
			gameService.createGame("BINGO_90",game.getGameName(),group.getId(),user.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_ALREADY_EXISTS.get());
	}
	
	
	@Test
	public void testGivenSameGameNameForDifferentGroup_WhenCreateGame_ThenReturnsNewGame() throws Exception {
		
        //Given   	
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);
	    
	    Game game = gameRepository.findById(GAME1_ID).get();  //Game1 belongs to group1
  
	    UserGroup group2 = groupRepository.findById(GROUP2_ID).get();
        Player player = playerRepository.findById(PLAYER3_ID).get(); 

        //Same name for games in different groups
        Game createdGame = new Game(game.getGameName());
        createdGame.setId(GAME3_ID);
        createdGame.setGameAdmin(player);
       
	    Mockito.when(gameRepository.findByGameNameAndUserGroup_GroupName(game.getGameName(),group2.getGroupName()))
	      .thenReturn(null);
	    
	    Mockito.when(gameRepository.save(Mockito.any(Game.class)))
	      .thenReturn(createdGame);
	    
    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingo/createGame/"
                		+"BINGO_90/"+createdGame.getId()+"/"+group2.getGroupName()
                		+"/"+createdGame.getGameName())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game)));


        //When
	    Game savedGame = gameService.createGame("BINGO_90",createdGame.getGameName(),group2.getId(),player.getId());

	    //Then
	    assertThat(savedGame.getGameName()).isEqualTo(createdGame.getGameName());
		assertThat(savedGame.getGameAdmin().getPlayerName()).isEqualTo(player.getPlayerName());	
	}
	
	
	@Test
	public void testGivenGameExist_WhenDeleteGame_ThenReturnsDeletedGame() throws Exception {
		
        //Given   	
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);
        
	    Game game = gameRepository.findById(GAME1_ID).get();

	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingo/deleteGame/"+game.getId())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
        );         
       		
        //When
		Game deletedGame = gameService.deleteGame(game.getId());
		
		//Then
	    Mockito.verify(gameRepository, Mockito.times(1)).deleteById(deletedGame.getId());
	}
	
	@Test
	public void testGivenANonExistingGameForGroup_WhenDeleteGame_ThenExceptionIsThrown() throws Exception {
		
		//Given/When/Then 
		assertThatThrownBy(() -> {
			gameService.deleteGame(NOT_ASSIGNED_YET_ID1);
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GAME_NOT_FOUND.get());
	}
	
	
	@Test
	public void testGivenNewAdminBelongsToGroup_WhenChangeAdminToGame_ThenReturnsUpdatedGame() throws Exception {

        //Given   	
	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player = playerRepository.findById(PLAYER2_ID).get();
        
	    Mockito.when(playerRepository.findByPlayerNameAndMyGroup_id(player.getPlayerName(),player.getMygroup().getId()))
	      .thenReturn(player);
	    
        //When
		Game updatedGame = gameService.changeAdminToGame(game.getId(),player.getPlayerName());

        //Then
	    assertThat(updatedGame.getGameName()).isEqualTo(game.getGameName());
		assertThat(updatedGame.getGameAdmin()).isEqualTo(player);	
	}

	
	
//		@Test(expected=ExceptionErrorInApp.class)
	@Test
	public void testGivenNewGameAdminDoesNotBelongToGroup_WhenChangeAdminToGame_ThenExceptionIsThrown() throws Exception {
	
        //Given   	
	    Game game = gameRepository.findById(GAME1_ID).get();
        
        Player player = playerRepository.findById(PLAYER3_ID).get();
	       
		//When/Then
		assertThatThrownBy(() -> {
			gameService.changeAdminToGame(game.getId(),player.getPlayerName());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PROPOSED_ADMIN_DOES_NOT_BELONG_TO_GROUP.get());
	}
	
	
	@Test
	public void testGivenNewPlayerBelongsToGameGroupAndNotGameAdmin_WhenAddPlayerToGame_ThenReturnsAddedPlayer() throws Exception {

        //Given   	
		int numberOfCards= 2;
		
	    RestTemplate restTemplate = new RestTemplate();
	    gameService.setRestTemplate(restTemplate);
	    
	    MockRestServiceServer mockServer;
	    
        mockServer = MockRestServiceServer.createServer(restTemplate);
	    
	    Game game = gameRepository.findById(GAME2_ID).get();
        Player player = playerRepository.findById(PLAYER2_ID).get();	
        
	    Mockito.when(gameRepository.save(Mockito.any(Game.class)))
	      .thenReturn(game);
                                	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingo/joinGame/"
                		+game.getId()+"/"+player.getId()
                		+"/"+player.getPlayerName()+"/"+numberOfCards)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
        );       
	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingoView/getGameView/Player/"
                		+game.getId()+"/"+player.getId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(player.getPlayerName()))
        );       
	     
        //When
	    gameService.addPlayerToGame(game.getId(), player.getId(), numberOfCards);
	    
        //No assertion done. Enough with checking that "http://localhost:8081/onlineBingoView/getGameView/Player/"
	    //was called once as specified in the mockServer.expect and that it was done with the role "Player"
	}
	
	
	@Test
	public void testGivenNewPlayerBelongsToGameGroupGameAdmin_WhenAddPlayerToGame_ThenReturnsAddedPlayer() throws Exception {
		
	    //Given   	
	    int numberOfCards= 2;
			
		RestTemplate restTemplate = new RestTemplate();
		gameService.setRestTemplate(restTemplate);
		    
		MockRestServiceServer mockServer;
		    
	    mockServer = MockRestServiceServer.createServer(restTemplate);
		    
		Game game = gameRepository.findById(GAME2_ID).get();
	    Player player = playerRepository.findById(PLAYER2_ID).get();
		game.setGameAdmin(player);
		player.setIsGroupAdmin(true);
  
		Mockito.when(gameRepository.save(Mockito.any(Game.class)))
		      .thenReturn(game);
	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingo/joinGame/"
                		+game.getId()+"/"+player.getId()
                		+"/"+player.getPlayerName()+"/"+numberOfCards)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(game))
        );       
	    
	    mockServer.expect(ExpectedCount.once(), 
                requestTo(new URI("http://localhost:8081/onlineBingoView/getGameView/Admin/"
                		+game.getId()+"/"+player.getId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(player))
        );       
	      
        //When
	    gameService.addPlayerToGame(game.getId(), player.getId(), numberOfCards);
	    
        //No assertion done. Enough with checking that "http://localhost:8081/onlineBingoView/getGameView/Player/"
	    //was called once as specified in the mockServer.expect and that it was done with the role "Admin"
	}
	
	

	@Test
	public void testGivenNewPlayerDoesNotBelongToGameGroup_WhenAddPlayerToGame_ThenExceptionIsThrown() throws Exception {
		
        //Given   	
		int numberOfCards= 2;

	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player3 = playerRepository.findById(PLAYER3_ID).get();
	        		
		//When/Then
		assertThatThrownBy(() -> {
			gameService.addPlayerToGame(game.getId(),player3.getId(),numberOfCards);
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_DOES_NOT_BELONG_TO_GROUP.get());
	}
	
	
	@Test
	public void testGivenGameExistAndNumberOfPlayersEqualToMinimum_WhenSetStatusInGame_ThenUpdatedGameStatuIsSetToInGame() throws Exception {
		 
        //Given   	
	    Game game = gameRepository.findById(GAME1_ID).get();
	       
        //When
	    Game updatedGame = gameService.setStatusInGame(game.getId());
	    
	    //Then
		assertThat(updatedGame.getStatus()).isEqualTo(GameStates.IN_GAME);	

	}
	
	
	@Test
	public void testGivenGameExistsAndNumberOfPlayersLessThanMinimum_WhenSetStatusInGame_ThenExceptionIsThrown() throws Exception {
		
		//Given
	    Game game = gameRepository.findById(GAME2_ID).get();
	    
		//When/Then 
		assertThatThrownBy(() -> {
			gameService.setStatusInGame(game.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.NO_MINIMUM_NUMBER_OF_PLAYERS_IN_GAME.get());
	}
	
	@Test
	public void testGivenGameExists_WhenSetStatusFinished_ThenUpdatedGameStatuIsSetToFinished() throws Exception {
        //Given   	        
	    Game game = gameRepository.findById(GAME1_ID).get();
	       
        //When
	    Game updatedGame = gameService.setStatusFinished(game.getId());
	    
        //Then
		assertThat(updatedGame.getStatus()).isEqualTo(GameStates.FINISHED);	

	}
	
	
	@Test
	public void testGivenGameExistAndPlayerBelongsToGame_WhenAwardMinorPrize_ThenIncreasesPlayerPoints() throws Exception {
        //Given   	           
	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player1 = playerRepository.findById(PLAYER1_ID).get();
	      
        //When
	    gameService.awardMinorPrize(game.getId(), player1.getId());
	    
	    //Then
		assertThat(player1.getPoints()).isEqualTo(MINOR_PRIZE);	


	}
	
	
	@Test
	public void testGivenANonExistingPlayerForGroup_WhenAwardMinorPrize_ThenExceptionIsThrown() throws Exception {
		//Given
	    Game game = gameRepository.findById(GAME1_ID).get();
	    
		//When/Then 
		assertThatThrownBy(() -> {
		    gameService.awardMinorPrize(game.getId(), NOT_ASSIGNED_YET_ID1);
		    
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_NOT_FOUND.get());
	}
	
	
	@Test
	public void testGivenGameExistAndPlayerDoesNotBelongToGame_WhenAwardMinorPrize_ThenExceptionIsThrown() throws Exception {
        //Given   	             
	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player3 = playerRepository.findById(PLAYER3_ID).get();
        player3.setPoints(0);
        
		//When/Then 
		assertThatThrownBy(() -> {
		    gameService.awardMinorPrize(game.getId(), player3.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_DOES_NOT_BELONG_TO_GAME.get());
		
		assertThat(player3.getPoints()).isEqualTo(0);		
	}
	
	
	@Test
	public void testGivenGameExistAndPlayerBelongsToGame_WhenAwardMajorPrize_ThenIncreasesPlayerPoints() throws Exception {
        //Given   	             
	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player1 = playerRepository.findById(PLAYER1_ID).get();
	     
        //When
	    gameService.awardMajorPrize(game.getId(), player1.getId());
	    
        //Then
		assertThat(player1.getPoints()).isEqualTo(MAJOR_PRIZE);	

	}
	
	
	@Test
	public void testGivenGameExistAndPlayerDoesNotBelongToGame_WhenAwardMajorPrize_ThenExceptionIsThrown() throws Exception {
        //Given   	            
	    Game game = gameRepository.findById(GAME1_ID).get();
        Player player3 = playerRepository.findById(PLAYER3_ID).get();
        player3.setPoints(0);
           
		//When/Then
		assertThatThrownBy(() -> {
		    gameService.awardMajorPrize(game.getId(), player3.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_DOES_NOT_BELONG_TO_GAME.get());
		
		assertThat(player3.getPoints()).isEqualTo(0);	
	}
}
