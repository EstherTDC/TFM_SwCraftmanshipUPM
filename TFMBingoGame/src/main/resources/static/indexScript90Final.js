     const CARD_ROWS = 3;
     const CARD_COLUMNS = 9;
     const BOARD_ROWS = 9;
     const BOARD_COLUMNS = 10;
     const NUMBERS_PER_CARD = 15;
     
     const CARDS_PER_USER = 3;
	 const CELL_IN_BOARD_CLASS = "rTableCell ballBackground completeBlueBall mx-auto";

	 
	 function inititateCard(cardNumber){
	     $(JQ_USER_CARDS_AREA).append('<div class="'+EXTERNAL_FRAME_CLASS+'">'+                  
	    	                             '<div id="'+USER_CARD+cardNumber + '" class="'+INTERNAL_FRAME_CLASS+'"></div>'+
	    	                             '<span class="'+CELL_CLASS+'">'+
	    	                                 '<button id="'+LINE_BUTTON+cardNumber + '" class="'+LINE_BUTTON_CLASS+'">Línea</button>'+
	    	                             '</span>'+
	    		                         '<span class="'+CELL_CLASS+'">'+
	    		                             '<button id="'+BINGO_BUTTON+cardNumber + '" class="'+BINGO_BUTTON_CLASS+'">Bingo</button>'+
	    		                         '</span>'+
		                              '</div>');
	     
	     initiateRowsInUserCard(cardNumber);     
	     initiateClaimedPrizeButtonsForCard(cardNumber);
	 }
	 
	 
	 function initiateRowsInUserCard(cardNumber){
	     for (var i = 0 ; i < CARD_ROWS ; i++){
	 	        $(JQ_USER_CARD+cardNumber).append('<div id="'+ROW_IN_CARD+cardNumber+""+i+'" class="'+ROW_CLASS+'">'+
	 	    		                              '</div>');
	     };
	 }
	 
	 function initiateClaimedPrizeButtonsForCard(cardNumber){
	     $(JQ_LINE_BUTTON+cardNumber).prop('disabled',true);
	     $(JQ_BINGO_BUTTON+cardNumber).prop('disabled',true);
	              
	     $(JQ_LINE_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,LINE_EVENT,cardNumber);});
	     $(JQ_BINGO_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,BINGO_EVENT,cardNumber);});
	 }
     	 
	 
	 function checkEventButtonsActive(cardNumber){
		 
		 var markedLines = getNumberOfMarkedRows(cardNumber);

		 if (!minorPrizeAlreadyClaimed){

		     var lineDisabled = (markedLines>0)?
				 $(JQ_LINE_BUTTON+cardNumber).prop('disabled',false):$(JQ_LINE_BUTTON+cardNumber).prop('disabled',true);	 			 
		 }
		 else{ 
			 $(JQ_LINE_BUTTON+cardNumber).prop('disabled',true);
		 }
		 
		 var bingoDisabled = (markedLines == CARD_ROWS)?
				 $(JQ_BINGO_BUTTON+cardNumber).prop('disabled',false):$(JQ_BINGO_BUTTON+cardNumber).prop('disabled',true);	 
	 }