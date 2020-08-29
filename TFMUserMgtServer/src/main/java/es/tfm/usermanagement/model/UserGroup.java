package es.tfm.usermanagement.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;



@Entity
public class UserGroup {
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String groupName;
	
	@OneToOne
	private Player groupAdmin;
	
	@OneToMany(orphanRemoval = true,cascade=CascadeType.ALL,mappedBy="myGroup")
	private List<Player> groupPlayers;

	@OneToMany(orphanRemoval = true,cascade=CascadeType.ALL,mappedBy="userGroup")
	private List<Game> groupGames;

    public UserGroup() {}
	
	public UserGroup(String groupName) {
		this.groupName = groupName;
        this.groupPlayers = new ArrayList<Player>();
        this.groupGames = new ArrayList<Game>();
	}
	
	
	public Long getId() {
		return id;
	}
	
	
	public void setId(Long id) {
		 this.id = id;
	}
	
	
	public String getGroupName() {
		return groupName;
	}

	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	public Player getGroupAdmin() {
		return groupAdmin;
	}

	
	public void setGroupAdmin(Player groupAdmin) {
		this.groupAdmin = groupAdmin;
	}
	
	
	public List<Player> getGroupPlayers(){
		return groupPlayers;
	}
	
	
	public void setGroupPlayers(List<Player> groupPlayers){
		this.groupPlayers = groupPlayers;
	}
	
	
	public List<Game> getGroupGames(){
		return groupGames;
	}
	
	
	public void setGroupGames(List<Game> groupGames){
		this.groupGames = groupGames;
	}
	
	
	public boolean doesPlayerBelongToGroup(Player player) {
		return this.groupPlayers.contains(player);
	}
}
