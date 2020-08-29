package es.tfm.usermanagement.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class GameUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String userName;
		
//	@OneToMany(mappedBy="groupAdmin")
//	private List<UserGroup> adminForGroups;

//	@OneToMany(mappedBy="gameAdmin")
//	private List<Game> adminForGames;
	
//	@OneToMany(orphanRemoval = true,cascade=CascadeType.ALL,mappedBy="myUser")
//	private List<Player> currentAssociatedPlayers;

	
	public GameUser() {}
	
	public GameUser(String userName) {
		this.userName = userName;
//		adminForGroups = new ArrayList<UserGroup>();
//		adminForGames = new ArrayList<Game>();
//		currentAssociatedPlayers = new ArrayList<Player>();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
//	@JsonIgnore
//	public List<UserGroup> getAdminForGroups() {
//		return adminForGroups;
//	}
//	
//	public void setAdminForGroups(List<UserGroup> adminForGroups) {
//		this.adminForGroups = adminForGroups;
//		
//	}
	
//	@JsonIgnore
//	public List<Game> getAdminForGames() {
//		return adminForGames;
//	}
//	
//	public void setAdminForGames(List<Game> adminForGames) {
//		this.adminForGames = adminForGames;
//	}	
//	@JsonIgnore
//	public List<Player> getCurrentAssociatedPlayers() {
//		return currentAssociatedPlayers;
//	}
//	
//	public void setCurrentAssociatedPlayers(List<Player> currentAssociatedPlayers) {
//		this.currentAssociatedPlayers = currentAssociatedPlayers;
//	}
}
