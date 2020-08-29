package es.tfm.bingo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import es.tfm.bingo.utils.*;

import javax.persistence.Entity;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Square {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	protected Integer coordX;
	protected Integer coordY;
	protected Integer number;

	transient protected ClosedInterval xInterval;

	transient protected ClosedInterval yInterval;

	transient protected ClosedInterval numbersInterval;

	protected Square() {}

	protected Square(BingoGameLimits bingoLimits, int coordX, int coordY, int number) {
		setLimits(bingoLimits);

		setCoordX(coordX);
		setCoordY(coordY);
		setNumber(number);
	}

	
	private void setLimits(BingoGameLimits bingoLimits) {
		this.xInterval = new ClosedInterval(bingoLimits.getMinRow(), bingoLimits.getMaxRow());
		this.yInterval = new ClosedInterval(bingoLimits.getMinColumn(), bingoLimits.getMaxColumn());
		this.numbersInterval = new ClosedInterval(bingoLimits.getMinNumber() - 1, bingoLimits.getMaxNumber());
	}

	
	public Integer getCoordX() {
		return this.coordX;
	}

	
	public void setCoordX(Integer x) {

		assert xInterval.includes(x);

		this.coordX = x;
	}

	
	public Integer getCoordY() {
		return this.coordY;
	}
	

	public void setCoordY(Integer y) {

		assert yInterval.includes(y);

		this.coordY = y;
	}

	
	public Integer getNumber() {
		return this.number;
	}

	
	public void setNumber(Integer number) {

		assert numbersInterval.includes(number);

		this.number = number;
	}

	
	@Override
	public String toString() {

		return "{coordX: "+ coordX + ", coordY:"+coordY + ", number: " + number + "}";
	}
}
