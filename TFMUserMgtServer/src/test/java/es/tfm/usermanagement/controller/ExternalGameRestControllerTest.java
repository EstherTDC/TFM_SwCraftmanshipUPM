package es.tfm.usermanagement.controller;

import static org.assertj.core.api.Assertions.*;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.test.web.servlet.MvcResult;

import es.tfm.usermanagement.controller.ExternalGameRestController;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.service.GameService;


@Import(ExternalGameRestController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ExternalGameRestController.class)
public class ExternalGameRestControllerTest {
	
	static final long GROUP1_ID = 1;
	
	static final long USER1_ID = 2;
	static final long USER2_ID = 3;
	
	static final long PLAYER1_ID = 4;
	static final long PLAYER2_ID = 5;

	static final long GAME1_ID = 8;
	static final long GAME2_ID = 9;

	
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
	    
	    Game game2 = new Game("MyTestGame2");
	    game2.setId(GAME2_ID);
	    game2.setGameGroup(group);
	    game2.setGameAdmin(player1);
	    game2.getPlayers().add(player1);
          
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
	    
	    Mockito.when(gameRepository.findById(game2.getId()))
	      .thenReturn(Optional.of(game2));
	}
	
	@Test
	public void testWhenGameCreation_ThenADummyGameResponseEntityIsReturned() throws Exception {
		
		    // Given
		    String gameName="JSmithFamilyGame";
		    Game newGame = new Game(gameName);
		    
		    Player player = playerRepository.findById(PLAYER1_ID).get();
		    newGame.setGameAdmin(player);
		    UserGroup group = groupRepository.findById(GROUP1_ID).get();
		    newGame.setGameGroup(group);
		
	    	Mockito.when(gameService.createGame("BINGO_90",gameName,group.getId(),player.getId())).thenReturn(newGame);

	    	//When/Then
		    mvc.perform(post("http://localhost:8080/gamemgt/createGame/BINGO_90/"+gameName+"/"+group.getId()+"/"+player.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(gameName)))
		      .andExpect(jsonPath("$.gameAdmin.playerName", equalTo("John")));
	}
	
	
	@Test
	public void testWhenChangeAdminToGame_ThenADummyGameResponseEntityWIthUpdatedAdminIsReturned() throws Exception {
		
    	    // Given
		    Game game = gameRepository.findById(GAME1_ID).get();
		    Player player = playerRepository.findById(PLAYER1_ID).get();
	
	    	Mockito.when(gameService.changeAdminToGame(game.getId(),player.getPlayerName())).thenReturn(game);

	    	//When/Then
		    mvc.perform(put("http://localhost:8080/gamemgt/changeAdminToGame/"+game.getId()+"/"+player.getPlayerName())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(game.getGameName())))
		      .andExpect(jsonPath("$.gameAdmin.playerName", equalTo("John")));
	}
	
	
	@Test
	public void testWhenDeleteGame_ThenADummyResponseEntityWithDeleteGameIsReturned() throws Exception {
		
    	    // Given
	        Game deletedGame = gameRepository.findById(GAME1_ID).get();

	    	Mockito.when(gameService.deleteGame(deletedGame.getId())).thenReturn(deletedGame);

	    	//When/Then
		    mvc.perform(delete("http://localhost:8080/gamemgt/deleteGame/"+deletedGame.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(deletedGame.getGameName())))
		      .andExpect(jsonPath("$.gameAdmin.playerName", equalTo(deletedGame.getGameAdmin().getPlayerName())));
	}
		

	
	@Test
	public void testWhenJoinGame_ThenAnStringSimulatingThePlayerViewIsReturned() throws Exception {
		
    	    // Given
		    int numberOfCards = 2;
	        Game game = gameRepository.findById(GAME1_ID).get();
		    Player player = playerRepository.findById(PLAYER1_ID).get();
  
            		    		    
	    	Mockito.when(gameService.addPlayerToGame(game.getId(),player.getId(),numberOfCards))
	    	  .thenReturn("File content");

	    	//When/Then
	    	MvcResult result = mvc.perform(put("http://localhost/gamemgt/joinGame/"+game.getId()+"/"+player.getId()+"/"+numberOfCards)
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andReturn();
		    	    	
	    	assertThat(result.getResponse().getContentAsString()).isEqualTo("File content");

	}
	
	
	@Test
	public void testWhenGetAllGames_ThenADummyListContainingTwoGamesIsReturned() throws Exception {
		
	    //Given
        Game game1 = gameRepository.findById(GAME1_ID).get();
        Game game2 = gameRepository.findById(GAME2_ID).get();
		    
		List<Game> games = new ArrayList<Game>();
		games.add(game1);
		games.add(game2);
		    
	    Mockito.when(gameService.getAllGames()).thenReturn(games);

	    //When/Then
		mvc.perform(get("http://localhost:8080/gamemgt/getAllGames")
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].gameName", equalTo(game1.getGameName())))
		      .andExpect(jsonPath("$[1].gameName", equalTo(game2.getGameName())));
	}
	
	
	@Test
	public void testWhenGetGame_ThenADummyGameIsReturned() throws Exception {
		
	    //Given
        Game game = gameRepository.findById(GAME1_ID).get();
		    
	    Mockito.when(gameService.getGame(game.getId())).thenReturn(game);

	    //When/Then
		mvc.perform(get("http://localhost:8080/gamemgt/getGame/"+game.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.gameName", equalTo(game.getGameName())))
		      .andExpect(jsonPath("$.gameAdmin.playerName", equalTo(game.getGameAdmin().getPlayerName())));
	}
}
