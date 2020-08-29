package es.tfm.usermanagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.utils.*;

@Service
public class UserService extends UserManagementService{
	
	@Autowired
	GroupService groupService;
	
	
    public GameUser addUser(String userName) throws ExceptionErrorInApp {
    	
		GameUser savedUser = userRepository.findByUserName(userName);
		
		if (savedUser != null)
            throw new ExceptionErrorInApp(Result.USER_ALREADY_EXISTS);

		return userRepository.save(new GameUser(userName));
    }
    
    
    public GameUser deleteUser(Long userId) throws ExceptionErrorInApp {
    	
		GameUser savedUser = getUserById(userId);

 		if ((this.getGroupsForWhomUserisAdmin(userId).size() > 0) ||
 		   (this.getGamesForWhomUserisAdmin(userId).size() > 0))
              throw new ExceptionErrorInApp(Result.USER_CANNOT_BE_DELETED_IS_ADMIN);
 		
 		List<Player> playersToDelete = playerRepository.findByPlayerName(savedUser.getUserName());
 		
 		for(Player player:playersToDelete) {
 			player.getMygroup();
 			player.getMyGames().size();
 			playerRepository.deleteById(player.getId());
 		}
 		
  		userRepository.deleteById(userId);

 		return savedUser;
    }
    
        
    public List<UserGroup> getGroupsForUser (Long userId) throws ExceptionErrorInApp{
    	
		GameUser savedUser = getUserById(userId);
		
		List<Player> players = playerRepository.findByPlayerName(savedUser.getUserName());
		
		List<UserGroup> groupsForUser = new ArrayList<UserGroup>();
		
		for (Player playerInGroup:players) {
			groupsForUser.add(playerInGroup.getMygroup());
		}
		
		return groupsForUser;
    }
    
    
    public List<UserGroup> getGroupsForWhomUserisAdmin (Long userId) throws ExceptionErrorInApp{
    	
		GameUser savedUser = getUserById(userId);
 			 		
		List<Player> players = playerRepository.findByPlayerNameAndIsGroupAdmin(savedUser.getUserName(),true);
 		
		List<UserGroup> groupsForUser = new ArrayList<UserGroup>();
		
		for (Player playerInGroup:players) {
			groupsForUser.add(playerInGroup.getMygroup());
		}
		
		return groupsForUser;
    }
    
    
    public List<Game> getGamesForWhomUserisAdmin (Long userId) throws ExceptionErrorInApp{
    	
		GameUser savedUser = getUserById(userId);
		
 		return gameRepository.findByGameAdmin_PlayerName(savedUser.getUserName());
    }  
    
    
    public List<Game> getGamesAvailableForUser (Long userId) throws ExceptionErrorInApp{
    				
		List<UserGroup> groupsForUser = this.getGroupsForUser(userId);
		
		List<Game> availableGames = new ArrayList<Game>();
		
		for (UserGroup groupForUser:groupsForUser) {
			UserGroup group = groupRepository.findById(groupForUser.getId()).get();

			List<Game> availableGamesForGroup = groupService.getGamesForGroupNotStartedYet(group.getId());
			
			availableGames.addAll(availableGamesForGroup);
		}
		
	    return availableGames;
    }  
    
    
    public List<Player> getPlayersCurrentlyAssociated(Long userId) throws ExceptionErrorInApp{
		GameUser savedUser = getUserById(userId);
		
		return playerRepository.findByPlayerName(savedUser.getUserName());
    }
    
    
    public List<GameUser> getAllUsers() {
		return userRepository.findAll();
    }
}
