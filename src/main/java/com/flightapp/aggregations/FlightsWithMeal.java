package com.flightapp.aggregations;

public class FlightsWithMeal {
    
	// to get flights with meal available
	private boolean mealAvailable;
    private long count;

    public boolean isMealAvailable() { 
    	return mealAvailable; 
    }
    public void setMealAvailable(boolean mealAvailable) { 
    	this.mealAvailable = mealAvailable; 
    }

    public long getCount() { 
    	return count; 
    }
    public void setCount(long count) { 
    	this.count = count; 
    }
}
