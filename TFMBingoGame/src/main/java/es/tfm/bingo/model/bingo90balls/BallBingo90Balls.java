package es.tfm.bingo.model.bingo90balls;

import javax.persistence.Entity;

import es.tfm.bingo.model.Ball;

@Entity
public class BallBingo90Balls extends Ball {
	
	public BallBingo90Balls() {}

	public BallBingo90Balls(String gameName,int number) {
		super(BingoGame90BallsLimits.instance(),gameName,number);
	}
}
