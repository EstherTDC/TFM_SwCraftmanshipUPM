package es.tfm.bingo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.tfm.bingo.utils.ClosedInterval;
import es.tfm.bingo.websocket.EventBingoCard;


@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class BingoCard {

	@Id
	protected String id;
	
	@ManyToOne
	private BingoGame bingoGame;

	@OrderColumn
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval = true, cascade=CascadeType.ALL)
	protected List<Square> squares;
	
	transient
	protected Map<Coordinate,Integer> mappedSquares;
		
	transient
	protected BingoGameLimits bingoLimits;

	
	protected BingoCard(BingoGameLimits bingoLimits) {
		this.bingoLimits = bingoLimits;
		
		this.setSquares(new ArrayList<Square>());
	}
	
	
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}
	
	
	@JsonIgnore
	public BingoGame getBingoGame() {
		return bingoGame;
	}


	public void setBingoGame(BingoGame bingoGame) {
		this.bingoGame = bingoGame;
	}
	

	public List<Square> getSquares() {
		return squares;
	}
	

	public void setSquares(List<Square> squares) {
		
		assert (squares.size() == (bingoLimits.getMaxRow()*bingoLimits.getMaxColumn())) ||
		       (squares.size() == 0);
		
		for (Square square:squares) {

	    	assert new ClosedInterval(bingoLimits.getMinRow(),bingoLimits.getMaxRow())
                                    .includes(square.getCoordX());
            assert new ClosedInterval(bingoLimits.getMinColumn(),bingoLimits.getMaxColumn())
                                    .includes(square.getCoordY());
            assert new ClosedInterval(bingoLimits.getMinNumber()-1,bingoLimits.getMaxNumber())
                                    .includes(square.getNumber());
			checkSquareTypeCorrectness(square);
		}
				
		if (this.squares == null) {
		    this.squares = squares;
		} else {
		    this.squares.retainAll(squares);
		    this.squares.addAll(squares);
		}
		
		this.setMappedSquares(this.squares);
		
		if (this.squares.size()>0)
	        checkCoordinatesCorrectness(mappedSquares);
	}
	
	
	private void setMappedSquares(List<Square> squares) {
		this.mappedSquares = new HashMap<Coordinate,Integer>();
		
		for (Square squareInList:squares) {

	        this.mappedSquares.put(new Coordinate(squareInList.getCoordX(),
	        		                              squareInList.getCoordY()),
	        		                              (Integer)squareInList.getNumber());
		}
		
	}
	
	
	private void checkCoordinatesCorrectness(Map<Coordinate,Integer> mappedSquares) {

		for (int i = bingoLimits.getMinRow(); i<=bingoLimits.getMaxRow(); i++)
			for (int j = bingoLimits.getMinColumn(); j<=bingoLimits.getMaxColumn();j++) {
				assert mappedSquares.containsKey(new Coordinate(i,j));
			}
	}
	
		
	@JsonIgnore
	public List<Integer> getCardNumbers(){
		List<Integer> numbers = new ArrayList<Integer>();
		
		for (Square squareInList:this.squares)
			numbers.add(squareInList.getNumber());
					
		Collections.sort(numbers);
		
		numbers.removeAll(Arrays.asList(0));
		
		return numbers;
	}
	
	
	public String cardNumbersToString() {
	
		List<Integer> numbers = getCardNumbers();

		return numbers.stream()
	      .map(n -> String.valueOf(n))
	      .collect(Collectors.joining("-", "", ""));
	}
	
	
	public int getNumberInPosition(int coordX,int coordY) {
		
		if (this.mappedSquares.size()==0)
    		this.setMappedSquares(this.squares);
		
	    return mappedSquares.get(new Coordinate(coordX,coordY)); 
	}
	
  
	public boolean equalsValues(Object obj) {

		if (this == obj)
			return true;

		if (obj == null)
			return false;

		EventBingoCard other = (EventBingoCard) obj;
		
		if (this.toString() == null) {
			if (other.toString() != null)
				return false;

		} else if (!((this.id.equals(other.getId()) && (this.toString().equals(other.toString())))))
			return false;
		
		return true;
	}
	
	
	public BingoCard generateAleatoryCard(BingoGame game){
		
		Map<Coordinate,Integer> baseCard = generateCardNumbers();
		
	    setWhitePositions(baseCard);
	    	    		
		return getNewBingoCard(game,baseCard);
	}
	
	
	protected BingoCard getNewBingoCard(BingoGame game,Map<Coordinate,Integer> card ) {
			
		BingoCard bingoCard = provideBaseCard();
		
		List<Square> listOfSquares = new ArrayList<Square>();
		
		for (int i = bingoLimits.getMinRow(); i<=bingoLimits.getMaxRow(); i++) {
  		    for (int j= bingoLimits.getMinRow(); j<=bingoLimits.getMaxColumn(); j++) {
  		    	listOfSquares.add(provideSquare(i,j,card.get(new Coordinate(i,j))));			  
		    }
	    }
		
		bingoCard.setSquares(listOfSquares);
		bingoCard.setBingoGame(game);
		bingoCard.setId(game.getGroupName()+"_"+game.getGameName()+"_"+bingoCard.cardNumbersToString());
		
		return bingoCard;
	}
	
	
	@Override
    public String toString() {
  	
		String card ="squares: [";
		
		for (int i=0; i<squares.size();i++)
			card+= squares.get(i).toString();
		
		card+="]";
		return card;
    }
	
	
	protected abstract Map<Coordinate,Integer> generateCardNumbers();
	protected abstract Map<Coordinate,Integer> setWhitePositions(Map<Coordinate,Integer> card);
	protected abstract BingoCard provideBaseCard();
	protected abstract Square provideSquare(int coordX,int coordY,int number);
	protected abstract void checkSquareTypeCorrectness(Square square);

}
