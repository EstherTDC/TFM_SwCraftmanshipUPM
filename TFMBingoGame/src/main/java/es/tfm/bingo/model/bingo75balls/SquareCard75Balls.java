package es.tfm.bingo.model.bingo75balls;

import javax.persistence.Entity;

import es.tfm.bingo.model.Square;


@Entity
public class SquareCard75Balls extends Square{
	
	public SquareCard75Balls(){}
	
	
	public SquareCard75Balls(Integer x, Integer y,Integer number){
        super(BingoGame75BallsLimits.instance(),x,y,number);
	}
}
