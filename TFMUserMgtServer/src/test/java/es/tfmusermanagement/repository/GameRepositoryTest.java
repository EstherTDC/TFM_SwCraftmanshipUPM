package es.tfmusermanagement.repository;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.tfm.usermanagement.Application;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.utils.GameStates;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNull;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GroupRepository groupRepository;

    
    @Test
    public void testGivenTwoOutOfForGamesHaveTheSamePlayer_WhenFindByGameAdmin_PlayerName_ThenReturnsThoseTwoGames() throws Exception {
        //Given
    	Game game1 = new Game("Game1");
    	Game game2 = new Game("Game2");
    	Game game3 = new Game("Game3");
    	Game game4 = new Game("Game4");
    	
    	Player player1 = new Player("John");
    	playerRepository.save(player1);
    	
    	Player player2 = new Player("Peter");
    	playerRepository.save(player2);

    	game1.setGameAdmin(player1);
    	game2.setGameAdmin(player2);
    	game3.setGameAdmin(player2);
    	game4.setGameAdmin(player1);
    	
    	gameRepository.save(game1);
    	gameRepository.save(game2);
    	gameRepository.save(game3);
    	gameRepository.save(game4);

        //When
        List<Game> listOfGames = gameRepository.findByGameAdmin_PlayerName(player1.getPlayerName());
        
        //Then
        assertThat(listOfGames.size()).isEqualTo(2);
        assertThat(listOfGames.get(0)).isEqualTo(game1);
        assertThat(listOfGames.get(1)).isEqualTo(game4);
    }
    
    
    @Test
    public void testGivemTwoOutOfForGamesHaveTheSamePlayerNotSearchedOne_WhenFindByGameAdmin_PlayerName_ThenReturnsAnEmptyList() throws Exception {
        //Given
    	Game game1 = new Game("Game1");
    	Game game2 = new Game("Game2");
    	Game game3 = new Game("Game3");
    	Game game4 = new Game("Game4");
    	
    	Player player1 = new Player("John");
    	playerRepository.save(player1);
    	
    	Player player2 = new Player("Peter");
    	playerRepository.save(player2);

    	game1.setGameAdmin(player1);
    	game2.setGameAdmin(player2);
    	game3.setGameAdmin(player2);
    	game4.setGameAdmin(player1);
    	
    	gameRepository.save(game1);
    	gameRepository.save(game2);
    	gameRepository.save(game3);
    	gameRepository.save(game4);

        //When
        List<Game> listOfGames = gameRepository.findByGameAdmin_PlayerName("Mary");
        
        //Then
        assertThat(listOfGames.size()).isEqualTo(0);
    }
    
	
    @Test
    public void testGivenTwoGamesWithSameNameDefinedForTwoGroups_WhenFindByGameNameAndUserGroup_GroupName_ThenReturnsTheGameForGivenGroupName() throws Exception {
    	
        //Given
    	Game game1 = new Game("Game1");
    	Game game2 = new Game("Game2");
    	Game game3 = new Game("Game1");
    	Game game4 = new Game("Game2");
    	
    	UserGroup group1 = new UserGroup("Group1");
    	groupRepository.save(group1);
    	
    	UserGroup group2 = new UserGroup("Group2");
    	groupRepository.save(group2);

    	game1.setGameGroup(group1);
    	game2.setGameGroup(group1);
    	game3.setGameGroup(group2);
    	game4.setGameGroup(group2);
    	
    	Game savedGame1 = gameRepository.save(game1);
    	Game savedGame2 = gameRepository.save(game2);
    	Game savedGame3 = gameRepository.save(game3);
    	Game savedGame4 = gameRepository.save(game4);
    	
        //When
        Game game = gameRepository.findByGameNameAndUserGroup_GroupName(game1.getGameName(),group2.getGroupName());
        
        //Then
        assertThat(game).isEqualTo(savedGame3);
        assertThat(game).isNotEqualTo(game1);
    }
    
    
    @Test
    public void testGivenTwoGamesWithSameNameDefinedForTwoGroupsNotSearchedGroup_WhenFindByGameNameAndUserGroup_GroupName_ThenReturnsNull() throws Exception {
    	
        //Given
    	Game game1 = new Game("Game1");
    	Game game2 = new Game("Game2");
    	Game game3 = new Game("Game1");
    	Game game4 = new Game("Game2");
    	
    	UserGroup group1 = new UserGroup("Group1");
    	groupRepository.save(group1);
    	
    	UserGroup group2 = new UserGroup("Group2");
    	groupRepository.save(group2);

    	game1.setGameGroup(group1);
    	game2.setGameGroup(group1);
    	game3.setGameGroup(group2);
    	game4.setGameGroup(group2);
    	
        gameRepository.save(game1);
    	gameRepository.save(game2);
    	gameRepository.save(game3);
    	gameRepository.save(game4);
    	
        //When/Then
        assertNull(gameRepository.findByGameNameAndUserGroup_GroupName(game1.getGameName(),"Group3"));
    }

    
    @Test
    public void testGivenTwoOutOfThreeGamesNotStartedInGroup_WhenFindByStatusAndUserGroup_Id_ThenReturnsAListWithNonStartedGames() throws Exception {
    	
        //Given
    	Game game1 = new Game("Game1");
    	Game game2 = new Game("Game2");
    	Game game3 = new Game("Game3");
    	Game game4 = new Game("Game2");
    	
    	UserGroup group1 = new UserGroup("Group1");
    	groupRepository.save(group1);
    	
    	UserGroup group2 = new UserGroup("Group2");
    	groupRepository.save(group2);

    	game1.setGameGroup(group1);
    	game2.setGameGroup(group1);
    	game3.setGameGroup(group1);
    	game4.setGameGroup(group2);
    	
    	game2.setStatus(GameStates.IN_GAME);
    	
    	Game savedGame1 = gameRepository.save(game1);
    	Game savedGame2 = gameRepository.save(game2);
    	Game savedGame3 = gameRepository.save(game3);
    	Game savedGame4 = gameRepository.save(game4);
    	
        //When
    	List<Game> games = gameRepository.findByStatusAndUserGroup_Id(GameStates.INITIAL,group1.getId());
    	

        assertThat(games.size()).isEqualTo(2);
        assertThat(games.get(0)).isEqualTo(savedGame1);
        assertThat(games.get(1)).isEqualTo(savedGame3);
        
        //When
    	games = gameRepository.findByStatusAndUserGroup_Id(GameStates.IN_GAME,group1.getId());

        assertThat(games.size()).isEqualTo(1);
        assertThat(games.get(0)).isEqualTo(savedGame2);
    }
}
