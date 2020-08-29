package es.tfm.usermanagement.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.service.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;


@Controller
@RestController
@RequestMapping("/usermgt")
public class GameUserRestController {

	@Autowired
	private UserService userService;
	

	@PostMapping("/addUser/{userName}")
	public ResponseEntity<Object> addUser(@PathVariable String userName) {

		System.out.println ("===> Request for creating a user with name "+userName);
		GameUser gameUser;
		try {
			gameUser = userService.addUser(userName);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
				
		return new ResponseEntity<>(gameUser, HttpStatus.CREATED); 
	}
	
	
	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {

		System.out.println ("===> Request for deleting user with id "+userId);

		GameUser deletedUser;
		try {
			deletedUser = userService.deleteUser(userId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
			
		return new ResponseEntity<>(deletedUser, HttpStatus.CREATED); 
	}
	
	
	@GetMapping("/getUserGroups/{userId}")
	public List<UserGroup> getUserGroups(@PathVariable Long userId ) {

		System.out.println ("===> Request for getting the groups that user with id "+userId+" belongs to");

		List<UserGroup> groups;
		try {
			groups = userService.getGroupsForUser(userId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
			
		return groups; 
	}
	
	
	@GetMapping("/getGroupsUserIsAdmin/{userId}")
	public List<UserGroup> getGroupsUserIsAdmin(@PathVariable Long userId ) {
		
		System.out.println ("===> Request for getting the groups where user with id "+userId+" is admin");

		List<UserGroup> groups;
		try {
			groups = userService.getGroupsForWhomUserisAdmin(userId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
			
		return groups; 
	}
	
	
	@GetMapping("/getGamesUserIsAdmin/{userId}")
	public List<Game> getGamesUserIsAdmin(@PathVariable Long userId ) {

		System.out.println ("===> Request for getting the games where user with id "+userId+" is admin");

		List<Game> games;
		try {
			games = userService.getGamesForWhomUserisAdmin(userId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
			
		return games; 
	}

	
	@GetMapping("/getGamesAvailableForUser/{userId}")
	public List<Game> getGamesAvailableForUser(@PathVariable Long userId ) {

		System.out.println ("===> Request for getting the games where user with id "+userId+" can join");

		List<Game> games;
		try {
			games = userService.getGamesAvailableForUser(userId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
			
		return games; 
	}
	
	
	@GetMapping("/getPlayersCurrentlyAssociated/{userId}")
	public List<Player> getPlayersCurrentlyAssociated(@PathVariable Long userId ) {

		System.out.println ("===> Request for getting the list of Players associated to user with id "+userId+" can join");

		List<Player> players;
		try {
			players = userService.getPlayersCurrentlyAssociated(userId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
			
		return players; 
	}
	
	
	@GetMapping("/getAllUsers")
	public List<GameUser> getAllUsers() {

		System.out.println ("===> Request for getting all users");
		
		return userService.getAllUsers();
	}
}
