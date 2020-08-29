package es.tfm.usermanagement.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.service.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;


@Controller
@RestController
@RequestMapping("/gamemgt")
public class ExternalGameRestController {
	
	@Autowired
	private GameService gameService;
	
	
	@PostMapping("/createGame/{gameType}/{gameName}/{groupId}/{adminId}")
	public ResponseEntity<Object> createGame(@PathVariable String gameType,
			                                        @PathVariable String gameName,
			                                        @PathVariable Long groupId,
			                                        @PathVariable Long adminId) {

		Game newGame;
		
		System.out.println ("===> Game creation request for game "+gameName
				           +" belonging to group with id "+groupId);

		try {
			newGame = gameService.createGame(gameType,gameName,groupId,adminId);
		} catch (ExceptionErrorInApp e) {
			
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<>(newGame, HttpStatus.CREATED); 
	}
	
	
	@PutMapping("/changeAdminToGame/{gameId}/{userName}")
	public ResponseEntity<Object> changeAdminToGame(@PathVariable Long gameId,
			                                       @PathVariable String userName) {

		System.out.println ("===> Request for changing admin for game with id "+gameId);
		
		Game updatedGame;
		try {
			updatedGame = gameService.changeAdminToGame(gameId,userName);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<>(updatedGame, HttpStatus.ACCEPTED); 
	}
	
	
	@DeleteMapping("/deleteGame/{gameId}")
	public ResponseEntity<Object> deleteGame(@PathVariable Long gameId) {

		System.out.println ("===> Request for deleting game with id "+gameId);

		Game deletedGame;
		try {
			deletedGame = gameService.deleteGame(gameId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
				
		return new ResponseEntity<>(deletedGame, HttpStatus.ACCEPTED); 
	}
	
	
	@PutMapping("/joinGame/{gameId}/{playerId}/{numberOfCards}")
	public String joinGame(@PathVariable Long gameId,
			                     @PathVariable Long playerId,
			                     @PathVariable int numberOfCards) {
		
		System.out.println ("===> Request for adding player with id "+playerId
				            +" to game with id "+gameId);

		try {
		     return gameService.addPlayerToGame(gameId, playerId,numberOfCards);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
	}
	
	
	@GetMapping("/getAllGames")
	public List<Game> getAllGames()
	{
		System.out.println ("===> Request for getting all games");

		return gameService.getAllGames();		
	}
	
	
	@GetMapping("/getGame/{gameId}")
	public Game getGame(@PathVariable Long gameId)
	{
		System.out.println ("===> Request for getting game with id "+gameId);

		try {
		   return gameService.getGame(gameId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
	}
}
