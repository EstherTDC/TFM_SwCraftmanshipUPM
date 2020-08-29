package es.tfm.bingo.websocket;

public class EventMessage {
	
	  private Long gameId;
	  private Long playerId;
	  private String eventType;
	  private EventBingoCard bingoCard;

	  public EventMessage() {
	  }
	    
	  public Long getGameId() {
		return gameId;
	  }
		  
	  public void setGameId(Long gameId) {
		this.gameId=gameId;
	  }
	  
	  public Long getPlayerId() {
		return playerId;
	  }
		  
	  public void setPlayerId(Long playerId) {
		this.playerId=playerId;
	  }
	  
	  public String getEventType() {
		return eventType;
	  }
		  
	  public void setEventType(String eventType) {
		this.eventType=eventType;
	  }
	  
	  public EventBingoCard getBingoCard() {
		return bingoCard;
	  }
		  
	  public void setBingoCard(EventBingoCard bingoCard) {
		this.bingoCard=bingoCard;
	  }
}
