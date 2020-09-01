package es.tfm.usermanagement.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Player implements Comparable<Player>{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String playerName;
	
	
	@ManyToOne
	private UserGroup myGroup;
	
	@ManyToMany(mappedBy="players")
	private List<Game> myGames;
	
	private Integer points;
	
	private boolean isGroupAdmin;
			
	public Player() {}
	
	public Player(String playerName) {
		this.playerName = playerName;
		this.points = 0;
		this.myGames = new ArrayList<Game>();
		this.isGroupAdmin = false;
	}

	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	
	public String getPlayerName() {
		return playerName;
	}

	
	@JsonIgnore
	public UserGroup getMygroup() {
		return myGroup;
	}
	
	public void setGroup(UserGroup userGroup) {
		this.myGroup = userGroup;
	}

	
	public Integer getPoints() {
		return points;
	}

	
	public void setPoints(Integer points) {
		this.points = points;
	}
	
	
	public boolean getIsGroupAdmin() {
		return isGroupAdmin;
	}

	
	public void setIsGroupAdmin(boolean isGroupAdmin) {
		this.isGroupAdmin = isGroupAdmin;
	}
	
    @Override
    public int compareTo(Player o) {
        return this.getPoints().compareTo(o.getPoints());
    }
    
    
	@JsonIgnore
	public List<Game> getMyGames() {
		return myGames;
	}
	
	
	public void setMyGames(List<Game> myGames) {
		this.myGames = myGames;
	}
}
