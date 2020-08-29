package es.tfm.bingo.websocket;

import java.util.ArrayList;
import java.util.List;


public class EventBingoCard{
	
	protected String id;
	
	private List<EventSquare> squares;
	
	
	public EventBingoCard() {
		this.squares = new ArrayList<>();
	}	
	

	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}
	

	public List<EventSquare> getSquares() {
		return squares;
	}
	
	
	public void setSquares(List<EventSquare> squares) {
		this.squares = squares;
	}
		
	@Override
    public String toString() {
  	  	
		String card ="squares: [";
			
		for (int i=0; i<squares.size();i++)
			card+= squares.get(i).toString();
			
		card+="]";
		return card;
    }
}
