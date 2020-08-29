package es.tfm.bingo.websocket;

public class NewBallMessage {
	
	  private Integer newNumber;

	  public NewBallMessage() {
	  }
	  
	  public NewBallMessage(Integer newNumber) {
		  this.newNumber = newNumber;
	  }

	  public Integer getNewNumber() {
	    return newNumber;
	  }

	  public void setNewNumber(Integer newNumber) {
	    this.newNumber = newNumber;
	  }
}
