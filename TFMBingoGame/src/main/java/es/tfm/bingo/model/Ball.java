package es.tfm.bingo.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.tfm.bingo.utils.ClosedInterval;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Ball {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
    protected int number;
    
    protected boolean isMarked = false;
    
	transient
	protected ClosedInterval interval;
	   
	protected Ball() {}
	
	protected Ball(BingoGameLimits bingoLimits,String gameName,int number) {
		this.interval = new ClosedInterval(bingoLimits.getMinNumber(),bingoLimits.getMaxNumber());

		this.number = number;
	}

	@JsonIgnore
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		assert interval.includes(number);
		
		this.number = number;
	}
	
	public boolean getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}
}
