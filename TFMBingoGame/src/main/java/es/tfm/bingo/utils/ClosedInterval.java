package es.tfm.bingo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClosedInterval {

	private int min;
	private int max;

	public ClosedInterval(int min, int max) {
		assert min <= max;

		this.min = min;
		this.max = max;
	}

	int getMin() {
		return this.min;
	}

	int getMax() {
		return this.max;
	}
	
	public boolean includes(int value) {
		return this.min <= value && value <= this.max;
	}
	
	public List<Integer> generateXNumbersInInterval (Integer xNumbers){
		
		assert max-(min-1)-xNumbers>=0;
			
		List<Integer> listOfNumbers=new ArrayList<Integer>();
		
		for (int i = min; i<=max; i++)
			listOfNumbers.add(i);
		
		Collections.shuffle(listOfNumbers);
		    	 	
		Collections.sort(listOfNumbers.subList(0, xNumbers));
			
		return listOfNumbers.subList(0, xNumbers);
	}
	
}
