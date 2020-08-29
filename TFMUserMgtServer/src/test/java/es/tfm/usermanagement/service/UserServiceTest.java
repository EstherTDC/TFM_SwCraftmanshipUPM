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
import es.tfm.usermanagement.service.UserService;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;
import es.tfm.usermanagement.utils.Result;
import es.tfm.usermanagement.repository.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class UserServiceTest {
	
	
	static final long GROUP1_ID = 1;
	static final long GROUP2_ID = 15;

	
	static final long USER1_ID = 2;
	static final long USER2_ID = 3;
	
	static final long PLAYER1_ID = 4;
	static final long PLAYER2_ID = 5;

	static final long USER3_ID = 6;
	static final long PLAYER3_ID = 7;
	
	static final long USER4_ID = 8;
	static final long PLAYER4_ID = 9;

	static final long NOT_ASSIGNED_YET_ID1 = 12;
	
	static final long GAME1_ID = 14;
	static final long GAME2_ID = 16;


	
    @TestConfiguration
    static class UserServiceTestContextConfiguration {
 
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }
     
	
    @Autowired
	private UserService userService;
	
	@MockBean
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
	public void testGivenExistingUser_WhenAddUser_ThenExceptionIsThrown() throws Exception {
		
		//Given
        GameUser user = userRepository.findById(USER1_ID).get();
		     		
		//When/Then 
		assertThatThrownBy(() -> {
			userService.addUser(user.getUserName());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.USER_ALREADY_EXISTS.get());
	}
	
	
	@Test
	public void testGivenNonExistingUser_WhenAddUser_ThenReturnsNewUser() throws Exception {
		    	
		//Given
		GameUser user = new GameUser("User3");
		
	    Mockito.when(userRepository.save(Mockito.any(GameUser.class)))
	      .thenReturn(user);

		//When
        GameUser newUser = userService.addUser(user.getUserName());
        
        //Then
		assertThat(newUser.getUserName()).isEqualTo(user.getUserName());
	}
	
	
	@Test
	public void testGivenUserNotAdministrator_WhenDeleteUser_ThenReturnsDeletedUser() throws Exception {
		
		//Given
		GameUser user = userRepository.findById(USER2_ID).get();
		
		List<Player> playersBeingGroupAdmin = new ArrayList<Player>();
        List<Game> gamesForWhomUserIsAdmin = new ArrayList<Game>();
		
		Mockito.when(playerRepository.findByPlayerNameAndIsGroupAdmin(user.getUserName(),true))
	       .thenReturn(playersBeingGroupAdmin);
		
		Mockito.when(gameRepository.findByGameAdmin_PlayerName(user.getUserName()))
		   .thenReturn(gamesForWhomUserIsAdmin);

		
		//When
		GameUser deletedUser = userService.deleteUser(user.getId());
		
        //Then
	    Mockito.verify(userRepository, Mockito.times(1)).deleteById(deletedUser.getId());
	}
	
	
	@Test
	public void testGivenANonExistingUser_WhenDeleteUser_ThenExceptionIsThrown() throws Exception {
	
		//Given/When/Then 
		assertThatThrownBy(() -> {
			userService.deleteUser(NOT_ASSIGNED_YET_ID1);
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.USER_NOT_FOUND.get());
	}
	
	@Test
	public void testGivenUserIsGroupAdministrator_WhenDeleteUser_ThenExceptionIsThrown() throws Exception {
		
		//Given
		GameUser user = userRepository.findById(USER1_ID).get();
		
		List<Player> playersBeingGroupAdmin = new ArrayList<Player>();
		playersBeingGroupAdmin.add(playerRepository.findById(PLAYER1_ID).get());
		
        List<Game> gamesForWhomUserIsAdmin = new ArrayList<Game>();
		
		Mockito.when(playerRepository.findByPlayerNameAndIsGroupAdmin(user.getUserName(),true))
	       .thenReturn(playersBeingGroupAdmin);
		
		Mockito.when(gameRepository.findByGameAdmin_PlayerName(user.getUserName()))
		   .thenReturn(gamesForWhomUserIsAdmin);
		
		//When/Then
		assertThatThrownBy(() -> {
			userService.deleteUser(user.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.USER_CANNOT_BE_DELETED_IS_ADMIN.get());
	}
	
	
	@Test
	public void testGivenUserIsGameAdministrator_WhenDeleteUser_ThenExceptionIsThrown() throws Exception {
		    
		//Given
		GameUser user = userRepository.findById(USER1_ID).get();
				
		List<Player> playersBeingGroupAdmin = new ArrayList<Player>();
		
        List<Game> gamesForWhomUserIsAdmin = new ArrayList<Game>();
        gamesForWhomUserIsAdmin.add(gameRepository.findById(GAME1_ID).get());
		
		Mockito.when(playerRepository.findByPlayerNameAndIsGroupAdmin(user.getUserName(),true))
	       .thenReturn(playersBeingGroupAdmin);
		
		Mockito.when(gameRepository.findByGameAdmin_PlayerName(user.getUserName()))
		   .thenReturn(gamesForWhomUserIsAdmin);
		
		//When/Then
		assertThatThrownBy(() -> {
			userService.deleteUser(user.getId());
		}).isInstanceOf(ExceptionErrorInApp.class)
		  .hasMessageContaining(Result.USER_CANNOT_BE_DELETED_IS_ADMIN.get());
	}
	
	
	@Test
	public void testGivenAUserHasTwoPlayersInTwoGroups_WhenGetGroupsForUser_ThenReturnsAListWithThoseTwoGroups() throws Exception {
		
		//Given
		GameUser user = userRepository.findById(USER1_ID).get();  

		UserGroup group1 = groupRepository.findById(GROUP1_ID).get();
		UserGroup group2 = groupRepository.findById(GROUP2_ID).get();

		Player player1 = playerRepository.findById(PLAYER1_ID).get();
		Player player2 = new Player (user.getUserName());
		player2.setId(PLAYER4_ID);
		player2.setGroup(group2);
			
		List<Player> playerList = new ArrayList<Player>();
		playerList.add(player1);
		playerList.add(player2);
				
	    
	    Mockito.when(playerRepository.findByPlayerName(user.getUserName()))
	      .thenReturn(playerList);
	    
	    Mockito.when(playerRepository.findById(player2.getId()))
	      .thenReturn(Optional.of(player2));

		//When
		List<UserGroup> groupList = userService.getGroupsForUser(user.getId());
		
        //Then
		assertThat(groupList).contains(group1);
		assertThat(groupList).contains(group2);
	}
	
	
	@Test
	public void tesGivenAUserBelongsToTwoGroupsButOnlyOneHasOneNotStartedGame_WhenGetGamesAvailableForUser_ThenReturnsThatGame() throws Exception {
		
		//Given 	
		//User2 has Player1 and Player2 in groups1 and 2
		GameUser user = userRepository.findById(USER2_ID).get();  

		UserGroup group1 = groupRepository.findById(GROUP1_ID).get();
		UserGroup group2 = groupRepository.findById(GROUP2_ID).get();
		
		Player player1 = playerRepository.findById(PLAYER1_ID).get();
		Player player2 = playerRepository.findById(PLAYER3_ID).get();
		
		Game game1 = gameRepository.findById(GAME1_ID).get();
		Game game2 = gameRepository.findById(GAME2_ID).get();

		List<Player> playerList = new ArrayList<Player>();
		playerList.add(player1);
		playerList.add(player2);
		
	    Mockito.when(playerRepository.findByPlayerName(user.getUserName()))
	      .thenReturn(playerList);
				
		Mockito.when(groupService.getGamesForGroupNotStartedYet(group1.getId()))
	      .thenReturn(group1.getGroupGames());
	    					
		Mockito.when(groupService.getGamesForGroupNotStartedYet(group2.getId()))
		  .thenReturn(new ArrayList<Game>());
	
		//When
		List<Game> availableGames = userService.getGamesAvailableForUser(user.getId());
			
        //Then
		assertThat(availableGames.size()).isEqualTo(2);
		assertThat(availableGames).contains(game1);
		assertThat(availableGames).contains(game2);
	}
}
