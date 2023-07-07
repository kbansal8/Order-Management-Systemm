package com.service.ordermanagement;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		OrderManagementSystem orderManagementSystem = new OrderManagementSystem(JDBC.connection());

		// Add customers to the queue
		orderManagementSystem.addCustomerToQueue(new Customer("John", Arrays.asList("item1", "item2", "item3")));
		orderManagementSystem.addCustomerToQueue(new Customer("Alice", Arrays.asList("item1", "item2")));
		orderManagementSystem.addCustomerToQueue(new Customer("Bob", Arrays.asList("item4", "item5")));
		orderManagementSystem.addCustomerToQueue(new Customer("Eva", Arrays.asList("item1")));
		

		// Prioritize orders and fetch item prices from the database
		orderManagementSystem.prioritizeOrders();

		// Serve customers
		orderManagementSystem.serveCustomers();
	}
}
