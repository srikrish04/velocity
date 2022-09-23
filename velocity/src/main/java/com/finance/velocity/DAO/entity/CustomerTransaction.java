package com.finance.velocity.DAO.entity;

import java.util.HashMap;

import javafx.util.Pair;

public class CustomerTransaction {
	HashMap<String, Pair<Double, Integer>> dayMap;
	HashMap<String, Double> weekMap;
	
	
	public CustomerTransaction() {
		dayMap = new HashMap<>();
		weekMap = new HashMap<>();
		
	}
	
	public CustomerTransaction(HashMap<String, Pair<Double, Integer>> dayMap, HashMap<String, Double> weekMap) {
		super();
		this.dayMap = dayMap;
		this.weekMap = weekMap;
	}

	public HashMap<String, Pair<Double, Integer>> getDayMap() {
		return dayMap;
	}

	public void setDayMap(HashMap<String, Pair<Double, Integer>> dayMap) {
		this.dayMap = dayMap;
	}

	public HashMap<String, Double> getWeekMap() {
		return weekMap;
	}

	public void setWeekMap(HashMap<String, Double> weekMap) {
		this.weekMap = weekMap;
	}
	
}
