package es.tfm.usermanagement.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import es.tfm.usermanagement.controller.GameUserRestController;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.service.*;


@Import(GameUserRestController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(GameUserRestController.class)
public class GameUserRestControllerTest {
	
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
	private UserService userService;
	
	@MockBean
	private GroupService groupService;
	
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
	    
	    Game game2 = new Game("AnotherGame");
	    game2.setId(GAME2_ID);
	    game2.setGameGroup(group);
	    game2.setGameAdmin(player2);
	    game2.getPlayers().add(player2);
          

	    
	    Mockito.when(userRepository.findById(user.getId()))
	      .thenReturn(Optional.of(user));
	    
	    Mockito.when(userRepository.findById(user2.getId()))
	      .thenReturn(Optional.of(user2));
	    
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
	public void testWhenAddUser_ThenADummyUserResponseEntityIsReturned() throws Exception {
		
		//Given
	    Mockito.when(userService.addUser("Esther")).thenReturn(new GameUser("Esther"));

	    //When/Then
		mvc.perform(post("http://localhost:8080/usermgt/addUser/Esther")
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.userName", equalTo("Esther")));
	}
	
	
	@Test
	public void testWhenDeleteUser_ThenADummyUserResponseEntityIsReturned() throws Exception {
		
    	//Given
        GameUser user = userRepository.findById(USER1_ID).get();

	    Mockito.when(userService.deleteUser(user.getId())).thenReturn(user);

	    //When/Then
		mvc.perform(delete("http://localhost:8080/usermgt/deleteUser/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$.userName", equalTo(user.getUserName())));
	}
	
	
	@Test
	public void testWhenGetUserGroups_ThenADummyListContainingTwoGroupsIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();
		    
		List<UserGroup> groups = new ArrayList<UserGroup>();
		groups.add(new UserGroup("group1"));
		groups.add(new UserGroup("group2"));

		
	    Mockito.when(userService.getGroupsForUser(user.getId())).thenReturn(groups);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getUserGroups/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].groupName", equalTo("group1")))
		      .andExpect(jsonPath("$[1].groupName", equalTo("group2")));
	}
	
	
	@Test
	public void testWhenGetGroupsUserIsAdmin_ThenADummyListContainingTwoGroupsIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();

		List<UserGroup> groups = new ArrayList<UserGroup>();
		groups.add(new UserGroup("group1"));
		groups.add(new UserGroup("group2"));
		
	    Mockito.when(userService.getGroupsForWhomUserisAdmin(user.getId())).thenReturn(groups);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getGroupsUserIsAdmin/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].groupName", equalTo("group1")))
		      .andExpect(jsonPath("$[1].groupName", equalTo("group2")));
	}
	
	
	@Test
	public void testWhenGetGamesUserIsAdmin_ThenADummyListContainingTwoGamesIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();
	        
		List<Game> games = new ArrayList<Game>();
		Game game1 = gameRepository.findById(GAME1_ID).get();
		Game game2 = gameRepository.findById(GAME2_ID).get();

		games.add(game1);
		games.add(game2);
		    
	    Mockito.when(userService.getGamesForWhomUserisAdmin(user.getId())).thenReturn(games);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getGamesUserIsAdmin/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].gameName", equalTo(game1.getGameName())))
		      .andExpect(jsonPath("$[1].gameName", equalTo(game2.getGameName())));
	}
	
	
	@Test
	public void testWhenGetGamesAvailableForUser_ThenADummyListContainingTwoGamesIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();
		    
		List<Game> games = new ArrayList<Game>();
		Game game1 = gameRepository.findById(GAME1_ID).get();
		Game game2 = gameRepository.findById(GAME2_ID).get();

		games.add(game1);
		games.add(game2);
		    
	    Mockito.when(userService.getGamesAvailableForUser(user.getId())).thenReturn(games);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getGamesAvailableForUser/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].gameName", equalTo(game1.getGameName())))
		      .andExpect(jsonPath("$[1].gameName", equalTo(game2.getGameName())));
	}
	
	@Test
	public void testWhenGetAllUsers_ThenADummyListContainingTwoUserIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();
        GameUser user2 = userRepository.findById(USER2_ID).get();
		    
		List<GameUser> users = new ArrayList<GameUser>();
		users.add(user);
		users.add(user2);
		    
	    Mockito.when(userService.getAllUsers()).thenReturn(users);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getAllUsers/")
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].userName", equalTo(user.getUserName())))
		      .andExpect(jsonPath("$[1].userName", equalTo(user2.getUserName())));
	}
	
	
	@Test
	public void testWhenGetPlayersCurrentlyAssociated_ThenADummyListContainingTwoPlayersIsReturned() throws Exception {
		
	    //Given
        GameUser user = userRepository.findById(USER1_ID).get();
        Player player1 = playerRepository.findById(PLAYER1_ID).get();
        Player player2 = new Player(user.getUserName());
		    
		List<Player> associatedPlayers = new ArrayList<Player>();
		associatedPlayers.add(player1);
		associatedPlayers.add(player2);
		    
	    Mockito.when(userService.getPlayersCurrentlyAssociated(user.getId())).thenReturn(associatedPlayers);

	    //When/Then
		mvc.perform(get("http://localhost:8080/usermgt/getPlayersCurrentlyAssociated/"+user.getId())
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().is2xxSuccessful())
		      .andExpect(jsonPath("$[0].playerName", equalTo(player1.getPlayerName())))
		      .andExpect(jsonPath("$[1].playerName", equalTo(player2.getPlayerName())));
	}
}
