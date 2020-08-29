package es.tfm.bingo.model;

public class Coordinate {
	
	public int coordX;
	public int coordY;
	
	public Coordinate(Integer x, Integer y){
		this.coordX = x;
		this.coordY = y;
	}
	
	public Integer getCoordX() {
		return this.coordX;
	}
		
	public Integer getCoordY() {
		return this.coordY;
	}
	
	public void setCoordX(Integer x) {
		coordX = x;
	}
	public void setCoordY(Integer y) {
		coordY = y;
	}
	
	@Override
	public int hashCode() {

		final int prime = 31;

		int result = 1;

		result = prime * result + coordY;
		result = prime * result + coordX;

		return result;

	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Coordinate other = (Coordinate) obj;

		if (this.coordY != other.coordY)
			return false;

		if (this.coordX != other.coordX)
			return false;

		return true;

	}
	
	public String toString() {
		return "["+coordX+"-"+coordY+"]";
	}
}
