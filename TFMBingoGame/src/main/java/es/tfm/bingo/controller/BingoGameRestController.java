package es.tfm.bingo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfm.bingo.model.*;
import es.tfm.bingo.service.*;
import es.tfm.bingo.utils.*;

@Controller
@RestController
@RequestMapping("/onlineBingo")
public class BingoGameRestController {

	@Autowired
	private BingoGameService bingoGameService;
	



	@PostMapping("/createGame/{gameType}/{gameId}/{groupName}/{bingoGameName}")
	public ResponseEntity<Object> addGame(@PathVariable String gameType,
			                              @PathVariable Long gameId,
			                              @PathVariable String groupName,
 		                                  @PathVariable String bingoGameName) {
		
		assert (BingoGameType.BINGO_75_BALLS.equals(gameType) || 
                BingoGameType.BINGO_90_BALLS.equals(gameType));
		
		BingoGameType requestedGameType;

		System.out.println ("===> Game creation request for game "+bingoGameName);

		
		if ( BingoGameType.BINGO_75_BALLS.equals(gameType))
			requestedGameType = BingoGameType.BINGO_75_BALLS;
		else if ( BingoGameType.BINGO_90_BALLS.equals(gameType))
			requestedGameType = BingoGameType.BINGO_90_BALLS;
		else
			return new ResponseEntity<>(Result.NON_EXISTING_BINGO_TYPE.get(),HttpStatus.CONFLICT);

		
		BingoGame createdGame;
		try {
			createdGame = bingoGameService.addNewGame(requestedGameType,gameId,groupName,bingoGameName);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(createdGame,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/getGame/{id}")
	public ResponseEntity<Object> getGame(@PathVariable Long id) {
		
		BingoGame game;
		try {
		    game = bingoGameService.getGameById(id);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(game,HttpStatus.ACCEPTED);
	}
	
	
	@GetMapping("/getPlayer/{id}")
	public ResponseEntity<Object> getPlayer(@PathVariable Long id) {
		
		Player player;
		try {
		    player = bingoGameService.getPlayerById(id);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(player,HttpStatus.ACCEPTED);
	}
	
	
	@DeleteMapping("/deleteGame/{id}")
	public ResponseEntity<Object> deleteGame(@PathVariable Long id) {

		System.out.println ("===> Game removal request for game with id "+id);

		BingoGame game;
		try {
		    game = bingoGameService.deleteGame(id);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(game,HttpStatus.ACCEPTED);
	}
	
	
	
	@PostMapping("/joinGame/{gameId}/{playerId}/{playerName}/{numberOfCards}")
	public ResponseEntity<Object> joinGame(@PathVariable Long gameId,
			                               @PathVariable Long playerId,
			                               @PathVariable String playerName,
			                               @PathVariable int numberOfCards) {
		
		System.out.println ("===> Request of adding player "+playerName+" with id "+playerId+" to game with id "+gameId);

		
		Player player;
		
		try {
			player = bingoGameService.joinGame(gameId, playerId, playerName, numberOfCards);

		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>(player,HttpStatus.CREATED);
	}
	
	
	//Following methods are just to ensure that deletion of referred objects has been done correctly
	
	@GetMapping("/getAllSquares")
	public List<Square> getAllSquares(){
		return bingoGameService.getAllSquares();
	}
	
	@GetMapping("/getAllBalls")
	public List<Ball> getAllBalls(){
		return bingoGameService.getAllBalls();
	}
	
	@GetMapping("/getAllCards")
	public List<BingoCard> getAllCards(){
		return bingoGameService.getAllCards();
	}
	
	@GetMapping("/getAllPlayers")
	public List<Player> getAllPlayers(){
		return bingoGameService.getAllPlayers();
	}
	
	@GetMapping("/getAllGames")
	public List<BingoGame> getAllGames(){
		return bingoGameService.getAllGames();
	}
}
