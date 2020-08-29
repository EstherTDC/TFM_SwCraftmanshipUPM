package es.tfmusermanagement.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import es.tfm.usermanagement.Application;
import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;

import static org.assertj.core.api.Assertions.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
public class PlayerRepositoryTest {
  
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GroupRepository groupRepository;

    
    @Test
    public void testGivenTwoOutOfThreePlayersMatchName_WhenFindByPlayerName_ThenReturnsPlayerMatchingName() throws Exception {
        //Given
    	Player player1 = new Player("Player1");
    	Player player2 = new Player("Player2");
    	Player player3 = new Player("Player2");
    	
    	Player savedPlayer1 = playerRepository.save(player1);
    	Player savedPlayer2 = playerRepository.save(player2);
    	Player savedPlayer3 = playerRepository.save(player3);


        //When
    	List<Player> players = playerRepository.findByPlayerName("Player2");
        
        //Then
        assertThat(players.size()).isEqualTo(2);
        assertThat(players).contains(savedPlayer2);
        assertThat(players).contains(savedPlayer3);
    }
    
    
    @Test
    public void testGivenTwoPlayersAreDefinedNoneOfThemMatching_WhenFindByPlayerName_ThenReturnsAnEmptyList() throws Exception {
        //Given
    	Player player1 = new Player("Player1");
    	Player player2 = new Player("Player2");
    	
    	playerRepository.save(player1);
    	playerRepository.save(player2);
        
        //When/Then
        assertThat(playerRepository.findByPlayerName("Player3").size()).isEqualTo(0);
    }
    
    
    @Test
    public void testGivenFourPlayersBelongToAGroup_WhenFindByMyGroup_idOrderByPointsDesc_ThenReturnsAnOrderedListBasedOnPoints()
    		throws Exception {
    	
    	//Given
    	UserGroup group1 = new UserGroup("Group1");
    	UserGroup group2 = new UserGroup("AnotherGroup");
    	
    	UserGroup savedGroup1 = groupRepository.save(group1);
    	UserGroup savedGroup2 = groupRepository.save(group2);

       	Player player1 = new Player("Player1");
       	player1.setGroup(group1);
       	player1.setPoints(100);
    	Player player2 = new Player("Player2");
    	player2.setGroup(group2);
    	player2.setPoints(300);
    	Player player3 = new Player("Player3");
    	player3.setGroup(group1);
    	player3.setPoints(500);
    	Player player4 = new Player("Player4");
    	player4.setGroup(group1);
    	player4.setPoints(400);
    	
       	Player savedPlayer1 = playerRepository.save(player1);
    	Player savedPlayer2 = playerRepository.save(player2);
       	Player savedPlayer3 = playerRepository.save(player3);
    	Player savedPlayer4 = playerRepository.save(player4);
    	
    	//When
    	List<Player> orderedList = playerRepository.findByMyGroup_idOrderByPointsDesc(savedGroup1.getId());

    	//Then
    	assertThat(orderedList.size()).isEqualTo(3);
        assertThat(orderedList.get(0)).isEqualTo(savedPlayer3);
        assertThat(orderedList.get(1)).isEqualTo(savedPlayer4);
        assertThat(orderedList.get(2)).isEqualTo(savedPlayer1);
    }
    
    
    @Test
    public void testGivenThreePlayersHaveSameNameAndTwoAreGroupAdmins_WhenFindByPlayerNameAndIsGroupAdmin_ThenReturnsAListWithTowPlayers()
    		throws Exception {
    	
    	UserGroup group1 = new UserGroup("Group1");
    	UserGroup group2 = new UserGroup("AnotherGroup");
    	UserGroup group3 = new UserGroup("ThirdGroup");

    	
    	UserGroup savedGroup1 = groupRepository.save(group1);
    	UserGroup savedGroup2 = groupRepository.save(group2);
    	UserGroup savedGroup3 = groupRepository.save(group3);

       	Player player1 = new Player("John");
       	player1.setGroup(group1);
       	player1.setIsGroupAdmin(true);
    	Player player2 = new Player("Peter");
    	player2.setGroup(group2);
    	player2.setIsGroupAdmin(true);
    	Player player3 = new Player("John");
    	player3.setGroup(group3);
    	player3.setIsGroupAdmin(true);
    	Player player4 = new Player("John");
    	player4.setGroup(group2);
    	player4.setIsGroupAdmin(false);

       	Player savedPlayer1 = playerRepository.save(player1);
    	Player savedPlayer2 = playerRepository.save(player2);
       	Player savedPlayer3 = playerRepository.save(player3);
    	Player savedPlayer4 = playerRepository.save(player4);
    	
    	//When
    	List<Player> list1 = playerRepository.findByPlayerNameAndIsGroupAdmin("John",true);
    	
    	//Then
    	assertThat(list1.size()==2);
        assertThat(list1.get(0)).isEqualTo(savedPlayer1);
        assertThat(list1.get(1)).isEqualTo(savedPlayer3);
        
        //When
    	List<Player> list2 = playerRepository.findByPlayerNameAndIsGroupAdmin("John",false);
    	
    	//Then
    	assertThat(list2.size()).isEqualTo(1);
        assertThat(list2.get(0)).isEqualTo(savedPlayer4);
    }
    
    
    @Test
    public void testGivenThreePlayersHaveSameNameInTwoGroups_WhenFindByPlayerNameAndMyGroup_id_ThenReturnsPlayerForGivenGroup()
    		throws Exception {
    	
    	UserGroup group1 = new UserGroup("Group1");
    	UserGroup group2 = new UserGroup("AnotherGroup");

    	UserGroup savedGroup1 = groupRepository.save(group1);
    	UserGroup savedGroup2 = groupRepository.save(group2);

       	Player player1 = new Player("John");
       	player1.setGroup(group1);
    	Player player2 = new Player("Peter");
    	player2.setGroup(group2);
    	Player player3 = new Player("John");
    	player3.setGroup(group2);

       	Player savedPlayer1 = playerRepository.save(player1);
    	Player savedPlayer2 = playerRepository.save(player2);
       	Player savedPlayer3 = playerRepository.save(player3);
    	
    	//When
    	Player recoveredPlayer = playerRepository.findByPlayerNameAndMyGroup_id("John",savedGroup1.getId());
    	
    	//Then
        assertThat(recoveredPlayer).isEqualTo(savedPlayer1);

        
    	//When
    	recoveredPlayer = playerRepository.findByPlayerNameAndMyGroup_id("John",savedGroup2.getId());
    	
    	//Then
        assertThat(recoveredPlayer).isEqualTo(savedPlayer3);
    }
}
