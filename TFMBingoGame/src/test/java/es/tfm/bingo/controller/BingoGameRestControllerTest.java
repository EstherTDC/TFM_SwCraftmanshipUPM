package es.tfm.bingo.controller;

import org.junit.Before;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


import es.tfm.bingo.controller.BingoGameRestController;
import es.tfm.bingo.service.BingoGameService;
import es.tfm.bingo.model.BingoCard;
import es.tfm.bingo.model.Player;
import es.tfm.bingo.model.bingo90balls.*;
import es.tfm.bingo.utils.BingoGameType;


@RunWith(SpringRunner.class)
@Import(BingoGameRestController.class)
@WebMvcTest(BingoGameRestController.class)
public class BingoGameRestControllerTest {
	
	private static long GAME_ID = 75;
	private static long PLAYER_ID = 21;



    private MockMvc mockMvc;
    
	@Autowired
	private WebApplicationContext webApplicationContext;
    
	@MockBean
	private BingoGameService bingoGameServiceMock;
	 
    
	@Before
	public void setUp() {
	    this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	
	@Test
	public void createGameTest() throws Exception {
		String gameName = "GameTest";
		String groupName = "FamilyGroup";
		
		when(bingoGameServiceMock.addNewGame(BingoGameType.BINGO_90_BALLS,GAME_ID,groupName,gameName))
		         .thenReturn(new BingoGame90Balls(GAME_ID,groupName,gameName));


		mockMvc.perform(post("/onlineBingo/createGame/BINGO90/"+GAME_ID+"/"+groupName+"/"+gameName)

	      .contentType(MediaType.APPLICATION_JSON))

	      .andExpect(status().is2xxSuccessful())

	      .andExpect(jsonPath("$.gameName", equalTo(gameName)));
	}
	
	
	@Test
	public void joinGameTest() throws Exception {
		
		String playerName = "John";
		int numberOfCards = 1;
		
		String gameName = "GameTest";
		String groupName = "FamilyGroup";
		
		BingoGame90Balls game = new BingoGame90Balls(GAME_ID,groupName,gameName);
		
		List<BingoCard> listOfCards = new ArrayList<BingoCard>();
		
		for (int i = 0; i < numberOfCards ; i++)
			listOfCards.add(game.generateAleatoryCard());		
		
		Player player = new Player(game,PLAYER_ID,playerName);
		player.setBingoCards(listOfCards);
				
		when(bingoGameServiceMock.joinGame(GAME_ID, PLAYER_ID, playerName, numberOfCards))
				.thenReturn(player);


		mockMvc.perform(post("/onlineBingo/joinGame/"+GAME_ID+"/"+PLAYER_ID+"/"+playerName+"/"+numberOfCards)

	      .contentType(MediaType.APPLICATION_JSON))

	      .andExpect(status().is2xxSuccessful())

	      .andExpect(jsonPath("$.foreignId", equalTo((int)PLAYER_ID)))
	      .andExpect(jsonPath("$.playerName", equalTo(playerName)))
	      .andExpect(jsonPath("$.bingoCards[0].squares[0].number",equalTo(listOfCards.get(0).getSquares().get(0).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[1].number",equalTo(listOfCards.get(0).getSquares().get(1).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[2].number",equalTo(listOfCards.get(0).getSquares().get(2).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[3].number",equalTo(listOfCards.get(0).getSquares().get(3).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[4].number",equalTo(listOfCards.get(0).getSquares().get(4).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[5].number",equalTo(listOfCards.get(0).getSquares().get(5).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[6].number",equalTo(listOfCards.get(0).getSquares().get(6).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[7].number",equalTo(listOfCards.get(0).getSquares().get(7).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[8].number",equalTo(listOfCards.get(0).getSquares().get(8).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[9].number",equalTo(listOfCards.get(0).getSquares().get(9).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[10].number",equalTo(listOfCards.get(0).getSquares().get(10).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[11].number",equalTo(listOfCards.get(0).getSquares().get(11).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[12].number",equalTo(listOfCards.get(0).getSquares().get(12).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[13].number",equalTo(listOfCards.get(0).getSquares().get(13).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[14].number",equalTo(listOfCards.get(0).getSquares().get(14).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[15].number",equalTo(listOfCards.get(0).getSquares().get(15).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[16].number",equalTo(listOfCards.get(0).getSquares().get(16).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[17].number",equalTo(listOfCards.get(0).getSquares().get(17).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[18].number",equalTo(listOfCards.get(0).getSquares().get(18).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[19].number",equalTo(listOfCards.get(0).getSquares().get(19).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[20].number",equalTo(listOfCards.get(0).getSquares().get(20).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[21].number",equalTo(listOfCards.get(0).getSquares().get(21).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[22].number",equalTo(listOfCards.get(0).getSquares().get(22).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[23].number",equalTo(listOfCards.get(0).getSquares().get(23).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[24].number",equalTo(listOfCards.get(0).getSquares().get(24).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[25].number",equalTo(listOfCards.get(0).getSquares().get(25).getNumber())))
	      .andExpect(jsonPath("$.bingoCards[0].squares[26].number",equalTo(listOfCards.get(0).getSquares().get(26).getNumber())));
	}
	
	
	@Test
	public void deleteGameTest() throws Exception {
		String gameName = "GameTest";
		String groupName = "FamilyGroup";
		
		when(bingoGameServiceMock.deleteGame(GAME_ID))
		         .thenReturn(new BingoGame90Balls(GAME_ID,groupName,gameName));


		mockMvc.perform(delete("/onlineBingo/deleteGame/"+GAME_ID)

	      .contentType(MediaType.APPLICATION_JSON))

	      .andExpect(status().is2xxSuccessful())

	      .andExpect(jsonPath("$.id", equalTo((int)GAME_ID)))
	      .andExpect(jsonPath("$.gameName", equalTo(gameName)));

	}
	
	
	@Test
	public void getGameTest() throws Exception {
		String gameName = "GameTest";
		String groupName = "FamilyGroup";
		
		when(bingoGameServiceMock.getGameById(GAME_ID))
		         .thenReturn(new BingoGame90Balls(GAME_ID,groupName,gameName));


		mockMvc.perform(get("/onlineBingo/getGame/"+GAME_ID)

	      .contentType(MediaType.APPLICATION_JSON))

	      .andExpect(status().is2xxSuccessful())

	      .andExpect(jsonPath("$.id", equalTo((int)GAME_ID)))
	      .andExpect(jsonPath("$.gameName", equalTo(gameName)));
	}
	
	
}
