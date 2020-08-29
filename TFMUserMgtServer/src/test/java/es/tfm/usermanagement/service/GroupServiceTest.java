package es.tfm.usermanagement.service;

import static org.assertj.core.api.Assertions.*;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;
import es.tfm.usermanagement.utils.GameStates;
import es.tfm.usermanagement.utils.Result;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class GroupServiceTest {
	
	static final long GROUP1_ID = 1;
	static final long GROUP2_ID = 15;

	
	static final long USER1_ID = 2;
	static final long USER2_ID = 3;
	
	static final long PLAYER1_ID = 4;
	static final long PLAYER2_ID = 5;

	static final long USER3_ID = 6;
	static final long PLAYER3_ID = 7;

	static final long NOT_ASSIGNED_YET_ID1 = 12;
	
	static final long GAME1_ID = 14;
	static final long GAME2_ID = 16;
	static final long GAME3_ID = 17;
	static final long GAME4_ID = 18;


	
    @TestConfiguration
    static class GroupServiceTestContextConfiguration {
 
        @Bean
        public GroupService groupService() {
            return new GroupService();
        }
    }
     
	
    @Autowired
	private GroupService groupService;
    
	@MockBean
	private GroupRepository groupRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private PlayerRepository playerRepository;
	
	@MockBean
	private GameRepository gameRepository;
	
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
	public void testGivenExistingUserGroup_WhenCreateUserGroup_ThenExceptionIsThrown() throws Exception {

		//Given    	   	
        GameUser user = userRepository.findById(USER1_ID).get();
                
        //When/Then
		assertThatThrownBy(() -> {
	        groupService.createUserGroup("TestGroup",user.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GROUP_ALREADY_EXISTS.get());
	}
	
	
	@Test
	public void testGivenNonExistingUserGroup_WhenCreateUserGroup_ThenReturnsNewGroup() throws ExceptionErrorInApp {
		//Given    	
	    UserGroup group = new UserGroup("FamilyGroup");
        group.setId(NOT_ASSIGNED_YET_ID1);
     
        GameUser user = userRepository.findById(USER1_ID).get();
        Player player = playerRepository.findById(PLAYER1_ID).get();
        group.setGroupAdmin(player);
        
	    Mockito.when(playerRepository.save(Mockito.any(Player.class)))
	      .thenReturn(player);
 
	    Mockito.when(groupRepository.save(Mockito.any(UserGroup.class)))
	      .thenReturn(group);
	    
	    //When
	    UserGroup createdGroup = groupService.createUserGroup(group.getGroupName(),user.getId());
	    
	    //Then
	    assertThat(createdGroup.getGroupName()).isEqualTo(group.getGroupName());
		assertThat(createdGroup.getGroupAdmin()).isEqualTo(player);
    }
	
	
	@Test
	public void testGivenGroupExist_WhenDeleteGroup_ThenReturnsDeletedGroup() throws ExceptionErrorInApp {
			
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
        		
	    //When
		UserGroup deletedGroup = groupService.deleteGroup(group.getId());
		
		//Then
	    Mockito.verify(groupRepository, Mockito.times(1)).deleteById(deletedGroup.getId());
	}
	
	
	@Test
	public void testGivenANonExistingGroup_WhenDeleteGroup_ThenExceptionIsThrown() throws ExceptionErrorInApp {
				
		//Given/When/Then
		assertThatThrownBy(() -> {
			groupService.deleteGroup(NOT_ASSIGNED_YET_ID1);
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.GROUP_NOT_FOUND.get());
	}
	
	
	@Test
	public void testGivenNewAdminBelongsToGroup_WhenChangeAdminToGroup_ThenReturnsGroupUpdatedWithNewAdmin() throws Exception{

		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		
		GameUser user2 = userRepository.findById(USER2_ID).get();
		Player player = playerRepository.findById(PLAYER2_ID).get();

		Mockito.when(playerRepository.findByPlayerNameAndMyGroup_id(player.getPlayerName(),player.getMygroup().getId()))
	      .thenReturn(player);
		
	    Mockito.when(playerRepository.save(Mockito.any(Player.class)))
	      .thenReturn(player);
		 
	    Mockito.when(groupRepository.save(Mockito.any(UserGroup.class)))
	      .thenReturn(group);
	    
	    //When
		UserGroup updatedGroup = groupService.changeAdminToGroup(group.getId(),user2.getUserName());

		//Then	
	    assertThat(updatedGroup.getGroupName()).isEqualTo(group.getGroupName());
		assertThat(updatedGroup.getGroupAdmin()).isEqualTo(player);	
	}
	
	
	@Test
	public void testGivenNewAdminDoesNotBelongToGroup_WhenChangeAdminToGroup_ThenExceptionIsThrown() throws Exception {
		
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		
		GameUser user2 = userRepository.findById(USER2_ID).get();
		Player player = playerRepository.findById(PLAYER2_ID).get();

		Mockito.when(playerRepository.findByPlayerNameAndMyGroup_id(player.getPlayerName(),player.getMygroup().getId()))
	      .thenReturn(null);
	   
        //When/Then
		assertThatThrownBy(() -> {
			groupService.changeAdminToGroup(group.getId(),user2.getUserName());	
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.ADMIN_DOES_NOT_BELONG_TO_GROUP.get());
	}
	
	
	@Test
	public void testGivenGroupAndUserExist_WhenAddMemberToGroup_ThenReturnsNewPlayer() throws Exception {
		   
		//Given    	
		UserGroup group = groupRepository.findById(GROUP2_ID).get();

		GameUser user = userRepository.findById(USER1_ID).get();
        Player player = new Player(user.getUserName());

	    Mockito.when(playerRepository.save(Mockito.any(Player.class)))
	      .thenReturn(player);

	    //When
	    Player addedPlayer = groupService.addPlayerToGroup(group.getId(),user.getUserName());

		//Then
	    assertThat(addedPlayer.getPlayerName()).isEqualTo(user.getUserName());

	}
	

	@Test
	public void testGivenGroupAndPlayerExistAndPlayerDoesNotBelongToGroup_WhenDeleteMemberFromGroup_ThenExceptionIsThrown() throws Exception {
			
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER3_ID).get();        
        
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	            
        //When/Then 
		assertThatThrownBy(() -> {
		    groupService.deleteMemberFromGroup(group.getId(),player.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_DOES_NOT_BELONG_TO_GROUP.get());
	}
	
	
	@Test
	public void testGivenGroupAndPlayerExistAndPlayerIsGroupAdmin_WhenDeleteMemberFromGroup_ThenExceptionIsThrown() throws Exception {
			
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER1_ID).get();        
        
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	           
        //When/Then 
		assertThatThrownBy(() -> {
		    groupService.deleteMemberFromGroup(group.getId(),player.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GROUP_ADMIN.get());
	}
	
	
	@Test
	public void testGivenGroupAndPlayerExistAndPlayerIsGameAdmin_WhenDeleteMemberFromGroup_ThenExceptionIsThrown() throws Exception {
			
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = playerRepository.findById(PLAYER2_ID).get();  
		Game game = gameRepository.findById(GAME1_ID).get();
		game.setGameAdmin(player);
		
		List<Game> listOfGames = new ArrayList<Game>();
		listOfGames.add(game);
        
	    Mockito.when(groupRepository.findById(group.getId()))
	      .thenReturn(Optional.of(group));
	    
	    Mockito.when(gameRepository.findByGameAdmin_PlayerName(player.getPlayerName()))
	      .thenReturn(listOfGames);
	    	            
        //When/Then
		assertThatThrownBy(() -> {
		    groupService.deleteMemberFromGroup(group.getId(),player.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GAME_ADMIN.get());
	}
	
	
	@Test
	public void testGivenPlayerBelongsToGroupAndIsNotGroupNorGameAdmin_WhenDeleteMemberFromGroup_ThenReturnsDeletedPlayer() throws Exception {
			
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
		Player player = group.getGroupPlayers().get(1);   //User2

	    //When
	    Player deletedPlayer = groupService.deleteMemberFromGroup(group.getId(),player.getId());

		//Then
	    Mockito.verify(playerRepository, Mockito.times(1)).deleteById(deletedPlayer.getId());
	}

	
	
	@Test
	public void testGivenGroupExistWithTwoGamesOnIt_WhenGetGamesForGroup_ThenReturnsAListOfTwoGames() throws Exception {
		
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
       
	    //When
	    List<Game> listOfGames = groupService.getGamesForGroup(group.getId());

		//Then
	    assertThat(listOfGames).contains(gameRepository.findById(GAME1_ID).get());
	    assertThat(listOfGames).contains(gameRepository.findById(GAME2_ID).get());
	}
	
	
	@Test
	public void testGivenGroupExistWithTwoPlayersOnIt_WhenGetMembersForGroup_ThenReturnAListOfTwoPlayers() throws Exception {
		       
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();
       
	    //When
	    List<Player> members = groupService.getGroupPlayers(group.getId());

		//Then
	    assertThat(members).contains(playerRepository.findById(PLAYER1_ID).get());
	    assertThat(members).contains(playerRepository.findById(PLAYER2_ID).get());
	}	
	
	
	@Test
	public void testGivenNumberOfMembersYSmallerThanX_WhenGetRankingOfFirstXInGroup_ThenReturnsRankingOFYMembers() throws Exception{
		
		//Given    	
		UserGroup group = groupRepository.findById(GROUP1_ID).get();;
		
		Player player1 = playerRepository.findById(PLAYER1_ID).get();
		Player player2 = playerRepository.findById(PLAYER2_ID).get();
		Player player3 = playerRepository.findById(PLAYER3_ID).get();
		
		player1.setPoints(500);
		player2.setPoints(1000);		
		player3.setPoints(700);
		
		// Players 1&2 already belong to group
		group.getGroupPlayers().add(player3);
		player3.setGroup(group);
		
		List<Player> dummyList = new ArrayList<Player>();
		dummyList.add(player2);
		dummyList.add(player3);
		dummyList.add(player1);
		
	    Mockito.when(playerRepository.findByMyGroup_idOrderByPointsDesc(player3.getMygroup().getId()))
	      .thenReturn(dummyList);
			    		
	    //When
		List<Player> listOfMembers = groupService.getRankingOfFirstXInGroup(group.getId(),7);

		//Then
	    assertThat(listOfMembers.size()).isEqualTo(group.getGroupPlayers().size());
	    assertThat(listOfMembers.get(0)).isEqualTo(player2);
	    assertThat(listOfMembers.get(1)).isEqualTo(player3);
	    assertThat(listOfMembers.get(2)).isEqualTo(player1);

	}
	
	@Test
	public void testGivenNumberOfMembersYGreaterThanX_WhenGetRankingOfFirstXInGroup_ThenReturnsXMembers() throws Exception{
		
		//Given    	
		UserGroup group =  groupRepository.findById(GROUP1_ID).get();
		
		Player player1 = playerRepository.findById(PLAYER1_ID).get();
		Player player2 = playerRepository.findById(PLAYER2_ID).get();
		Player player3 = playerRepository.findById(PLAYER3_ID).get();
		
		player1.setPoints(500);		
		player2.setPoints(700);
		player3.setPoints(200);
		
		// Players 1&2 already belong to group
		group.getGroupPlayers().add(player3);
		player3.setGroup(group);
		
		List<Player> dummyList = new ArrayList<Player>();
		dummyList.add(player2);
		dummyList.add(player1);
		dummyList.add(player3);
		
	    Mockito.when(playerRepository.findByMyGroup_idOrderByPointsDesc(player3.getMygroup().getId()))
	      .thenReturn(dummyList);

	    //When
		List<Player> listOfMembers = groupService.getRankingOfFirstXInGroup(group.getId(),2);
		
		//Then
	    assertThat(listOfMembers.size()).isEqualTo(2);
	    assertThat(listOfMembers.get(0)).isEqualTo(player2);
	    assertThat(listOfMembers.get(1)).isEqualTo(player1);
	}
	
	
	@Test
	public void testGivenTwoGamesForAGroupAreNotStarted_WhenGetGamesForGroupNotStartedYet_ThenReturnAListWithTwoGroups() throws Exception{
		
		//Given    	
		UserGroup group =  groupRepository.findById(GROUP1_ID).get();
		
		Game game1 = new Game("TestGame1");
		game1.setId(GAME1_ID);
		Game game2 = new Game("TestGame2");
		game2.setId(GAME2_ID);
		Game game3 = new Game("TestGame3");
		game2.setId(GAME3_ID);
		Game game4 = new Game("TestGame4");
		game4.setId(GAME4_ID);

		game1.setStatus(GameStates.IN_GAME);
		game4.setStatus(GameStates.IN_GAME);
		
		List<Game> listOfGames= new ArrayList<Game>();
		
		listOfGames.add(game2);
		listOfGames.add(game3);
			
        group.setGroupGames(listOfGames);
                
	    Mockito.when(gameRepository.findByStatusAndUserGroup_Id(GameStates.INITIAL, group.getId()))
	      .thenReturn(listOfGames);
        
	    //When
        List<Game> notStartedGames = groupService.getGamesForGroupNotStartedYet(group.getId());
		
		//Then
	    assertThat(notStartedGames.size()).isEqualTo(2);
	    assertThat(notStartedGames).contains(game2);
	    assertThat(notStartedGames).contains(game3);

	}
}
