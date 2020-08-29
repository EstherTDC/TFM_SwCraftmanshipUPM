package es.tfm.usermanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.repository.*;
import es.tfm.usermanagement.utils.*;


@Service
public abstract class UserManagementService {
	
	@Autowired
	GameRepository gameRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PlayerRepository playerRepository;
	
	@Autowired
	GroupRepository groupRepository;
	
    
    protected UserGroup getUserGroupById(Long groupId) throws ExceptionErrorInApp {
		Optional<UserGroup> savedGroup = groupRepository.findById(groupId);
		
		if (!savedGroup.isPresent())
            throw new ExceptionErrorInApp(Result.GROUP_NOT_FOUND);
		
		return savedGroup.get();
    }
    
    
    protected GameUser getUserById(Long userId) throws ExceptionErrorInApp {
		Optional<GameUser> savedUser = userRepository.findById(userId);
		
		if (!savedUser.isPresent())
            throw new ExceptionErrorInApp(Result.USER_NOT_FOUND);
		
		return savedUser.get();
    }
    
    
    protected Game getGameById(Long gameId) throws ExceptionErrorInApp {
 		Optional<Game> savedGame = gameRepository.findById(gameId);
 		
 		if (!savedGame.isPresent())
             throw new ExceptionErrorInApp(Result.GAME_NOT_FOUND);
 		
 		return savedGame.get();
    }
    
    
    protected Player getPlayerById(Long playerId) throws ExceptionErrorInApp {
 		Optional<Player> savedPlayer = playerRepository.findById(playerId);
 		
 		if (!savedPlayer.isPresent())
             throw new ExceptionErrorInApp(Result.PLAYER_NOT_FOUND);
 		
 		return savedPlayer.get();
     }
}
