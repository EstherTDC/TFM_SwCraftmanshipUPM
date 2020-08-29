package es.tfm.bingo.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.Assert.assertNull;
import static org.assertj.core.api.Assertions.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import es.tfm.bingo.Application;
import es.tfm.bingo.model.BingoGame;
import es.tfm.bingo.model.Player;
import es.tfm.bingo.model.bingo90balls.BingoGame90Balls;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
public class PlayerRepositoryTest {
  
	static final long GAME1_ID = 1;
	static final long GAME2_ID = 2;
	static final long GAME3_ID = 3;


	static final long FOREIGN1_ID = 4;
	static final long FOREIGN2_ID = 5;

    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private BingoGameRepository gameRepository;
    
    @Test
    public void testGivenTwoPlayersWithSameForeignIdInTwoGames_WhenfindByForeignIdAndGame_Id_ThenReturnsPlayerCorrespondingToGivenGame() throws Exception {
        //Given
    	BingoGame game = new BingoGame90Balls(GAME1_ID,"MyGroup","TestGame");
    	BingoGame game2 = new BingoGame90Balls(GAME2_ID,"MyGroup","TestGame");
    	
    	BingoGame savedGame1 = gameRepository.save(game);
    	BingoGame savedGame2 = gameRepository.save(game2);
    	
    	Player player1 = new Player(savedGame1,FOREIGN1_ID,"Player1");
    	Player player2 = new Player(savedGame1,FOREIGN2_ID,"Player2");
    	Player player3 = new Player(savedGame2,FOREIGN1_ID,"Player1");

    	Player savedPlayer1 = playerRepository.save(player1);
    	Player savedPlayer2 = playerRepository.save(player2);
    	Player savedPlayer3 = playerRepository.save(player3);

        //When
        Player player = playerRepository.findByForeignIdAndGame_Id(FOREIGN1_ID,game2.getId());
        
        //When/Then
        assertThat(playerRepository.findByForeignIdAndGame_Id(FOREIGN1_ID,game2.getId())).isEqualTo(savedPlayer3);
        assertThat(playerRepository.findByForeignIdAndGame_Id(FOREIGN1_ID,game.getId())).isEqualTo(savedPlayer1);
        assertThat(playerRepository.findByForeignIdAndGame_Id(FOREIGN2_ID,game.getId())).isEqualTo(savedPlayer2);

    }
    
    @Test
    public void testGivenTwoPlayersWithSameForeignIdInTwoGamesNotForGivenGame_WhenfindByForeignIdAndGame_Id_ThenReturnsNulls() throws Exception {
        //Given
     	BingoGame game = new BingoGame90Balls(GAME1_ID,"MyGroup","TestGame");
     	BingoGame game2 = new BingoGame90Balls(GAME2_ID,"MyGroup","TestGame");

    	BingoGame savedGame1 = gameRepository.save(game);
    	BingoGame savedGame2 = gameRepository.save(game2);
    	
    	Player player1 = new Player(savedGame1,FOREIGN1_ID,"Player1");
    	Player player2 = new Player(savedGame1,FOREIGN2_ID,"Player2");
    	Player player3 = new Player(savedGame2,FOREIGN1_ID,"Player1");

     	playerRepository.save(player1);
     	playerRepository.save(player2);
     	playerRepository.save(player3);

         
        //When/Then
        assertNull(playerRepository.findByForeignIdAndGame_Id(FOREIGN1_ID,GAME3_ID));
    }
}
