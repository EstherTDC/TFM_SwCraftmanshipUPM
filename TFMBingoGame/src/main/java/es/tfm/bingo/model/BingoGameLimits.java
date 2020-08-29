package es.tfm.bingo.model;

public abstract class BingoGameLimits {
		
	protected int minRow;
	protected int maxRow;
	
	protected int minColumn;
	protected int maxColumn;

    protected int minNumber;
    protected int maxNumber;
    
    protected int numbersInRange;
    
    protected BingoGameLimits(){}
    
    
    public int getMinRow() {
    	return minRow;
    }
	
    public int getMaxRow() {
    	return maxRow;
    }
    
    public int getMinColumn() {
    	return minColumn;
    }
    
    public int getMaxColumn() {
    	return maxColumn;
    }
    
    public int getMinNumber() {
    	return minNumber;
    }
    
    public int getMaxNumber() {
    	return maxNumber;
    }
    
    public int getNumbersInRange() {
    	return numbersInRange;
    }
}
