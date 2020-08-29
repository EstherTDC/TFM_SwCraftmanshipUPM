package es.tfm.bingo.websocket;

public class EventNotification {
	
	  private String eventType;
	  private String message;

	  public EventNotification() {
	  }
	  
	  public EventNotification(String eventType,String message) {
		  this.eventType = eventType;
		  this.message = message;
	  }
	  
	  public String getEventType() {
		return eventType;
	  }
		  
	  public void setEventType(String eventType) {
		this.eventType=eventType;
	  }
	  
	  public String getMessage() {
		return message;
	  }
		  
	  public void setMessage(String message) {
		this.message=message;
	  }
}
