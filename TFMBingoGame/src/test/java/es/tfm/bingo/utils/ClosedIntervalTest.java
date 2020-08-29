package es.tfm.bingo.utils;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClosedIntervalTest {
	
	@RunWith(Parameterized.class)
	public static class TestIncludesMethod{
	
    	@Parameters(name = "{index}: Given an interval with limits [{0},{1}] when it is checked if {2} included returns {3}")
        public static Collection<Object[]> data() {
            Object[][] values = {
                {1, 75, 1,true},
                {1, 75, 75,true},
                {1, 75, 76,false},
                {1, 75, 30,true},
                {1, 75, 0,false},       
                {1, 90, 1,true},
                {1, 90, 90,true},
                {1, 90, 91,false},
                {1, 90, 2,true},
                {1, 90, 0,false},       
            };
    
            return Arrays.asList(values);
        } 
    
      	@Parameter(0) public int minLimit;
       	@Parameter(1) public int maxLimit;
       	@Parameter(2) public int numberToCheck;
	    @Parameter(3) public boolean result;
	
	    @Test 
	    public void GivenADefinedInterval_ThenCheckIncludesMethod() {
           //Given
		   ClosedInterval closedInterval = new ClosedInterval(minLimit,maxLimit);
	    
           // When
		   closedInterval.includes(numberToCheck);
		
	       //Then
		   assertTrue(closedInterval.includes(numberToCheck)==result);
	    }
	}
	
	@RunWith(Parameterized.class)
	public static class TestGenerateXNumbersInInterval{
	
    	@Parameters(name = "{index}: Given an interval with limits [{0},{1}] when {2} numbers in limit are requested a list of {2} numbers in limit is returned")
        public static Collection<Object[]> data() {
            Object[][] values = {
                {1, 15, 4},
                {1, 75, 10},
                {1, 40, 1},
                {1, 75, 0},       
                {1, 90, 1},
                {1, 1, 1},
                {90, 90, 1},
                {1, 90, 2},
                {1, 2, 2}
            };
    
            return Arrays.asList(values);
        } 
    
      	@Parameter(0) public int minLimit;
       	@Parameter(1) public int maxLimit;
       	@Parameter(2) public int requestedNumbers;
	
	    @Test 
	    public void GivenADefinedInterval_WhenGenerateXNumbersInInterval_ThenCheckThatNumbersBelongToInterval() {
           //Given
		   ClosedInterval closedInterval = new ClosedInterval(minLimit,maxLimit);
	    
           // When
		   List<Integer> generatedNumbers = closedInterval.generateXNumbersInInterval(requestedNumbers);
		
	       //Then
		   assertTrue(generatedNumbers.size()==requestedNumbers);
		   for (int i = 0; i<requestedNumbers; i++)
//			   assertTrue(closedInterval.includes(generatedNumbers.get(i)));
		       assertThat(closedInterval.includes(generatedNumbers.get(i))).isEqualTo(true);

	    }
	}
}
