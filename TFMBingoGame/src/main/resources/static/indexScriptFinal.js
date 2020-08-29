
     var stompClient =null;
     var game;
     var player;
     
     var previousNumber = 0;
     var lastFourNumbers = [];
    
     var minorPrizeAlreadyClaimed = false;
	 
     const EMPTY_CELL = 0;
     const TEN = 10;
     const FOUR_NUMBERS = 4;
     const LAST_NUMBER = -1;
     
     const INITIAL_STATE = 0;
     const IN_GAME_RUNNING_STATE = 1;
     const IN_GAME_STOPPED_STATE = 2;
        
	 const BALL_BACKGROUND_CLASS = " ballBackground";
	 const BALL_BASE_CLASS = " ballBackground mx-auto";
     const DISABLED_BUTTON_CLASS = "btn-disabled";
	 const EXTERNAL_FRAME_CLASS = "externalCardFrame rTableRow";
	 const INTERNAL_FRAME_CLASS = "internalCardFrame";
	 const IN_BOARD_CLASS = "in-board";
	 const IN_BOARD_NOT_MARKED_CLASS = "not-marked btn-circle in-board";
	 const IN_BOARD_MARKED_CLASS = "marked btn-circle in-board";
	 const LAST_NUMBERS_CELL_CLASS = "rTableCellLastNumbers";
	 const LINE_BUTTON_CLASS = "btn btn-info";
	 const BINGO_BUTTON_CLASS = "btn btn-success";
	 const NOT_MARKED_CELL_CLASS = "not-marked btn-circle";
     const MARKED_CLASS = "marked";
     const ROW_CLASS = "rTableRow";
     const CELL_CLASS = "rTableCell";
     const EMPTY_CELL_IN_CARD_CLASS = "rTableCellCardEmpty";
     const NON_EMPTY_CELL_IN_CARD_CLASS = "rTableCellCard";
	 
	 const BINGO_EVENT = "BINGO";
	 const LINE_EVENT = "LINE";
	 
	 const FETCH_ERROR_MESSAGE = "Hubo un problema con la petición Fetch:";
	 const NUMBER_NOT_IN_BOARD_MESSAGE = "Ese número no ha sido cantado todavía";
	 
	 const AUTOMATIC_MARKING ="automaticMarking";
	 const BINGO_BUTTON = "bingoButton";
	 const CARDS_CELL = "cardsCell";
	 const CELL_IN_BOARD = "cellInBoard";
	 const LAST_NUMBERS = "lastNumbers";
	 const LAST_NUMBERS_CELL ="lastNumbersCell";
	 const LINE_BUTTON = "lineButton";
	 const NEW_NUMBER = "newNumber";
	 const ROW_IN_BOARD = "rowInBoard";
	 const ROW_IN_CARD = "rowInCard";
	 const USER_CARD = "userCard";
	 
	 const JQ_AUTOMATIC_MARKING = "#"+AUTOMATIC_MARKING;
	 const JQ_CARDS_CELL = "#"+CARDS_CELL;
	 const JQ_NEW_NUMBER = "#"+NEW_NUMBER;
	 const JQ_LAST_NUMBERS = "#"+LAST_NUMBERS;
	 const JQ_USER_CARD = "#"+USER_CARD;
	 const JQ_CONTROL_BOARD = "#controlBoard";
	 const JQ_USER_CARDS_AREA = "#userCardsArea";
	 const JQ_LINE_BUTTON = "#"+LINE_BUTTON;
	 const JQ_BINGO_BUTTON = "#"+BINGO_BUTTON; 
	 
	 const GAME_REMOVAL_PATH = '/gamemgt/deleteGame/';

	 const PLAYER_DATA_REQUEST_PATH = '/onlineBingo/getPlayer/';

	 
	 const BINGO_WEB_SOCKET = 'http://127.0.0.1:8080/tfm-bingo-websocket';
	 const PERSONAL_NOTIFICATIONS = '/user/queue/reply';

	 const BINGO_GAME_SUBSCRIPTION ='/topic/';
	 const SUBSCRIPTION_TO_NOTIFICATIONS_START = '/topic/start';
	 const SUBSCRIPTION_TO_NOTIFICATIONS_STOP = '/topic/stop';
	 const SUBSCRIPTION_TO_NOTIFICATIONS_MINOR_PRIZE = '/topic/notifMinorPrize';
	 const SUBSCRIPTION_TO_NOTIFICATIONS_BINGO = '/topic/notifBingo';
     
     var numbersInCards = [new Map(),new Map(),new Map()];
            
     
	 function BingoGame(id,gameName,status){
		 this.id=id;
		 this.gameName = gameName;
		 this.status = status;
		 this.balls = [];
     } 
	 		
	 
     function BingoCard(id,gameName,squares){
		 this.id = id;
		 this.squares = [];
	 }
     
     
     function Square(id,coordX,coordY,number){
		 this.coordX = coordX;
		 this.coordY = coordY;
		 this.number = number;
     }
     
		 
     function Player(id,playerName){
		 this.id = id;
		 this.playerName = playerName;
		 this.bingoCards = [];
	 }
		 
     
     function EventMessage(gameId,playerId,eventType,bingoCard){
    	 this.gameId = gameId; 
    	 this.playerId = playerId;
    	 this.eventType = eventType;
    	 this.bingoCard = bingoCard;
     }
     
     
	 function initiateControlBoard(){
		 for (var rowId = 0; rowId < BOARD_ROWS ; rowId++){
		    initiateRowInControlBoard(rowId);
		    initiateCellsInControlBoard(rowId);
	 	 };		 
	 }
	 
	 
	 function initiateRowInControlBoard(rowId){
	 	 $(JQ_CONTROL_BOARD).append('<div id="'+ROW_IN_BOARD+rowId+'" class="'+ROW_CLASS+'"></div>');
	 }
	 
	 
	 function initiateCellsInControlBoard(rowId){
		 var rowElem = document.getElementById(ROW_IN_BOARD+rowId);

		 for (var columnId = 0 ; columnId < BOARD_COLUMNS ; columnId++){
		 	 var number = columnId+(rowId*BOARD_COLUMNS)+1;
		 	 $(rowElem).append('<div id="'+CELL_IN_BOARD+number+'" class="'+CELL_IN_BOARD_CLASS+'">'+
		 			               number+
		 			           '</div>');
		 };
	 }
	 
	 
	 function fillInCard(player,cardNumber){
	     var counter = 0;
 	     for (var i = 0 ; i < CARD_ROWS ; i++){
            var rowCards = document.getElementById(ROW_IN_CARD+cardNumber+""+i);
 		 
  	        for (var j = 0 ; j < CARD_COLUMNS ; j++){
	           var number = player.bingoCards[cardNumber].squares[counter].number;
	         
	           var cellName = CARDS_CELL+cardNumber+""+player.bingoCards[cardNumber].squares[counter].coordX
	                                               +""+player.bingoCards[cardNumber].squares[counter].coordY;

	  		   var buttonInCellClass = (number!=EMPTY_CELL)?NOT_MARKED_CELL_CLASS:DISABLED_BUTTON_CLASS;
	  		   var cellClass = (number!=EMPTY_CELL)?NON_EMPTY_CELL_IN_CARD_CLASS:EMPTY_CELL_IN_CARD_CLASS;

	  		   $(rowCards).append('<div class="'+cellClass+'">'+
	         	                     '<button id='+cellName+' class="'+buttonInCellClass+'">'+number+
	         	                     '</button>'+
	                              '</div>');
	  		 
	  	  	   if (number != 0) 
	  			  fillInNumberAtPosition (cardNumber,number,cellName);
		     
	  		   counter++;
	       };
 	    };
	 }
	 
	 
	 function activateListenerForCellsInCard(cardNumber){
 	     for (var i = 1 ; i <= CARD_ROWS ; i++)
   	        for (var j = 1 ; j <= CARD_COLUMNS ; j++)
  		       $(JQ_CARDS_CELL+cardNumber+""+i+""+j).click(function(){
  		     	  var automaticMarkingElem = document.getElementById(AUTOMATIC_MARKING);
  		    	  if (automaticMarkingElem.checked==false){
  		    	     changeClassOfCellInCard(this);
  		    	     checkEventButtonsActive(cardNumber);
  		    	  }
  		       });
	 }
	  
	 
	 function fillInUserCards(player){
		 	 
		for (var card = 0; card < CARDS_PER_USER; card++){
		   inititateCard(card);
		   fillInCard(player,card);
		   activateListenerForCellsInCard(card);
		};
	 }	
	 
	 
	 function fillInNumberAtPosition(cardNumber,number,cellName){
    	 numbersInCards[cardNumber].set(number,cellName);
	 }
	 
	 
	 function automaticCardsFillIn(player,number){
		 var automaticMarkingElem = document.getElementById(AUTOMATIC_MARKING);
		 for (var card = 0; card< player.bingoCards.length ; card++){
			markNumberInCardForAutomaticMarking(card,number);
		    checkEventButtonsActive(card);
		 }
	 }
	 
	 
	 function markNumberInCardForAutomaticMarking(card,number){
		 var automaticMarkingElem = document.getElementById(AUTOMATIC_MARKING);

		 var cellName = numbersInCards[card].get(number);
			
		 if (cellName != undefined){
		 	var cell = document.getElementById(cellName);
			 
		 	cell.className = (automaticMarkingElem.checked==true)?
		 	  	               IN_BOARD_MARKED_CLASS:IN_BOARD_NOT_MARKED_CLASS;
		 }
	 }
	 
	 
	 function changeClassBall(ballId,baseClass,number)
	 {
		 var updatedBall= document.getElementById(ballId);
		 		 		
		 for (var i = BOARD_COLUMNS; i<=(BOARD_ROWS*BOARD_COLUMNS); i+=BOARD_COLUMNS){
		    if (number <= i){
		       updatedBall.className = baseClass + BALL_BASE_CLASS + BALL_BACKGROUND_CLASS+(i/BOARD_COLUMNS);
			   break;
		    }
		 }
	 }
	 
	 
	 function changeClassOfCellInCard(cell)
	 {
		 if (cell.classList.contains(IN_BOARD_CLASS))
	 	    cell.className=(cell.className==IN_BOARD_NOT_MARKED_CLASS)?
	 	    		IN_BOARD_MARKED_CLASS:IN_BOARD_NOT_MARKED_CLASS;
		 else
	        	tempAlert(NUMBER_NOT_IN_BOARD_MESSAGE,2000,50,2);
	 }
	 	 
	 
	 function getNumberOfMarkedRows(cardNumber){
		 var markedLines = 0;
		 
		 for (var i = 1; i <= CARD_ROWS; i++){
		   var markedCellsInLine = 0;

	       for (var j = 1 ; j <= CARD_COLUMNS ; j++){
		         var cell = document.getElementById(CARDS_CELL+cardNumber+""+i+""+j);
		         if (cell.innerText!=0){
		        	 if (cell.classList.contains(MARKED_CLASS))
					     markedCellsInLine++;
		         }
		         else 
				     markedCellsInLine++;
	       }
	       if (markedCellsInLine==CARD_COLUMNS)
	    	   markedLines++;
		 }
		 
		 return markedLines;
	 }
	 
	 
	 $(JQ_AUTOMATIC_MARKING).click(function(){
		 var automaticMarkingElem = document.getElementById(AUTOMATIC_MARKING);

		 if (automaticMarkingElem.checked==true)
		    for (var card=0; card<player.bingoCards.length; card++)
		    	automaticMarkingInCard(card);
	 })
	 
	 
	 function automaticMarkingInCard(cardNumber){

		 for (var i=1; i<=CARD_ROWS; i++)
		    for (var j=1; j<= CARD_COLUMNS; j++){
			   var cell = document.getElementById(CARDS_CELL+cardNumber+""+i+""+j);
			         
			   if (cell.classList.contains(IN_BOARD_CLASS))
			      cell.className = IN_BOARD_MARKED_CLASS;
		    }
	 }
	  
	 
     function tempAlert(msg,duration,x,y){
         var element = document.createElement("div");
         element.setAttribute("style","position:absolute;top:"+y+"%;left:"+x+"%;"+
        		                      "background-color:white;"+
        		                      "font-size:30px;color:black;text-transform: uppercase;");
         element.innerHTML = msg;
         setTimeout(function(){
             element.parentNode.removeChild(element);
         },duration);
         document.body.appendChild(element);
     }

	 
	 function removeContent(elementId){
	    var a=document.getElementById(elementId);
		while(a.hasChildNodes()){
		  	a.removeChild(a.firstChild);	
		};
	 }
	 
	 
	 function showNewNumber(newNumber) {
		changeClassBall(NEW_NUMBER,"",newNumber);
		removeContent(NEW_NUMBER);
	    $(JQ_NEW_NUMBER).append(newNumber);
	 }
	 
	 
	 function markPreviousNumberInBoard(newNumber) {
		if (previousNumber != 0)
	        changeClassBall(CELL_IN_BOARD+previousNumber,CELL_CLASS,previousNumber);

		if (newNumber != LAST_NUMBER)
		    previousNumber = newNumber;
	 }
	 
	 
	 function updateLastFourNumbers(){
		if (previousNumber!=0){
	       lastFourNumbers.unshift(previousNumber);
    	   if (lastFourNumbers.length>FOUR_NUMBERS)
	          lastFourNumbers.pop();
		 
   		   removeContent(LAST_NUMBERS);
	     	 	
		   for (var i = 0; i < lastFourNumbers.length ; i++){
		      $(JQ_LAST_NUMBERS).append('<div id="'+LAST_NUMBERS_CELL+i+'">'+lastFourNumbers[i]+'</div>');
   	 		  changeClassBall(LAST_NUMBERS_CELL+i,LAST_NUMBERS_CELL_CLASS,lastFourNumbers[i]);
	 	   };
		}
	 }
	  
	 
	 function sendEventClaim(gameId,playerId,event,cardNumber){

		 var card = player.bingoCards[cardNumber];
		 var eventMessage = new EventMessage(gameId,playerId,event,card);

		 var body = JSON.stringify(eventMessage);
		 stompClient.send('/app/userEvent/',{},body);
	 }
	 
	 function disconnect() {
		 if (stompClient !== null)
			   stompClient.disconnect();
			    
	        console.log("Disconnected");
	 }
		 

$(document).ready(function () {
     initiateControlBoard();    
})