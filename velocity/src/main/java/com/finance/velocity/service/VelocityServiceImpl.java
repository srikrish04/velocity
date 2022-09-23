package com.finance.velocity.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.finance.velocity.DAO.entity.CustomerTransaction;
import com.finance.velocity.DAO.entity.Transaction;
import com.finance.velocity.repr.OutputRepr;
import com.finance.velocity.repr.TransactionRepr;

import javafx.util.Pair;

@Service
public class VelocityServiceImpl implements VelocityService {
	final int MAX_PER_DAY = 5000;
	final int MAX_PER_WEEK = 20000;
	HashMap<String, CustomerTransaction> transactions = new HashMap<>();
	Set<String> transIdSet = new HashSet<>();

	/**
	 * @param fileName - takes input fileName as parameter
	 * Reads the file line by line; parses string load line as JSON object and pass transaction for handling
	 */
	@Override
	public void processTransaction(String fileName) {
		Scanner myReader = null;

		try {
			File myObj = new File(fileName);
			myReader = new Scanner(myObj);

			String input = null;
			while (myReader.hasNext()) {
				input = myReader.next();
				ObjectMapper objectMapper = new ObjectMapper();
				TransactionRepr transRepr = objectMapper.readValue(input, TransactionRepr.class);

				Double loadAmt = 0.0;
				NumberFormat format = NumberFormat.getCurrencyInstance();
				Number number = format.parse(transRepr.load_amount);
				loadAmt = number.doubleValue();

				Instant loadTime = Instant.parse(transRepr.time);

				Transaction trans = new Transaction(transRepr.id, transRepr.customer_id, loadAmt, loadTime);

				StringBuilder custLoadID = new StringBuilder();
				custLoadID.append(trans.getCustomerId());
				custLoadID.append("#" + trans.getId());

				// checks if the customer_id # load id combination has been already processed
				if (!transIdSet.contains(custLoadID.toString())) {
					String transactionStatus = handleTransaction(trans);
					transIdSet.add(custLoadID.toString());
					writeTransactionLoadStatus(transactionStatus);

				}

			}
		} catch (FileNotFoundException ex) {
			System.err.println("Processing Transaction stopped with FileNotFoundException");// TODO:// Handling
																							// FileNotFoundException
			ex.printStackTrace();
		} catch (JsonMappingException ex) {
			System.err.println("Processing Transaction stopped with JsonMappingException");// TODO:// Handling
																							// JsonMappingException
			ex.printStackTrace();
		} catch (JsonProcessingException ex) {
			System.err.println("Processing Transaction stopped with JsonProcessingException");// TODO:// Handling
																								// JsonProcessingException
			ex.printStackTrace();
		} catch (ParseException ex) {
			System.err.println("Processing Transaction stopped with ParseException");// TODO:// Handling ParseException
			ex.printStackTrace();
		} finally {
			myReader.close();
		}
	}

	/**
	 * @param trans
	 * @return String
	 * @throws JsonProcessingException
	 */
	private String handleTransaction(Transaction trans) throws JsonProcessingException {
		Instant time = trans.getTime();
		ZonedDateTime zdt = time.atZone(ZoneOffset.UTC);

		StringBuilder keyBuilder = new StringBuilder();

		int year = zdt.getYear();
		int dayOfYear = zdt.getDayOfYear();

		keyBuilder.append(dayOfYear);
		keyBuilder.append(year);
		String dayKey = keyBuilder.toString();// generates hashMap key for validating per day load not greater than 5000

		keyBuilder = new StringBuilder();
		int week = zdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		int weekYear = zdt.get(IsoFields.WEEK_BASED_YEAR);

		keyBuilder.append(week);
		keyBuilder.append(weekYear);
		String weekKey = keyBuilder.toString();// generates hashMap key for validating per week load not greater than 20000

		OutputRepr output = new OutputRepr(trans.getId(), trans.getCustomerId());

		// checks this user transaction is valid and if valid updates the CustomerTransaction object.
		if (updateUserTransaction(trans, dayKey, weekKey)) {
			output.setAccepted(true);
		}

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(output);

		return json;

	}
	
	/**
	 * Check if transaction is below 5000 per day & 20000 per week and if not rejects the transaction with false status
	 * @param trans
	 * @param dayKey
	 * @param weekKey
	 * @return
	 */
	private Boolean updateUserTransaction(Transaction trans, String dayKey, String weekKey) {

		if (trans.getLoadAmount() > MAX_PER_DAY)
			return false;

		CustomerTransaction custTrans = transactions.get(trans.getCustomerId());

		if (custTrans == null) {
			custTrans = new CustomerTransaction();
		}

		HashMap<String, Pair<Double, Integer>> dayMap = custTrans.getDayMap();
		Pair<Double, Integer> custDayTrans = null;
		// check if the customerID has already made a transaction for the given day and the given year
		if (dayMap.containsKey(dayKey)) {
			custDayTrans = dayMap.get(dayKey);
			if (custDayTrans.getKey() + trans.getLoadAmount() > MAX_PER_DAY)
				return false;
			if (custDayTrans.getValue() + 1 > 3)
				return false;
		}

		HashMap<String, Double> weekMap = custTrans.getWeekMap();
		// check if the customerID has already made a transaction for the given week and the given year
		if (weekMap.containsKey(weekKey)) {
			if (weekMap.get(weekKey) + trans.getLoadAmount() > MAX_PER_WEEK)
				return false;
		}

		if (custDayTrans == null) {
			custDayTrans = new Pair<>(trans.getLoadAmount(), 1);
		} else {
			custDayTrans = new Pair<>(custDayTrans.getKey() + trans.getLoadAmount(), custDayTrans.getValue() + 1);

		}
		dayMap.put(dayKey, custDayTrans);

		if (weekMap.containsKey(weekKey)) {
			weekMap.put(weekKey, weekMap.get(weekKey) + trans.getLoadAmount());
		} else {
			weekMap.put(weekKey, trans.getLoadAmount());
		}

		// Load request looks good, updating Customer account with the transaction load
		custTrans.setDayMap(dayMap);
		custTrans.setWeekMap(weekMap);
		transactions.put(trans.getCustomerId(), custTrans);

		return true;

	}

	private void writeTransactionLoadStatus(String transactionStatus) {
		//TODO: Currently printing load status on to standard output, 
		//this method later can be used to populate db or write to file
		System.out.println(transactionStatus + ",");
	}

}
