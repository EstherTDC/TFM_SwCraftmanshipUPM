package es.tfm.bingo.websocket;


public class EventSquare{
	
	public Integer coordX;
	public Integer coordY;
	public Integer number;
	
	public EventSquare() {}
	
	public EventSquare(Integer coordX,Integer coordY,Integer number) {
		
		this.coordX = coordX;
		this.coordY = coordY;
		this.number = number;
	}
	
	public void setCoordX(Integer coordX) {
		this.coordX = coordX;
	}
	
	public Integer getCoordX() {
		return coordX;
	}
	
	public void setCoordY(Integer coordY) {
		this.coordY = coordY;
	}
	
	public Integer getCoordY() {
		return coordY;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	@Override
    public String toString() {		
		return "{coordX: "+ coordX + ", coordY:"+coordY + ", number: " + number + "}";
    }
}
