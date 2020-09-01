package es.tfm.bingo.model;

import javax.persistence.InheritanceType;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.tfm.bingo.utils.*;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class BingoGame {
	
	@Id
	protected Long id;
	
	protected String gameName;
	protected String groupName;

	protected Timestamp creationTimestamp;
	protected BingoStateValues status;
	protected boolean minorPrizeAlreadyClaimed;
	protected boolean minorPrizeNoLongerAvailable;
	
	@OrderColumn
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(orphanRemoval = true, cascade=CascadeType.ALL)
	protected List<Ball> balls;
	
	@OneToMany(orphanRemoval = true, cascade=CascadeType.ALL,mappedBy="bingoGame")
	protected List<BingoCard> bingoCards;
	
	@OneToMany(orphanRemoval = true, cascade=CascadeType.ALL,mappedBy="game")
	protected List<Player> players;
	
	transient
	protected BingoGameLimits bingoLimits;
	
	
	public BingoGame() {}
	
	
	protected BingoGame(Long id,String groupName,String gameName,BingoGameLimits bingoLimits) {
		
		this.id = id;
		this.gameName = gameName;
		this.groupName = groupName;
		this.bingoLimits = bingoLimits;

		this.status = BingoStateValues.INITIAL ;
		this.creationTimestamp = new Timestamp(System.currentTimeMillis());
		this.minorPrizeNoLongerAvailable = false;
		this.minorPrizeAlreadyClaimed = false;
		
		this.balls = new ArrayList<Ball>();
		this.bingoCards = new ArrayList<BingoCard>();
		
		this.players = new ArrayList<Player>();
	}
	
	
	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	
	public String getGameName() {
		return gameName;
	}

	
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	
	@JsonIgnore
	public String getGroupName() {
		return groupName;
	}

	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	

	@JsonIgnore
	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	
	public BingoStateValues getStatus() {
		return status;
	}

	
	public void setStatus(BingoStateValues status) {
		this.status = status;
	}

	
	@JsonIgnore
	public boolean getMinorPrizeAlreadyClaimed() {
		return minorPrizeAlreadyClaimed;
	}


	public void setMinorPrizeAlreadyClaimed(boolean minorPrizeAlreadyClaimed) {
		this.minorPrizeAlreadyClaimed = minorPrizeAlreadyClaimed;
	}
	
	@JsonIgnore
	public boolean getMinorPrizeNoLongerAvailable() {
		return minorPrizeNoLongerAvailable;
	}


	public void setMinorPrizeNoLongerAvailable(boolean minorPrizeNoLongerAvailable) {
		this.minorPrizeNoLongerAvailable = minorPrizeNoLongerAvailable;
	}
	
	
	@JsonIgnore
	public List<Ball> getBalls(){
		return this.balls;
	}
	
	
	@JsonIgnore
	public List<Player> getPlayers() {
		return players;
	}

	
	public void setPlayers(List<Player> players) {
		if (this.players == null) {
		    this.players = players;
		} else {
		    this.players.retainAll(players);
		    this.players.addAll(players);
		}
	}	
	
	
	@JsonIgnore
	public List<BingoCard> getBingoCards() {
		return bingoCards;
	}
	   
	
	protected void setBingoLimits (BingoGameLimits bingoLimits) {
		this.bingoLimits = bingoLimits;
	}
	
	protected boolean checkBingo(BingoCard bingoCard) {
				
		for (Square square:bingoCard.getSquares()) {
			int number = square.getNumber();
			
		    if ((number!=0) && (this.balls.get(number-1).getIsMarked()==false))
			    return false;
		}
		
		return true;
	}
	
	
	protected boolean checkLinePrize(BingoCard bingoCard) {
		if (minorPrizeNoLongerAvailable == true)
			return false;
				
		for (int i = bingoLimits.getMinRow(); i<=bingoLimits.getMaxRow();i++) {
			int markedNumbersInLine = 0;
		
			for (int j=bingoLimits.getMinColumn(); j<=bingoLimits.getMaxColumn();j++) {
				int number = bingoCard.getNumberInPosition(i,j);
			    if (number != 0) {
				   if (balls.get(number-1).getIsMarked() == true)
                       markedNumbersInLine++;
			    }
				else
				   markedNumbersInLine++;
			}
					
			if (markedNumbersInLine == bingoLimits.getMaxColumn()) {
				minorPrizeAlreadyClaimed = true;
				return true;
			}
		}		

		return false;
	}
	
	
    public abstract void setBalls(List<Ball> balls);
    public abstract void setBingoCards(List<BingoCard> bingoCards);
	public abstract BingoCard generateAleatoryCard();
	public abstract boolean isCorrectClaimedPrize(String prizeType,BingoCard bingoCard);
}