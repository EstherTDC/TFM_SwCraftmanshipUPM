package es.tfm.bingo.websocket;

public class GameAdministratorMessage {

    private Long gameId;

    public GameAdministratorMessage() {
	
    }

    public GameAdministratorMessage(long gameId) {
		this.gameId = gameId;
    }
  
    public Long getGameId() {
 		return gameId; 
     }

     public void setGameId(Long gameId) {
 		this.gameId = gameId;
     }
}
