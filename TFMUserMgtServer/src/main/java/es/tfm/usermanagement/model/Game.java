package es.tfm.usermanagement.model;

import javax.persistence.ManyToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.tfm.usermanagement.utils.GameStates;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


@Entity
public class Game {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	protected String gameName;
	protected Timestamp creationTimestamp;
	
	protected GameStates status;
	
	@ManyToOne
	protected Player gameAdmin;
	
	@ManyToOne
	protected UserGroup userGroup;
	
	@ManyToMany
	protected List<Player> players;
	
	
	public Game() {}
		
	public Game(String gameName) {
		this.gameName = gameName;
		this.status = GameStates.INITIAL;
		this.creationTimestamp = new Timestamp(System.currentTimeMillis());
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


	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	
	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
	
	public Player getGameAdmin() {
		return gameAdmin;
	}


	public void setGameAdmin(Player gameAdmin) {
		this.gameAdmin = gameAdmin;
	}
	
	
	@JsonIgnore
	public UserGroup getGameGroup() {
		return userGroup;
	}
	
	public Long getGameGroupId() {
		return userGroup.getId();
	}

	
	public void setGameGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	
	public List<Player> getPlayers() {
		return players;
	}


	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	
	public GameStates getStatus() {
		return status;
	}
	
	
	public void setStatus(GameStates status) {
		this.status = status;
	}
	
	
    public boolean doesPlayerBelongToGame(Player player) {
		return this.players.contains(player);
	}
}