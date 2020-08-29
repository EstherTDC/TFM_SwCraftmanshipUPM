package es.tfm.usermanagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.service.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;


@Controller
@RestController
@RequestMapping("/internalgamemgt")
public class InternalGameRestController {
	
	@Autowired
	private GameService gameService;
	
	
	
	@PutMapping("/awardMinorPrize/{gameId}/{playerId}")
    public ResponseEntity<Object> setMinorPrize(@PathVariable Long gameId,
    		                                    @PathVariable Long playerId) {
		
		System.out.println ("===> Request for awarding a minor prize to player with id "+playerId);

	    Player updatedPlayer;
		try {
			updatedPlayer = gameService.awardMinorPrize(gameId,playerId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
	    	    		
		return new ResponseEntity<>(updatedPlayer, HttpStatus.ACCEPTED); 
	}
	
	
	@PutMapping("/awardMajorPrize/{gameId}/{playerId}")
    public ResponseEntity<Object> setMajorPrize(@PathVariable Long gameId, 
    		                                    @PathVariable Long playerId) {
		
		System.out.println ("===> Request for awarding a major prize to player with id "+playerId);

	    Player updatedPlayer;
		try {
			updatedPlayer = gameService.awardMajorPrize(gameId,playerId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
	 		
		return new ResponseEntity<>(updatedPlayer, HttpStatus.ACCEPTED); 
	}

	
	@PutMapping("/setStatusInGame/{gameId}")
    public ResponseEntity<Object> setStatusInGame(@PathVariable Long gameId) {
		
		System.out.println ("===> Request for setting game status = 'InGame' for game with id "+gameId);

		Game updatedGame;
		try {
			updatedGame = gameService.setStatusInGame(gameId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
				
		return new ResponseEntity<>(updatedGame, HttpStatus.ACCEPTED); 
	}
	
	
	@PutMapping("/setStatusFinished/{gameId}")
    public ResponseEntity<Object> setStatusFinished(@PathVariable Long gameId) {
		
		System.out.println ("===> Request for setting game status = 'Finished' for game with id "+gameId);

		Game updatedGame;
		try {
			updatedGame = gameService.setStatusFinished(gameId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
				
		return new ResponseEntity<>(updatedGame, HttpStatus.ACCEPTED); 
	}
}
