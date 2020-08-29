package es.tfm.bingo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import es.tfm.bingo.model.*;
import es.tfm.bingo.model.bingo75balls.*;
import es.tfm.bingo.model.bingo90balls.*;
import es.tfm.bingo.repository.*;
import es.tfm.bingo.utils.*;
import es.tfm.bingo.websocket.BingoEventController;
import es.tfm.bingo.websocket.EventBingoCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class BingoGameService {
	
	static final int MINIMUM_NUMBER_USERS = 2;
	static final int MINIMUM_NUMBER_OF_CARDS = 1;
	static final int MAXIMUM_NUMBER_OF_CARDS = 3;

	
	@Autowired
	BingoGameRepository bingoGameRepository;
	
	@Autowired
	BallRepository ballRepository;
	
	@Autowired
	PlayerRepository playerRepository;
	
	@Autowired
	BingoCardRepository bingoCardRepository;
	
	@Autowired
	BingoEventController bingoEventController;
	
	@Autowired
	SquareRepository squareRepository;	
	
	@Autowired
	BingoCardRepository cardRepository;	

	@Value("${gameControllerUrl}")
	private String gameControllerUrl;
	
	private RestTemplate restTemplate;
	
	public BingoGameService() {
	    this.restTemplate = new RestTemplate();
	}
	
	public void setRestTemplate (RestTemplate restTemplate) {

		this.restTemplate = restTemplate;

	}
	
	
	public BingoGame addNewGame(BingoGameType gameType,Long id, String groupName,String gameName) throws ExceptionErrorInApp {
		Optional<BingoGame> savedGame = bingoGameRepository.findById(id);
		if (savedGame.isPresent())
            throw new ExceptionErrorInApp(Result.GAME_ALREADY_EXISTS);
		
		BingoGame addedBingo;
		
		if (gameType == BingoGameType.BINGO_75_BALLS)
			addedBingo = new BingoGame75Balls(id,groupName,gameName);
	    else if (gameType == BingoGameType.BINGO_90_BALLS)
	    	addedBingo = new BingoGame90Balls(id,groupName,gameName);
	    else
 			throw new ExceptionErrorInApp(Result.NON_EXISTING_BINGO_TYPE);
			
		return bingoGameRepository.save(addedBingo);
	}
		
	
	public BingoGame deleteGame(Long id) throws ExceptionErrorInApp {
		BingoGame deletedGame = getGameById(id);
		
		deletedGame.getBalls().size();
		deletedGame.getPlayers().size();
		deletedGame.getBingoCards().size();
		
		bingoGameRepository.deleteById(id);
		
		return deletedGame;
	}
	
	
	public Player joinGame(Long gameId,Long foreignPlayerId,String playerName,int numberOfCards) throws ExceptionErrorInApp {

		if ((numberOfCards<MINIMUM_NUMBER_OF_CARDS) || (numberOfCards >MAXIMUM_NUMBER_OF_CARDS))
            throw new ExceptionErrorInApp(Result.INVALID_REQUESTED_NUMBER_OF_CARDS);
		
		Player player = addPlayerToGame(gameId,foreignPlayerId,playerName);
		
        return addCardsToGameAndPlayer(gameId,player,numberOfCards);
	}

	
	private Player addPlayerToGame(Long gameId,Long foreignPlayerId,String playerName) throws ExceptionErrorInApp {
	    	
	    BingoGame savedGame = getGameById(gameId);
        if (savedGame.getStatus() == BingoStateValues.IN_GAME_RUNNING)
             throw new ExceptionErrorInApp(Result.GAME_ALREADY_INITITATED_CANNOT_JOIN);
        
		Player player = playerRepository.findByForeignIdAndGame_Id(foreignPlayerId,gameId);
		
		if (player!=null)
             throw new ExceptionErrorInApp(Result.PLAYER_ALREADY_EXISTS_FOR_THIS_GAME);
							
		return playerRepository.save(new Player(savedGame,foreignPlayerId,playerName));
	}
	
	
	private Player addCardsToGameAndPlayer (Long gameId,Player player,int numberOfCards) throws ExceptionErrorInApp {
	    		
		BingoGame game = getGameById(gameId);
		
	    int generatedCards = 0;
	    List<BingoCard> listOfCards = new ArrayList<BingoCard>();
	    
	    do {
	    	BingoCard card = game.generateAleatoryCard();
	    	
	    	Optional<BingoCard> savedCard = bingoCardRepository.findById(card.getId());
	    	
	    	if (!savedCard.isPresent()) {
	    		listOfCards.add(bingoCardRepository.save(card));

	    		generatedCards++;
	    	}		
	    } while (generatedCards < numberOfCards);
	    
	    player.setBingoCards(listOfCards);
	    	    
	    return playerRepository.save(player);	    
	}
	
	
	@Transactional
	public BingoGame startGame (Long id) throws ExceptionErrorInApp {
		Optional<BingoGame> savedGame = bingoGameRepository.findById(id);
		
		if (!savedGame.isPresent())
 			throw new ExceptionErrorInApp(Result.GAME_NOT_FOUND);
		
		if (!(savedGame.get().getStatus()==BingoStateValues.INITIAL ||
				savedGame.get().getStatus()==BingoStateValues.IN_GAME_STOPPED))
 			throw new ExceptionErrorInApp(Result.GAME_ALREADY_STARTED);

				
  		if (savedGame.get().getPlayers().size()<MINIMUM_NUMBER_USERS)
 			throw new ExceptionErrorInApp(Result.NO_MINIMUM_NUMBER_OF_PLAYERS_IN_GAME);
  		
  		BingoStateValues previousStatus = savedGame.get().getStatus();
  		
  		savedGame.get().setStatus(BingoStateValues.IN_GAME_RUNNING);
  		
  		BingoGame updatedGame = bingoGameRepository.save(savedGame.get());
  		
  		if (previousStatus == BingoStateValues.INITIAL)
  	         sendStatusInGameToUserMgt(updatedGame.getId());
  	    
  		return updatedGame;
	}

	
	public BingoGame stopGame (Long id) throws ExceptionErrorInApp {
		Optional<BingoGame> savedGame = bingoGameRepository.findById(id);
		
		if (!savedGame.isPresent())
 			throw new ExceptionErrorInApp(Result.GAME_NOT_FOUND);
		
		if (!(savedGame.get().getStatus()==BingoStateValues.IN_GAME_RUNNING))
 			throw new ExceptionErrorInApp(Result.GAME_NOT_RUNNING);
		
		savedGame.get().setStatus(BingoStateValues.IN_GAME_STOPPED);

		return bingoGameRepository.save(savedGame.get());
	}
	
	
	@Transactional
    public int generateNumber(Long id) throws ExceptionErrorInApp {
		Optional<BingoGame> savedGame = bingoGameRepository.findById(id);
		
		if (!savedGame.isPresent())
 			throw new ExceptionErrorInApp(Result.GAME_NOT_FOUND);
		
		if (!(savedGame.get().getStatus()==BingoStateValues.IN_GAME_RUNNING))
 			throw new ExceptionErrorInApp(Result.GAME_NOT_RUNNING);
		
		
       	List<Ball> balls = savedGame.get().getBalls();
       	
		List<Ball> availableNumbers = new ArrayList<Ball>();

		for (Ball ball:balls) {
			if (!ball.getIsMarked())
				availableNumbers.add(ball);	
		}
		
		if (availableNumbers.size()==0)
			return -1;
		
		Collections.shuffle(availableNumbers);
		
		Ball ball = ballRepository.findById(availableNumbers.get(0).getId()).get();
		ball.setIsMarked(true);
		ballRepository.save(ball);
		
		if (!savedGame.get().getMinorPrizeNoLongerAvailable()) {
		    if (savedGame.get().getMinorPrizeAlreadyClaimed()) {
		    	savedGame.get().setMinorPrizeNoLongerAvailable(true);
		    	bingoGameRepository.save(savedGame.get());
		    }
		}
		
    	return ball.getNumber();
    }  
	   
	
	@Transactional
    public boolean isCalledPrizeCorrect (long gameId, Long playerId, String prizeType,EventBingoCard eventBingoCard) { 
    	
		Optional<BingoGame> savedGame = bingoGameRepository.findById(gameId);
		
		if (!savedGame.isPresent())
			return false;
					
		Optional<Player> savedPlayer = playerRepository.findById(playerId);
		
		if (!savedPlayer.isPresent())
			return false;
		
		Optional<BingoCard> savedCard = bingoCardRepository.findById(eventBingoCard.getId());
		if (!savedCard.isPresent())
			return false;
		
		Player player = savedPlayer.get();
		
		if (!player.doesCardBelongToUser(eventBingoCard))
	    	return false;

    	if (!savedGame.get().isCorrectClaimedPrize(prizeType, savedCard.get()))
    		return false;
    	
    	if (ClaimedPrize.BINGO.get().equals(prizeType)) {
    		sendMajorAwardCommunicationToUserMgt(savedGame.get().getId(),player.getForeignId());
    		sendStatusFinishedToUserMgt(savedGame.get().getId());
    	}
    	else
    		sendMinorAwardCommunicationToUserMgt(savedGame.get().getId(),player.getForeignId());

       
    	return true;
    }
       
    
     public BingoGame getGameById(Long gameId) throws ExceptionErrorInApp {
  		Optional<BingoGame> savedGame = bingoGameRepository.findById(gameId);
  		
  		if (!savedGame.isPresent())
              throw new ExceptionErrorInApp(Result.GAME_NOT_FOUND);
  		
  		return savedGame.get();
     }
     
     
     public Player getPlayerById(Long playerId) throws ExceptionErrorInApp {
  		Optional<Player> savedPlayer = playerRepository.findById(playerId);
  		
  		if (!savedPlayer.isPresent())
              throw new ExceptionErrorInApp(Result.PLAYER_NOT_FOUND);
  		 
	    return savedPlayer.get();
     }
     
     
     public Player getPlayerByGameAndForeignId(BingoGame game, Long playerId) throws ExceptionErrorInApp {
    	 
   		Player savedPlayer = playerRepository.findByForeignIdAndGame_Id(playerId,game.getId());
   		
   		if (savedPlayer ==null)
               throw new ExceptionErrorInApp(Result.PLAYER_NOT_FOUND);
   		 
 	    return savedPlayer;
      }
     
     
     protected void sendStatusInGameToUserMgt(Long gameId) {
    	 String url=gameControllerUrl+"/internalgamemgt/setStatusInGame/"+gameId;
    	 
    	 restTemplate.put(url, "");
     }
     
     
     protected void sendMinorAwardCommunicationToUserMgt(Long gameId,Long foreignPlayerId) {
    	 String url = gameControllerUrl+"/internalgamemgt/awardMinorPrize/"+gameId+"/"+foreignPlayerId;

    	 restTemplate.put(url, "");
     }
     
     
     protected void sendMajorAwardCommunicationToUserMgt(Long gameId,Long foreignPlayerId) {
    	 String url = gameControllerUrl+"/internalgamemgt/awardMajorPrize/"+gameId+"/"+foreignPlayerId;

    	 restTemplate.put(url, "");
     }
     
     
     protected void sendStatusFinishedToUserMgt(Long gameId) {
    	 String url = gameControllerUrl+"/internalgamemgt/setStatusFinished/"+gameId;
    	     	 
    	 restTemplate.put(url, "");
     }
     
  	 public List<Square> getAllSquares(){
 		return squareRepository.findAll();
 	 }
 	
 	 public List<Ball> getAllBalls(){
 		return ballRepository.findAll();
  	 }
 	
 	 public List<BingoCard> getAllCards(){
 		return cardRepository.findAll();
 	 }
 	
 	 public List<Player> getAllPlayers(){
 		return playerRepository.findAll();
 	 }
 	 
 	 public List<BingoGame> getAllGames(){
 		return bingoGameRepository.findAll();
 	 }
}
