package es.tfm.bingo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.tfm.bingo.model.*;
import es.tfm.bingo.service.*;
import es.tfm.bingo.utils.*;

@Controller
@RequestMapping("/onlineBingoView")
public class BingoGameViewController {

	@Autowired
	private BingoGameService bingoGameService;


	@GetMapping("/getGameView/{role}/{gameId}/{foreignPlayerId}")
    public String getGameView(Model model,@PathVariable String role,@PathVariable Long gameId,@PathVariable Long foreignPlayerId) {
		
		System.out.println ("===> Game View request for player with foreignId "+foreignPlayerId+" and game with id "+gameId);

		if (!((role.equals("Admin")) || (role.equals("Player"))))
			return null;

	    BingoGame game;
		Player player;
		
		try {
			game = bingoGameService.getGameById(gameId);
			player = bingoGameService.getPlayerByGameAndForeignId(game,foreignPlayerId);

		} catch (ExceptionErrorInApp e) {
			return "Error upon getting player";
		}

		model.addAttribute("role", role);
		model.addAttribute("gameId", gameId);
		model.addAttribute("gameName", game.getGameName());
		model.addAttribute("gameType", game.getBalls().size());
		model.addAttribute("playerId", player.getId());
		model.addAttribute("playerName",player.getPlayerName());

		return "bingoGame";
	}
}
