package es.tfm.bingo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.tfm.bingo.websocket.EventBingoCard;

@Entity
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long foreignId;
	
	private String playerName;
		
	@ManyToOne
	private BingoGame game;

	@LazyCollection(LazyCollectionOption.FALSE)	
	@OneToMany(orphanRemoval = true)
	private List<BingoCard> bingoCards;
	
	public Player() {}
	
	public Player(BingoGame game,Long foreignId,String playerName) {
		this.foreignId = foreignId;
		this.playerName = playerName;
		this.game = game;
		this.bingoCards = new ArrayList<BingoCard>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getForeignId() {
		return foreignId;
	}

	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	@JsonIgnore
	public BingoGame getGame() {
		return game;
	}

	public void setGame(BingoGame game) {
		this.game = game;
	}

	public List<BingoCard> getBingoCards() {
		return bingoCards;
	}
	
	public void setBingoCards(List<BingoCard> bingoCards) {
		
		if (this.bingoCards == null) {
		    this.bingoCards = bingoCards;
		} else {
		    this.bingoCards.retainAll(bingoCards);
		    this.bingoCards.addAll(bingoCards);
		}	
	}
	
	public boolean doesCardBelongToUser(EventBingoCard bingoCard) {
		
		for (BingoCard card:bingoCards)
			if (card.equalsValues(bingoCard))
		        return true;
		
		return false;
	}
	
}
