  $(document).ready(function () {

	 const START_BUTTON = "startButton";
	 const STOP_BUTTON = "stopButton";
	 
	 const JQ_START_BUTTON = "#"+START_BUTTON;
	 const JQ_STOP_BUTTON = "#"+STOP_BUTTON;
     
	 function initiateAdminStartAndStopButtons(){   
	     var containersArea = document.getElementById("frame");
		 $(containersArea).append('<div id="adminButtonsArea" class="bingo-container mx-auto" align="center">'
	                                    +'<button id="'+START_BUTTON+'"  class="btn btn-success">Empezar/Reanudar partida</button>'
	                                    +'<button id="'+STOP_BUTTON+'" class="btn btn-warning">Parar partida</button>'
	                               +'</div>');
		 	 
 	     $(JQ_START_BUTTON).show();
 	     $(JQ_STOP_BUTTON).hide();

		 
	     $(JQ_START_BUTTON).click(function(){ 
	    	    startGame(game);

	    	    $(JQ_START_BUTTON).hide();
	    	    $(JQ_STOP_BUTTON).show();
	     });
	     $(JQ_STOP_BUTTON).click(function(){ 
	    	    stopGame(game);

	    	    $(JQ_START_BUTTON).show();
	    	    $(JQ_STOP_BUTTON).hide();
	     });
	 }
	     
	 
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

		               if (game.status == IN_GAME_RUNNING_STATE){
		        		    setTimeout(function () {
		        		    	manageNewBallRequest(game);
		        		    }, 1000);
		        	   }
	        	    }
	            	else {

	 		           markPreviousNumberInBoard(newNumber);
                	   disconnect();
	            	   manageGameRemoval(GAME_REMOVAL_PATH,game.id);
	            	}
	            }
	        });
		    	        

	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_START+game.id, function (message) {
	     	    game.status = IN_GAME_RUNNING_STATE;

	     	    var messageToShow = JSON.parse(message.body);

	        	console.log("Start Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,50,6);
	        	
	   	        manageNewBallRequest(game);

	        });
	        
	        
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_STOP+game.id, function (message) {
	   		    game.status = IN_GAME_STOPPED_STATE;

	   		    var messageToShow = JSON.parse(message.body);
	        	        	
	        	console.log("Stop Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,45,6);
	        });
	        
		    
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_MINOR_PRIZE+game.id, function (message) {
	        	
	        	minorPrizeAlreadyClaimed = true;
	        	
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Minor Prize Claimed Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,15,6);
	        });
	        
	        
	        stompClient.subscribe(SUBSCRIPTION_TO_NOTIFICATIONS_BINGO+game.id, function (message) {
	        	
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Bingo Claimed Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,10000,30,6);
	        	
            	disconnect();
         	    manageGameRemoval(GAME_REMOVAL_PATH,game.id);
	        });
	        
	        
	        stompClient.subscribe(PERSONAL_NOTIFICATIONS, function (message) {
	   		    var messageToShow = JSON.parse(message.body);
        	
	        	console.log("Personal Notification Message: "+messageToShow.message);
	        	tempAlert(messageToShow.message,2000,50,90);
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
	 
	 
	 function manageGameRemoval(url,gameId){

	    var response = fetch(url+gameId, {
		       credentials: 'same-origin', 
		       method: 'DELETE', 
		       headers: new Headers({ 
		          'Content-Type': 'application/json'
		       }),
		    })
		    .then(response => response.json())
		    .then(json=>{
 
		    })	    
		    .catch(function(error) {
		       console.log(FETCH_ERROR_MESSAGE + error.message);
		    });
     }
	 
	 
	 function startGame(game){

		 stompClient.send('/app/startGame',{},JSON.stringify({'gameId': game.id}));
  	     game.status = IN_GAME_RUNNING_STATE;
	 }
	 
	 function stopGame(game){

		 stompClient.send('/app/stopGame',{},JSON.stringify({'gameId': game.id}));
  	     game.status = IN_GAME_STOPPED_STATE;
	 }
	 
	 function manageNewBallRequest(game){
         stompClient.send('/app/getBall',{},JSON.stringify({'gameId': game.id}));
	 }
	 
	 
     var gameId = $('#gameId').data('myval');
     var gameName = $('#gameName').data('myval');
     var role = "Admin";

     var playerId = $('#playerId').data('myval');
     var playerName = $('#playerName').data('myval');
     console.log ("PlayerId: "+playerId+ " Player Name: "+playerName);

	 game = new BingoGame(gameId,gameName,undefined,"INITIAL");

     player = new Player(playerId,playerName);
	 
	 initiateAdminStartAndStopButtons();
	 
     managePlayerRequest(PLAYER_DATA_REQUEST_PATH+playerId);	 	 	 	 
})