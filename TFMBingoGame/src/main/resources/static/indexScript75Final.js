     const CARD_ROWS = 5;
     const CARD_COLUMNS = 5;
     const BOARD_ROWS = 5;
     const BOARD_COLUMNS = 15;
     const NUMBERS_PER_CARD = 24;

     const CARDS_PER_USER = 2;
	 const CELL_IN_BOARD_CLASS = "rTableCell75 ballBackground completeBlueBall mx-auto";

	 const COLUMN_BUTTON_CLASS = "btn btn-info";
	 const DIAGONAL_BUTTON_CLASS = "btn btn-info";
	 
	 const COLUMN_EVENT = "COLUMN";
	 const DIAGONAL_EVENT = "DIAGONAL";
	 
	 const COLUMN_BUTTON = "columnButton";
	 const DIAGONAL_BUTTON = "diagonalButton";
	 
	 const JQ_COLUMN_BUTTON = "#"+COLUMN_BUTTON;
	 const JQ_DIAGONAL_BUTTON = "#"+DIAGONAL_BUTTON;
	 
	 const CONTROL_BOARD = "controlBoard";
	
		 
	 
	 function inititateCard(cardNumber){
	     $(JQ_USER_CARDS_AREA).append('<div class="'+EXTERNAL_FRAME_CLASS+'">'+                  
	    	                             '<div id="'+USER_CARD+cardNumber + '" class="'+INTERNAL_FRAME_CLASS+'"></div>'+
	    	                             '<span class="'+CELL_CLASS+'">'+
	    	                                 '<button id="'+LINE_BUTTON+cardNumber + '" class="'+LINE_BUTTON_CLASS+'" display="block">Línea</button>'+
	    	                             '</span>'+
	    	                             '<span class="'+CELL_CLASS+'">'+
    	                                     '<button id="'+COLUMN_BUTTON+cardNumber + '" class="'+COLUMN_BUTTON_CLASS+'" display="block">Columna</button>'+
    	                                 '</span>'+
	    	                             '<span class="'+CELL_CLASS+'">'+
    	                                     '<button id="'+DIAGONAL_BUTTON+cardNumber + '" class="'+DIAGONAL_BUTTON_CLASS+'" display="block">Diagonal</button>'+
    	                                 '</span>'+
	    		                         '<span class="'+CELL_CLASS+'"">'+
	    		                             '<button id="'+BINGO_BUTTON+cardNumber + '" class="'+BINGO_BUTTON_CLASS+'" display="block">Bingo</button>'+
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
	     $(JQ_COLUMN_BUTTON+cardNumber).prop('disabled',true);
	     $(JQ_DIAGONAL_BUTTON+cardNumber).prop('disabled',true);
	              
	     $(JQ_LINE_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,LINE_EVENT,cardNumber);});
	     $(JQ_BINGO_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,BINGO_EVENT,cardNumber);});
	     $(JQ_COLUMN_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,COLUMN_EVENT,cardNumber);});
	     $(JQ_DIAGONAL_BUTTON+cardNumber).click(function(){ sendEventClaim(game.id,player.id,DIAGONAL_EVENT,cardNumber);});
	 }
     
	 
 	 function checkIfAnyColumnMarked(cardNumber){
		 
		 for (var i = 1; i <= CARD_COLUMNS; i++){
		   var markedCellsInColumn = 0;

	       for (var j = 1 ; j <= CARD_ROWS ; j++){
		         var cell = document.getElementById(CARDS_CELL+cardNumber+""+j+""+i);
		         if (cell.innerText!=0){
		        	 if (cell.classList.contains(MARKED_CLASS))
		        		 markedCellsInColumn++;
		         }
		         else 
	        		 markedCellsInColumn++;
	       }

	       if (markedCellsInColumn==CARD_ROWS)
	    	   return true;
		 }
		 
		 return false;
	 }
	 
	 
	 function checkIfAnyDiagonalMarked(cardNumber){
		 
		 for (var i = 1; i <= CARD_COLUMNS; i++){
		   var markedCellsInDiagonalLeftToRight = 0;
		   var markedCellsInDiagonalRightToLeft = 0;
		   
	       var cell = document.getElementById(CARDS_CELL+cardNumber+""+i+""+i);
	       
	       if (cell.innerText!=0)
	       	 if (cell.classList.contains(MARKED_CLASS))
	       		markedCellsInDiagonalLeftToRight++;

	       var cell = document.getElementById(CARDS_CELL+cardNumber+""+(CARD_ROWS-i+1)+""+i);
	       
	       if (cell.innerText!=0)
	       	 if (cell.classList.contains(MARKED_CLASS))
	       		markedCellsInDiagonalRightToLeft++;
		 }
	     
		 if ((markedCellsInDiagonalLeftToRight==CARD_ROWS-1) ||
		     (markedCellsInDiagonalRightToLeft==CARD_ROWS-1))
		  	   return true;
		 
		 return false;
	 }
	 
	 
	 function checkEventButtonsActive(cardNumber){
		 
		 var markedLines = getNumberOfMarkedRows(cardNumber);

		 if (!minorPrizeAlreadyClaimed){
			 var isMarkedAnyColumn = checkIfAnyColumnMarked(cardNumber);
			 var isMarkedAnyDiagonal = checkIfAnyDiagonalMarked(cardNumber);


		     var lineDisabled = (markedLines>0)?
				 $(JQ_LINE_BUTTON+cardNumber).prop('disabled',false):$(JQ_LINE_BUTTON+cardNumber).prop('disabled',true);	 
				 
			 var columnDisabled = (isMarkedAnyColumn)?
			     $(JQ_COLUMN_BUTTON+cardNumber).prop('disabled',false):$(JQ_COLUMN_BUTTON+cardNumber).prop('disabled',true);	 

		     var diagonalDisabled = (isMarkedAnyDiagonal)?
			     $(JQ_DIAGONAL_BUTTON+cardNumber).prop('disabled',false):$(JQ_DIAGONAL_BUTTON+cardNumber).prop('disabled',true);	 
		 }
		 else{ 
			 $(JQ_LINE_BUTTON+cardNumber).prop('disabled',true);
			 $(JQ_COLUMN_BUTTON+cardNumber).prop('disabled',true);
			 $(JQ_DIAGONAL_BUTTON+cardNumber).prop('disabled',true);
		 }
		 
		 var bingoDisabled = (markedLines == CARD_ROWS)?
				 $(JQ_BINGO_BUTTON+cardNumber).prop('disabled',false):$(JQ_BINGO_BUTTON+cardNumber).prop('disabled',true);	 
	 }