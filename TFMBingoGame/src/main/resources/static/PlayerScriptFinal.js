  $(document).ready(function () {
	         
     function connect(game) {

	    var socket = new SockJS(BINGO_WEB_SOCKET);
	    var playerIdInString = String.valueOf(player.id);

	    stompClient = Stomp.over(socket);
	    
	    stompClient.connect({}, function (frame) {

	        console.log('Connected: ' + frame);
	        	        
		    stompClient.subscribe(BINGO_GAME_SUBSCRIPTION+game.id, function (message) {

		    	
	        	var newNumber = JSON.parse(message.body); 
	        	
	         	if (newNumber != 0){
	            	if (newNumber != LAST_NUMBER){
	                   showNewNumber(newNumber);
	                   automaticCardsFillIn(player,newNumber);
		               updateLastFourNumbers();
 		               markPreviousNumberInBoard(newNumber);
	        	    }
	            	else {

	 		           markPreviousNumberInBoard(newNumber);
                	   disconnect();
	            	}
	            }
	        });
		    
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_START+game.id, function (message) {
	     	    game.status = IN_GAME_RUNNING_STATE;

	     	    var messageToShow = JSON.parse(message.body);
      	
	        	console.log("Start Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,50,2);
	        });
	        
	        
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_STOP+game.id, function (message) {
	   		    game.status = IN_GAME_STOPPED_STATE;

	   		    var messageToShow = JSON.parse(message.body);
	        	        	
	        	console.log("Stop Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,50,2);
	        });
	        
		    
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_MINOR_PRIZE+game.id, function (message) {
	        	
	        	minorPrizeAlreadyClaimed = true;
	        	
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Minor Prize Claimed Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,30,2);
	        });
	        
	        
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_BINGO+game.id, function (message) {
	        	
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Bingo Claimed Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,10000,50,2);
	        });
	        
	        
	        stompClient.subscribe(PERSONAL_NOTIFICATIONS, function (message) {
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Personal Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,50,10);
	        },{'auto-delete': true});
	         
	    });
	 }
	  
	 function managePlayerRequest(url){
	    var response = fetch(url, {
	           credentials: 'same-origin', 
	           method: 'GET', 
	           headers: new Headers({ 
	              'Content-Type': 'application/json'
	           }),
	        })
	        .then(response => response.json())
	        .then(json=>{
	        	player.bingoCards = json.bingoCards;
	            fillInUserCards(player);
	            connect (game); 
	        })	    
	        .catch(function(error) {
	           console.log(FETCH_ERROR_MESSAGE + error.message);
	        });
	 }
	 
	 
     var gameId = $('#gameId').data('myval');
     var gameName = $('#gameName').data('myval');
     var role = "Admin"

     var playerId = $('#playerId').data('myval');;
     var playerName = $('#playerName').data('myval');;
     console.log ("PlayerId: "+playerId+ " Player Name: "+playerName);

	 game = new BingoGame(gameId,gameName,undefined,INITIAL_STATE);

     player = new Player(playerId,playerName);
	 	 
     managePlayerRequest(PLAYER_DATA_REQUEST_PATH+playerId);
	 		 	 	 	 
})