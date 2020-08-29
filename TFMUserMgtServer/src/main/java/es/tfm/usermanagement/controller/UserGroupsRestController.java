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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfm.usermanagement.model.*;
import es.tfm.usermanagement.service.*;
import es.tfm.usermanagement.utils.ExceptionErrorInApp;


@Controller
@RestController
@RequestMapping("/groupmgt")
public class UserGroupsRestController {
	
	@Autowired
	private GroupService groupService;
	
	
	@PostMapping("/createUserGroup/{groupName}/{userAdminId}")
	public ResponseEntity<Object> createUserGroup(@PathVariable String groupName, @PathVariable Long userAdminId) {

		System.out.println ("===> Request for creating a user group with name "+groupName);

		UserGroup newGroup;
		try {
			newGroup = groupService.createUserGroup(groupName,userAdminId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
			
		return new ResponseEntity<>(newGroup, HttpStatus.CREATED); 
	}
	
	
	@PutMapping("/changeAdminToGroup/{groupId}/{userName}")
	public ResponseEntity<Object> changeAdminToGroup(@PathVariable Long groupId, @PathVariable String userName) {

		System.out.println ("===> Request for changing admin for user group with id "+groupId);

		UserGroup updatedGroup;
		try {
			updatedGroup = groupService.changeAdminToGroup(groupId,userName);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
				
		return new ResponseEntity<>(updatedGroup, HttpStatus.CREATED); 
	}
	
	
	@PostMapping("/addMemberToGroup/{groupId}/{newMemberName}")
	public ResponseEntity<Object> addMemberToGroup(@PathVariable Long groupId, @PathVariable String newMemberName) {

		System.out.println ("===> Request for adding a player with name "+newMemberName
	            +" to user group with id "+groupId);
		
		Player addedMember;
		try {
			addedMember = groupService.addPlayerToGroup(groupId,newMemberName);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
			
		return new ResponseEntity<>(addedMember, HttpStatus.CREATED); 
	}
	
	
	@DeleteMapping("/deleteMemberFromGroup/{groupId}/{playerId}")
	public ResponseEntity<Object> deleteMemberFromGroup(@PathVariable Long groupId,@PathVariable Long playerId) {

		System.out.println ("===> Request for deleting a player with id "+playerId
	                        +" from user group with id "+groupId);
		
		Player deletedMember;
		try {
			deletedMember = groupService.deleteMemberFromGroup(groupId,playerId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
			
		return new ResponseEntity<>(deletedMember, HttpStatus.CREATED); 
	}
	
	
	@DeleteMapping("/deleteGroup/{groupId}")
	public ResponseEntity<Object> deleteGroup(@PathVariable Long groupId) {

		System.out.println ("===> Request for deleting user group with id "+groupId);
		
		UserGroup deletedGroup;
		try {
			deletedGroup = groupService.deleteGroup(groupId);
		} catch (ExceptionErrorInApp e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<>(deletedGroup, HttpStatus.CREATED); 
	}
	
	
	@GetMapping("/getRanking/{groupId}/{firstXplayers}")
	public List<Player> getRankingFirstXPlayers(@PathVariable Long groupId, @PathVariable int firstXplayers){

		System.out.println ("===> Request for getting the ranking of first "+firstXplayers
				            +" from user group with id "+groupId);

		try {
			return groupService.getRankingOfFirstXInGroup(groupId,firstXplayers);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
	}
	
	
	@GetMapping("/getAllGroups")
	public List<UserGroup> getAllGroups()
	{
		System.out.println ("===> Request for getting all groups");

		return groupService.getAllGroups();		
	}
	
	
	@GetMapping("/getGroup/{groupId}")
	public UserGroup getGroup(@PathVariable Long groupId)
	{
		System.out.println ("===> Request for getting group with id "+groupId);

		try {
		    return groupService.getGroup(groupId);	
		} catch (ExceptionErrorInApp e) {
			return null;
		}
	}
	
	
	@GetMapping("/getAllPlayers")
	public List<Player> getAllPlayers()
	{
		System.out.println ("===> Request for getting all players");

		return groupService.getAllPlayers();		
	}
	
	
	@GetMapping("/getPlayersForGroup/{groupId}")
	public List<Player> getPlayersForGroup(@PathVariable Long groupId)
	{
		System.out.println ("===> Request for getting all players for group with id "+groupId);

		try {
		    return groupService.getGroupPlayers(groupId);
		} catch (ExceptionErrorInApp e) {
			return null;
		}
	}
}
