package es.tfm.usermanagement.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;
import es.tfm.usermanagement.utils.GameStates;
import es.tfm.usermanagement.utils.Result;


@Service
public class GroupService extends UserManagementService{
	

	public UserGroup createUserGroup(String groupName, Long adminId) throws ExceptionErrorInApp {
			
		UserGroup savedGroup = groupRepository.findByGroupName(groupName);
		
		if (savedGroup !=null)
 			throw new ExceptionErrorInApp(Result.GROUP_ALREADY_EXISTS);
		
		GameUser savedUser = getUserById(adminId);
		
		UserGroup newGroup = new UserGroup (groupName);
		
		Player playerAdmin = new Player(savedUser.getUserName());
		playerAdmin.setIsGroupAdmin(true);
		
		Player storedAdmin = playerRepository.save(playerAdmin);

		newGroup.setGroupAdmin(storedAdmin);
		newGroup.getGroupPlayers().add(storedAdmin);
		
		savedGroup = groupRepository.save(newGroup);
		
		storedAdmin.setGroup(savedGroup);
		playerRepository.save(storedAdmin);
			
	    return savedGroup;
	}
	
	
	public UserGroup deleteGroup(Long groupId) throws ExceptionErrorInApp {
		
		UserGroup savedGroup = getUserGroupById(groupId);
		
		savedGroup.getGroupAdmin();
	    savedGroup.getGroupPlayers().size();
	    savedGroup.getGroupGames().size();

	    groupRepository.deleteById(groupId);
	
	    return savedGroup;
	}
	
	
	public UserGroup changeAdminToGroup(Long groupId,String adminName) throws ExceptionErrorInApp {
		UserGroup savedGroup = getUserGroupById(groupId);
		
		Player newAdmin = playerRepository.findByPlayerNameAndMyGroup_id(adminName,groupId);
	
		if (newAdmin==null)
 			throw new ExceptionErrorInApp(Result.PROPOSED_ADMIN_DOES_NOT_BELONG_TO_GROUP);
		
		Player previousAdmin = savedGroup.getGroupAdmin();
		previousAdmin.setIsGroupAdmin(false);
		playerRepository.save(previousAdmin);
		playerRepository.save(newAdmin);

		newAdmin.setIsGroupAdmin(true);
		Player updatedPlayer = playerRepository.save(newAdmin);
		
		savedGroup.setGroupAdmin(updatedPlayer);
		
		return groupRepository.save(savedGroup);
	}
		
	
	public Player addPlayerToGroup(Long groupId,String userName) throws ExceptionErrorInApp {
		
		UserGroup savedGroup = getUserGroupById(groupId);
		
		GameUser savedUser = userRepository.findByUserName(userName);
		
		if (savedUser==null)
 			throw new ExceptionErrorInApp(Result.USER_NOT_FOUND);
		
	    Player existingPlayerInGroup = playerRepository.findByPlayerNameAndMyGroup_id(userName, groupId);
	    
	    if (existingPlayerInGroup!=null)
 			throw new ExceptionErrorInApp(Result.PLAYER_ALREADY_IN_GROUP);
		
		Player player = new Player(savedUser.getUserName());
		player.setGroup(savedGroup);
//		player.setUser(savedUser);
				
		return playerRepository.save(player);
	}
	

	public Player deleteMemberFromGroup(Long groupId,Long playerId) throws ExceptionErrorInApp {
		getUserGroupById(groupId);
		
		Player playerToRemove = getPlayerById(playerId);

		if (!(playerToRemove.getMygroup().getId()).equals(groupId))
 			throw new ExceptionErrorInApp(Result.PLAYER_DOES_NOT_BELONG_TO_GROUP);
		
		if (playerToRemove.getIsGroupAdmin())
 			throw new ExceptionErrorInApp(Result.PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GROUP_ADMIN);
		
		List<Game> players = gameRepository.findByGameAdmin_PlayerName(playerToRemove.getPlayerName());
 		
		if (players.size()>0)
 			throw new ExceptionErrorInApp(Result.PLAYER_CANNOT_BE_DELETED_FROM_GROUP_IS_GAME_ADMIN);
		
		playerToRemove.getMygroup();
//		playerToRemove.getMyUser();
		playerToRemove.getMyGames().size();
		
		playerRepository.deleteById(playerId); 
		
		return playerToRemove;
	}
	
    
    public List<Game> getGamesForGroup(Long groupId) throws ExceptionErrorInApp{
    	
		UserGroup savedGroup = getUserGroupById(groupId);
		
		return savedGroup.getGroupGames();
    }
    
    
    public List<Player> getGroupPlayers(Long groupId) throws ExceptionErrorInApp{
    	
		UserGroup savedGroup = getUserGroupById(groupId);
		
		return savedGroup.getGroupPlayers();
    }
    
    
    public List<Player> getRankingOfFirstXInGroup(Long groupId, int firstX) throws ExceptionErrorInApp{
    	
 		UserGroup savedGroup = getUserGroupById(groupId);
 	
 		if (savedGroup.getGroupPlayers().size()==0)
         	return savedGroup.getGroupPlayers();

 		List<Player> rankingPlayers = playerRepository.findByMyGroup_idOrderByPointsDesc(groupId);
 		
		if (rankingPlayers.size()>=firstX)
		   	return rankingPlayers.subList(0, firstX);
 		
 		return rankingPlayers.subList(0, rankingPlayers.size());	
    }
    
    
    public List<Game> getGamesForGroupNotStartedYet(Long groupId) throws ExceptionErrorInApp{
		getUserGroupById(groupId);
					
		return gameRepository.findByStatusAndUserGroup_Id(GameStates.INITIAL, groupId);
    }
    
    
	 public List<UserGroup> getAllGroups(){
		return groupRepository.findAll();
	 }
	 
	 
	 public UserGroup getGroup(Long groupId) throws ExceptionErrorInApp{

		UserGroup savedGroup = getUserGroupById(groupId);

		return savedGroup;
	 }
	 
	 
	 public List<Player> getAllPlayers(){
		return playerRepository.findAll();
	 }
	 
}
