package es.tfm.bingo.model.bingo90balls;

import javax.persistence.Entity;

import es.tfm.bingo.model.Square;

@Entity
public class SquareCard90Balls extends Square{
	
	public SquareCard90Balls(){
	}
	
	public SquareCard90Balls(Integer x, Integer y,Integer number){
        super(BingoGame90BallsLimits.instance(),x,y,number);
   	}
}
