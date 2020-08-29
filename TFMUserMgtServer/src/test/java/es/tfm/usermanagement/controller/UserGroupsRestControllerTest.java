package es.tfm.usermanagement.controller;

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


import es.tfm.usermanagement.controller.UserGroupsRestController;
import es.tfm.usermanagement.model.Game;
import es.tfm.usermanagement.model.GameUser;
import es.tfm.usermanagement.model.Player;
import es.tfm.usermanagement.model.UserGroup;
import es.tfm.usermanagement.repository.GameRepository;
import es.tfm.usermanagement.repository.GroupRepository;
import es.tfm.usermanagement.repository.PlayerRepository;
import es.tfm.usermanagement.repository.UserRepository;
import es.tfm.usermanagement.service.GameService;
import es.tfm.usermanagement.service.GroupService;
import es.tfm.usermanagement.service.UserService;

@Import(UserGroupsRestController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserGroupsRestController.class)
public class UserGroupsRestControllerTest {
	
	static final long GROUP1_ID = 1;
	static final long GROUP2_ID = 10;

	
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
        
	    UserGroup group2 = new UserGroup("TestGroup2");
        group2.setId(GROUP2_ID);
        
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
	    
	    group.getGroupPlayers().add(player1);
	    group.getGroupPlayers().add(player2);

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
          
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	    
	    Mockito.when(groupRepository.findById(group2.getId()))
	      .thenReturn(Optional.of(group2));
	    
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
	public void testWhenGroupCreation_ThenADummyGroupIsReturned() throws Exception {
		
    	//Given
		UserGroup newUserGroup = groupRepository.findById(GROUP1_ID).get();
		GameUser user = userRepository.findById(USER1_ID).get();
		
	    Mockito.when(groupService.createUserGroup(newUserGroup.getGroupName(),user.getId())).thenReturn(newUserGroup);

	    //When/Then
		mvc.perform(post("http://localhost:8080/groupmgt/createUserGroup/"+newUserGroup.getGroupName()+"/"+user.getId())
		   .contentType(MediaType.APPLICATION_JSON))
		   .andExpect(status().is2xxSuccessful())
		   .andExpect(jsonPath("$.groupName", equalTo(newUserGroup.getGroupName())))
		   .andExpect(jsonPath("$.groupAdmin.playerName", equalTo(user.getUserName())));
	}
	
	
	@Test
	public void testWhenChangeAdminToGroup_ThenADummyGroupWithUpdatedAdminIsReturned() throws Exception {
		
    	//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER1_ID).get();
		
	    Mockito.when(groupService.changeAdminToGroup(group.getId(),player.getPlayerName())).thenReturn(group);

	    //When/Then
		mvc.perform(put("http://localhost:8080/groupmgt/changeAdminToGroup/"+group.getId()+"/"+player.getPlayerName())
		   .contentType(MediaType.APPLICATION_JSON))
		   .andExpect(status().is2xxSuccessful())
		   .andExpect(jsonPath("$.groupName", equalTo(group.getGroupName())))
		   .andExpect(jsonPath("$.groupAdmin.playerName", equalTo(player.getPlayerName())));
	}
	
	
	@Test
	public void testWhenAddMemberToGroup_ThenAddedPlayerIsReturned() throws Exception {
		
    	//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER1_ID).get();
		    
	    Mockito.when(groupService.addPlayerToGroup(group.getId(),player.getPlayerName())).thenReturn(player);

	    //When/Then
		mvc.perform(post("http://localhost:8080/groupmgt/addMemberToGroup/"+group.getId()+"/"+player.getPlayerName())
		   .contentType(MediaType.APPLICATION_JSON))
		   .andExpect(status().is2xxSuccessful())
		   .andExpect(jsonPath("$.playerName", equalTo(player.getPlayerName())));
	}
	
	
	@Test
	public void testWhenDeleteMemberFromGroup_ThenDeletedPlayerIsReturned() throws Exception {
		
    	//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER1_ID).get();
		    
	    Mockito.when(groupService.deleteMemberFromGroup(group.getId(),player.getId())).thenReturn(player);

	    //When/Then
		mvc.perform(delete("http://localhost:8080/groupmgt/deleteMemberFromGroup/"+group.getId()+"/"+player.getId())
		   .contentType(MediaType.APPLICATION_JSON))
		   .andExpect(status().is2xxSuccessful())
		   .andExpect(jsonPath("$.playerName", equalTo(player.getPlayerName())));
	}
	
	
	@Test
	public void testWhenDeleteGroup__ThenADummyDeletedGroupIsReturned() throws Exception {
		
    	//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		
	    Mockito.when(groupService.deleteGroup(group.getId())).thenReturn(group);

	    //When/Then
		mvc.perform(delete("http://localhost:8080/groupmgt/deleteGroup/"+group.getId())
		   .contentType(MediaType.APPLICATION_JSON))
		   .andExpect(status().is2xxSuccessful())
		   .andExpect(jsonPath("$.groupName", equalTo(group.getGroupName())))
		   .andExpect(jsonPath("$.groupAdmin.playerName", equalTo(group.getGroupAdmin().getPlayerName())));
	}
	
	
	@Test
	public void testWhenGetRanking_ThenADummyListOfPlayersIsReturned() throws Exception {
		
    	//Given
	    String groupName="JSmithFamily";
	    UserGroup rankedGroup = new UserGroup(groupName);
	    rankedGroup.setId((long)1);
	    
	    List<Player> playerList = new ArrayList<Player>();
	    
	    Player player1 = new Player("John");
	    Player player2 = new Player("Michael");
	    Player player3 = new Player("Peter");
	    Player player4 = new Player("Charlie");
	    player1.setPoints(15);
	    player2.setPoints(25);
	    player3.setPoints(40);
	    player4.setPoints(50);
	    player1.setGroup(rankedGroup);
	    player2.setGroup(rankedGroup);
	    player3.setGroup(rankedGroup);
	    player4.setGroup(rankedGroup);

	    playerList.add(player1);
	    playerList.add(player2);
	    playerList.add(player3);
	    playerList.add(player4);
	    
	    rankedGroup.setGroupPlayers(playerList);
	
	    int playersToReturn = 3;
    	Mockito.when(groupService.getRankingOfFirstXInGroup(rankedGroup.getId(),playersToReturn))
    	       .thenReturn(playerList.subList(0, playersToReturn));

	    //When/Then
	    mvc.perform(get("http://localhost:8080/groupmgt/getRanking/"+rankedGroup.getId()+"/"+playersToReturn)
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().is2xxSuccessful())
	      .andExpect(jsonPath("$[0].playerName", equalTo(player1.getPlayerName())))
	      .andExpect(jsonPath("$[1].playerName", equalTo(player2.getPlayerName())))
	      .andExpect(jsonPath("$[2].playerName", equalTo(player3.getPlayerName())));
	}	
	
	
	@Test
	public void testGivenTwoGroupsAreDefined_WhenGetAllGroups_ThenReturnsAListWithTheseTwoGroups() throws Exception {		

		//Given
		UserGroup group1 = groupRepository.findById(GROUP1_ID).get();
		UserGroup group2 = groupRepository.findById(GROUP2_ID).get();
		
		List<UserGroup> allGroups = new ArrayList<UserGroup>();
		allGroups.add(group1);
		allGroups.add(group2);

    	Mockito.when(groupService.getAllGroups())
               .thenReturn(allGroups);
    	
	    //When/Then
	    mvc.perform(get("http://localhost:8080/groupmgt/getAllGroups")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().is2xxSuccessful())
	      .andExpect(jsonPath("$[0].groupName", equalTo(group1.getGroupName())))
	      .andExpect(jsonPath("$[1].groupName", equalTo(group2.getGroupName())));
	}
	
	
	@Test
	public void testGivenAGroupIsDefined_WhenGetGroup_ThenReturnsThatGroups() throws Exception {		

		//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		
    	Mockito.when(groupService.getGroup(group.getId()))
               .thenReturn(group);
    	
	    //When/Then
	    mvc.perform(get("http://localhost:8080/groupmgt/getGroup/"+group.getId())
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().is2xxSuccessful())
	      .andExpect(jsonPath("$.groupName", equalTo(group.getGroupName())));
	}
	
	
	@Test
	public void testGivenTwoPlayersAreDefined_WhenGetAllPlayers_ThenReturnsAListWithTheseTwoPlayers() throws Exception {		

		//Given
		Player player1 = playerRepository.findById(PLAYER1_ID).get();
		Player player2 = playerRepository.findById(PLAYER2_ID).get();
		
		List<Player> allPlayers = new ArrayList<Player>();
		allPlayers.add(player1);
		allPlayers.add(player2);

    	Mockito.when(groupService.getAllPlayers())
               .thenReturn(allPlayers);
    	
	    //When/Then
	    mvc.perform(get("http://localhost:8080/groupmgt/getAllPlayers")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().is2xxSuccessful())
	      .andExpect(jsonPath("$[0].playerName", equalTo(player1.getPlayerName())))
	      .andExpect(jsonPath("$[1].playerName", equalTo(player2.getPlayerName())));
	}

	@Test
	public void testGivenTwoPlayersAreDefinedForAGroup_WhenGetPlayersForGroup_ThenReturnsAListWithTheseTwoPlayers() throws Exception {		

		//Given
		UserGroup group = groupRepository.findById(GROUP1_ID).get();

    	Mockito.when(groupService.getGroupPlayers(group.getId()))
               .thenReturn(group.getGroupPlayers());
    	
	    //When/Then
	    mvc.perform(get("http://localhost:8080/groupmgt/getPlayersForGroup/"+group.getId())
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().is2xxSuccessful())
	      .andExpect(jsonPath("$[0].playerName", equalTo(group.getGroupPlayers().get(0).getPlayerName())))
	      .andExpect(jsonPath("$[1].playerName", equalTo(group.getGroupPlayers().get(1).getPlayerName())));
	}
}
