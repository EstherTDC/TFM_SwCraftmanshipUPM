package es.tfm.usermanagement.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import es.tfm.usermanagement.controller.ExternalGameRestController;
import es.tfm.usermanagement.model.Game;
import es.tfm.usermanagement.model.GameUser;
import es.tfm.usermanagement.model.Player;
import es.tfm.usermanagement.model.UserGroup;
import es.tfm.usermanagement.repository.GameRepository;
import es.tfm.usermanagement.repository.GroupRepository;
import es.tfm.usermanagement.repository.PlayerRepository;
import es.tfm.usermanagement.repository.UserRepository;
import es.tfm.usermanagement.service.GameService;
import es.tfm.usermanagement.utils.GameStates;


@Import(ExternalGameRestController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(InternalGameRestController.class)
public class InternalGameRestControllerTest {
	
	static final long GROUP1_ID = 1;
	
	static final long USER1_ID = 2;
	static final long USER2_ID = 3;
	
	static final long PLAYER1_ID = 4;
	static final long PLAYER2_ID = 5;

	static final long GAME1_ID = 8;
	
	static final int MINOR_PRIZE = 15;
	static final int MAJOR_PRIZE = 40;

	
	@Autowired
    private MockMvc mvc;
	

	@MockBean
	private GameService gameService;
	
	@MockBean
	private GameRepository gameRepository;

	@MockBean
	private GroupRepository groupRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private PlayerRepository playerRepository;
	
	@Before
	public void setUp() {
		
		// Create a group that will contain 2 players
	    UserGroup group = new UserGroup("TestGroup");
        group.setId(GROUP1_ID);
        
	    GameUser user = new GameUser("John");
	    user.setId(USER1_ID);
	    
	    GameUser user2 = new GameUser("Peter");
	    user2.setId(USER2_ID);
	    
	    Player player1 = new Player(user.getUserName());
	    player1.setId(PLAYER1_ID);
	    player1.setGroup(group);
	    
        group.setGroupAdmin(player1);
        player1.setIsGroupAdmin(true);
        
	    Player player2 = new Player(user2.getUserName());
	    player2.setId(PLAYER2_ID);
	    player2.setGroup(group);
	    
	    Game game = new Game("MyGame");
	    game.setId(GAME1_ID);
	    game.setGameGroup(group);
	    game.setGameAdmin(player1);
	    game.getPlayers().add(player1);
          
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	    
	    Mockito.when(userRepository.findById(user.getId()))
	      .thenReturn(Optional.of(user));
	    
	    Mockito.when(playerRepository.findById(player1.getId()))
	      .thenReturn(Optional.of(player1));
	    
	    Mockito.when(playerRepository.findById(player2.getId()))
	      .thenReturn(Optional.of(player2)); 
	    
	    Mockito.when(gameRepository.findById(game.getId()))
	      .thenReturn(Optional.of(game));
	}
		
	@Test
	public void tesWhenAwardMinorPrize_ThenADummyListOfAPlayerWithUpdatedPointsIsReturned() throws Exception {
		
		//Given
		Game game = gameRepository.findById(GAME1_ID).get();
        Player player = playerRepository.findById(PLAYER1_ID).get();
	    player.setPoints(MINOR_PRIZE);

	   	Mockito.when(gameService.awardMinorPrize(game.getId(),player.getId())).thenReturn(player);

	   	//When/Then
	    mvc.perform(put("http://localhost:8080/internalgamemgt/awardMinorPrize/"+game.getId()+"/"+player.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.playerName", equalTo(player.getPlayerName())))
		      .andExpect(jsonPath("$.points", equalTo(player.getPoints())));
	}
	
	
	@Test
	public void testWhenAwardMajorPrize_ThenADummyListOfAPlayerWithUpdatedPointsIsReturned() throws Exception {
		
        //Given
	    Game game = gameRepository.findById(GAME1_ID).get();
	    Player player = playerRepository.findById(PLAYER1_ID).get();
	    player.setPoints(MAJOR_PRIZE);
		
	   	Mockito.when(gameService.awardMajorPrize(game.getId(),player.getId())).thenReturn(player);

	   	//When/Then
	    mvc.perform(put("http://localhost:8080/internalgamemgt/awardMajorPrize/"+game.getId()+"/"+player.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.playerName", equalTo(player.getPlayerName())))
		      .andExpect(jsonPath("$.points", equalTo(player.getPoints())));
	}
	
	
	@Test
	public void testWhenSetGameStatusInGame_ThenADummyGameWithStatusInGameIsReturned() throws Exception {
		
        //Given
        Game game = gameRepository.findById(GAME1_ID).get();
        game.setStatus(GameStates.IN_GAME);

	   	Mockito.when(gameService.setStatusInGame(game.getId()))
	    	  .thenReturn(game);

	   	//When/Then
	    mvc.perform(put("http://localhost:8080/internalgamemgt/setStatusInGame/"+game.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(game.getGameName())))
		      .andExpect(jsonPath("$.status", equalTo(game.getStatus().get())));
	}
	
	
	@Test
	public void testWhenSetGameStatusFinished_ThenADummyGameWithStatusInGameIsReturned() throws Exception {
		
        //Given
        Game game = gameRepository.findById(GAME1_ID).get();
        game.setStatus(GameStates.FINISHED);

	   	Mockito.when(gameService.setStatusFinished(game.getId()))
	   	  .thenReturn(game);

	   	//When/Then
	    mvc.perform(put("http://localhost:8080/internalgamemgt/setStatusFinished/"+game.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(game.getGameName())))
		      .andExpect(jsonPath("$.status", equalTo(game.getStatus().get())));
	}
}
