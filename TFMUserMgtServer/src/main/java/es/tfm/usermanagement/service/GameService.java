package es.tfm.usermanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.utils.*;


@Service
public class GameService extends UserManagementService{
	
	static final int MINOR_PRIZE = 15;
	static final int MAJOR_PRIZE = 40;
	static final int MINIMUM_NUMBER_USERS = 2;

	
	@Value("${gameControllerUrl}")
	private String gameControllerUrl;
	
	private RestTemplate restTemplate;
	
	
	public GameService() {
	    this.restTemplate = new RestTemplate();
	}
	
	
	public void setRestTemplate (RestTemplate restTemplate) {

		this.restTemplate = restTemplate;
	}
	
	
	public Game createGame (String gameType,String gameName, Long groupId, Long playerId) throws ExceptionErrorInApp {
		
		UserGroup savedGroup = getUserGroupById(groupId);
		
		Game savedGame = gameRepository.findByGameNameAndUserGroup_GroupName(gameName,savedGroup.getGroupName());
		
		if (savedGame !=null)
            throw new ExceptionErrorInApp(Result.GAME_ALREADY_EXISTS);
		
		Player savedPlayer = getPlayerById(playerId);

		if (!savedGroup.doesPlayerBelongToGroup(savedPlayer))
            throw new ExceptionErrorInApp(Result.ADMIN_DOES_NOT_BELONG_TO_GROUP);


		Game game = new Game(gameName);
		
		game.setGameAdmin(savedPlayer);
		game.setGameGroup(savedGroup);
		
		savedGame = gameRepository.save(game);
		
		sendCreateGame(gameType,savedGame,savedGroup);
		
		return savedGame;
	}
	
	
	public Game deleteGame (Long gameId) throws ExceptionErrorInApp {
		
		Game deletedGame = getGameById(gameId);
		
		if (deletedGame.getStatus() == GameStates.IN_GAME)
            throw new ExceptionErrorInApp(Result.GAME_ONGOING_CANNOT_BE_DELETED);
		
		deletedGame.getGameGroup();
		deletedGame.getPlayers().size();
		
		gameRepository.deleteById(gameId);
		
		sendDeleteGame(deletedGame);
		
		return deletedGame;
	}
	
	
	public Game changeAdminToGame(Long gameId,String adminName) throws ExceptionErrorInApp {
		
		Game savedGame = getGameById(gameId);
				
		Player player = playerRepository.findByPlayerNameAndMyGroup_id(adminName, savedGame.getGameGroupId());
		
		if (player==null)
            throw new ExceptionErrorInApp(Result.PROPOSED_ADMIN_DOES_NOT_BELONG_TO_GROUP);
		
		savedGame.setGameAdmin(player);
		
		return gameRepository.save(savedGame);				
	}
	
	
	@Transactional
    public String addPlayerToGame(Long gameId,Long playerId,int numberOfCards) throws ExceptionErrorInApp {
    	
		Game savedGame = getGameById(gameId);
		
		if (savedGame.getStatus()!=GameStates.INITIAL)
			throw new ExceptionErrorInApp(Result.GAME_ALREADY_STARTED);
		
		Player savedPlayer = getPlayerById(playerId);
		
		UserGroup group = savedGame.getGameGroup();
		
		UserGroup savedGroup = getUserGroupById(group.getId());
		
		if (!savedGroup.doesPlayerBelongToGroup(savedPlayer))
			throw new ExceptionErrorInApp(Result.PLAYER_DOES_NOT_BELONG_TO_GROUP);
		
		savedGame.getPlayers().add(savedPlayer);		
		
		Game updatedGame = gameRepository.save(savedGame);
		
		sendJoinGame(updatedGame,savedPlayer,numberOfCards);
   	 
		return sendGetGameView(updatedGame,savedPlayer);
    }
    
    
    public Player awardMinorPrize(Long gameId, Long playerId) throws ExceptionErrorInApp {

    	Game savedGame = getGameById(gameId);
 		
		Player savedPlayer = getPlayerById(playerId);
		
 		if (!savedGame.doesPlayerBelongToGame(savedPlayer))
 			throw new ExceptionErrorInApp(Result.PLAYER_DOES_NOT_BELONG_TO_GAME);
 		
 		savedPlayer.setPoints(savedPlayer.getPoints()+MINOR_PRIZE);
 		
 		return playerRepository.save(savedPlayer);   
    }
    
    
    public Player awardMajorPrize(Long gameId, Long playerId) throws ExceptionErrorInApp {
 
    	Game savedGame = getGameById(gameId);
 		
		Player savedPlayer = getPlayerById(playerId);
		
 		if (!savedGame.doesPlayerBelongToGame(savedPlayer))
 			throw new ExceptionErrorInApp(Result.PLAYER_DOES_NOT_BELONG_TO_GAME);
 		
 		savedPlayer.setPoints(savedPlayer.getPoints()+MAJOR_PRIZE);
 		
 		Player updatedPlayer = playerRepository.save(savedPlayer);
 		
 		return updatedPlayer;   
    }
    
    
    public Game setStatusInGame(Long gameId) throws ExceptionErrorInApp {
    	 	
    	Game savedGame = getGameById(gameId);
    	
  		if (savedGame.getPlayers().size()<MINIMUM_NUMBER_USERS)
 			throw new ExceptionErrorInApp(Result.NO_MINIMUM_NUMBER_OF_PLAYERS_IN_GAME);
  		
  		savedGame.setStatus(GameStates.IN_GAME);
  		
  		return gameRepository.save(savedGame);   
    }  
    
    
    public Game setStatusFinished(Long gameId) throws ExceptionErrorInApp {
	 	
    	Game savedGame = getGameById(gameId);
  		
  		savedGame.setStatus(GameStates.FINISHED);
  		
  		return gameRepository.save(savedGame);   
    }  
    
    
    protected void sendCreateGame(String gameType,Game game,UserGroup group) throws ExceptionErrorInApp  {
   	    String url=gameControllerUrl+"/onlineBingo/createGame/"+gameType
   			                     +"/"+game.getId()
   			                     +"/"+group.getGroupName()
   			                     +"/"+game.getGameName();
   	    try { 	 
      	    restTemplate.postForEntity(url, "", ObjectNode.class);
   	    } catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
   	    	String message = e.getResponseBodyAsString();
 			throw new ExceptionErrorInApp(message,Result.ERROR_UPON_GAME_CREATION);
		}
    }
    
    
    protected void sendDeleteGame(Game game) {
      	 String url=gameControllerUrl+"/onlineBingo/deleteGame/"+game.getId();
      	 
      	 restTemplate.delete(url);
    }
    
    
    protected void sendJoinGame(Game game,Player player,int numberOfCards) throws ExceptionErrorInApp{
     	 String url=gameControllerUrl+"/onlineBingo/joinGame/"+game.getId()
     	                             +"/"+player.getId()
     	                             +"/"+player.getPlayerName()
     	                             +"/"+numberOfCards; 

   	    try { 	 
      	    restTemplate.postForEntity(url, "", ObjectNode.class);
   	    } catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
   	    	String message = e.getResponseBodyAsString();
 			throw new ExceptionErrorInApp(message, Result.ERROR_UPON_JOINING_PLAYER_TO_GAME);
		}

    }
    
    
    protected String sendGetGameView(Game game,Player player) {

	    String role = "Player";
	    
     	if (game.getGameAdmin().getId() == player.getId())
   	         role = "Admin";
     	
    	String url = gameControllerUrl+"/onlineBingoView/getGameView/"+role+"/"+game.getId()+"/"+player.getId();

    	String result = restTemplate.getForObject(url,String.class);
    	    	 
    	return result;
    }
    
    
	 public List<Game> getAllGames(){
		return gameRepository.findAll();
	 }
	 
	 public Game getGame(Long gameId) throws ExceptionErrorInApp{
		return this.getGameById(gameId);
	 }
	 
}
