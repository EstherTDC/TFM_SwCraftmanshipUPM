package es.tfm.bingo.model.bingo75balls;

import javax.persistence.Entity;

import es.tfm.bingo.model.Ball;

@Entity
public class BallBingo75Balls extends Ball {
	
	public BallBingo75Balls() {}
	
	public BallBingo75Balls (String gameName, int number) {
		super(BingoGame75BallsLimits.instance(),gameName, number);
	}
}
