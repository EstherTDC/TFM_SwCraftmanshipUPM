package es.tfm.bingo.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import es.tfm.bingo.model.BingoGame;
import es.tfm.bingo.service.*;
import es.tfm.bingo.utils.ClaimedPrize;
import es.tfm.bingo.utils.ExceptionErrorInApp;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Controller
public class BingoEventController {
    @Autowired
    private SimpMessagingTemplate template;
    
	@Autowired
	private BingoGameService bingoGameService;
	   
	public BingoEventController(){}
	
    
	@MessageMapping("/userEvent/") 
	public void userEvent(Principal principal, EventMessage message) {
		
		Long gameId = message.getGameId();

		try {
		    if (bingoGameService.stopGame(gameId)!=null) {
		
            	boolean correct = bingoGameService.isCalledPrizeCorrect (gameId, 
       			                                                 message.getPlayerId(),
       			                                                 message.getEventType(),
       			                                                 message.getBingoCard());
		
        	    if (correct) {
        	    	sendNotificationToUser(principal,"CORRECT_PRIZE","El evento cantado '"+message.getEventType()+"' es correcto");
                    System.out.println("<== Claimed event '"+message.getEventType()+"' for game with id "+gameId+" is correct");

                    EventNotification eventNotification= new EventNotification("CORRECT_PRIZE","");

        	        if (message.getEventType().equals(ClaimedPrize.BINGO.get())) {
                       eventNotification.setMessage("Se ha cantado Bingo. La partida ha terminado");
            	       this.template.convertAndSend("/topic/notifBingo"+gameId, eventNotification); 	
        	        }
            	    else {
                       eventNotification.setMessage("Se ha cantado un premio menor. "+
                		                            "A partir de ahora ya solo se puede cantar Bingo");

         	           this.template.convertAndSend("/topic/notifMinorPrize"+gameId, eventNotification);
            	    }
       	        }
       	        else {
                    System.out.println("<== Claimed event '"+message.getEventType()+
     	    	                       "' for game with id "+gameId+" is not correct");
		           
        	    	sendNotificationToUser(principal,"NOT_CORRECT_PRIZE","El evento cantado '"+message.getEventType()+"' no es correcto");
       	        }
		        bingoGameService.startGame(gameId);
		    }
		}catch (ExceptionErrorInApp e) {
	    	sendNotificationToUser(principal,"ERROR",e.getMessage());
        }
	}
	
	
	@MessageMapping("/startGame") 
	private void startGame(Principal principal,GameAdministratorMessage gameAdministratorMessage) {

		Long gameId = gameAdministratorMessage.getGameId();
		
		try {
		    if (bingoGameService.startGame(gameId) != null) {
			
		        EventNotification eventNotification= new EventNotification("START","");
                eventNotification.setMessage("¡A jugar!");

	    	    this.template.convertAndSend("/topic/start"+gameId,eventNotification);

		    	System.out.println("==> Game with id "+gameAdministratorMessage.getGameId()+" started");
		    }
		}catch (ExceptionErrorInApp e) {
	    	sendNotificationToUser(principal,"ERROR",e.getMessage());
        }
	}
	
	
	@MessageMapping("/stopGame") 
	private void stopGame(Principal principal,GameAdministratorMessage gameAdministratorMessage) {

		Long gameId = gameAdministratorMessage.getGameId();

		try{	
	    	if (bingoGameService.stopGame(gameId) != null) {
			
	    	    EventNotification eventNotification= new EventNotification("STOP","");
                eventNotification.setMessage("El juego se ha parado");
             
	        	this.template.convertAndSend("/topic/stop"+gameId,eventNotification);

  		        System.out.println("==> Game with id "+gameAdministratorMessage.getGameId()+" stopped");
	    	}
		}catch (ExceptionErrorInApp e) {
	    	sendNotificationToUser(principal,"ERROR",e.getMessage());
        }
	}
	
	
	@MessageMapping("/getBall")
    public void getBall(Principal principal,GameAdministratorMessage gameAdministratorMessage) {
		
		Long gameId = gameAdministratorMessage.getGameId();

		try {
	    	Integer newNumber = bingoGameService.generateNumber(gameId);	
		
    	    this.template.convertAndSend("/topic/"+gameId, newNumber);
    	    
		}catch (ExceptionErrorInApp e) {
	    	sendNotificationToUser(principal,"ERROR",e.getMessage());
        }
	}
	
	
	private void sendNotificationToUser(Principal principal, String notifType,String message) {
        EventNotification eventNotification= new EventNotification(notifType,"");
        eventNotification.setMessage(message);
        Map<String, Object> headers = new HashMap<>();
        headers.put("auto-delete", "true");
	    this.template.convertAndSendToUser(principal.getName(),"/queue/reply", eventNotification,headers);
	}
}
