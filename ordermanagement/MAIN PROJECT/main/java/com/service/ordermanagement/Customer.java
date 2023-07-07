package com.service.ordermanagement;

import java.sql.*;
import java.util.*;

class Customer {
    private String name;
    private List<String> order;

    public Customer(String name, List<String> order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> updatedOrder) {
        this.order = updatedOrder;
    }

}

class OrderManagementSystem {
    private Queue<Customer> queue;
    private Connection connection;

    public OrderManagementSystem(Connection connection) {
        queue = new LinkedList<>();
        this.connection = connection;
    }

    public void addCustomerToQueue(Customer customer) {
        queue.add(customer);
    }

    public void serveCustomers() {
        List<Customer> sortedQueue = new ArrayList<>(queue);
        sortedQueue.sort(Comparator.comparingInt(this::getOrderPriority).thenComparingLong(this::getOrderArrivalTime));

        for (Customer customer : sortedQueue) {
            System.out.println("Serving customer: " + customer.getName() + ", Order: " + customer.getOrder());
        }
    }




    public void prioritizeOrders() {
        try {
            fetchItemPrices();
            // Fetches the item prices from the "emp2" table
        } catch (SQLException e) {
            System.out.println("Failed to fetch item prices: " + e.getMessage());
        } finally {
            disconnectFromDatabase(); // Disconnect from the MySQL database
        }

        PriorityQueue<Customer> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(this::getOrderPriority).thenComparingLong(this::getOrderArrivalTime));
        priorityQueue.addAll(queue);

        List<Customer> sortedQueue = new ArrayList<>(priorityQueue);  // Create a new sorted list
        queue.clear();
        queue.addAll(sortedQueue);  // Assign the sorted list back to the queue
    }


    private void fetchItemPrices() throws SQLException {
        // Fetches the item prices from the "emp2" table in the database
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT item_name, price FROM emp2");

        List<ItemPrice> itemPrices = new ArrayList<>();
        while (resultSet.next()) {
            String itemName = resultSet.getString("item_name");
            int price = resultSet.getInt("price");
            itemPrices.add(new ItemPrice(itemName, price));
        }

        // Assigns the fetched item prices to the corresponding items in the orders
        for (Customer customer : queue) {
            List<String> order = customer.getOrder();
            List<String> updatedOrder = new ArrayList<>();
            for (String item : order) {
                int price = getItemPrice(item, itemPrices);
                updatedOrder.add(item + " (Price: " + price + " rupees)");
            }
            customer.setOrder(updatedOrder);
        }
    }

    private int getOrderPriority(Customer customer) {
        List<String> order = customer.getOrder();
        int totalValue = 0;
        for (String item : order) {
            totalValue += getItemPriceFromOrder(item, customer);
        }

        if (totalValue <= 20) {
            return 0; // Higher priority for orders below or equal to 20 rupees
        } else {
            return 1; // Lower priority for other orders
        }
    }


    private long getOrderArrivalTime(Customer customer) {
        return System.nanoTime(); // Return the current system time as the arrival time for simplicity
    }

    private int getItemPrice(String item, List<ItemPrice> itemPrices) {
        for (ItemPrice itemPrice : itemPrices) {
            if (itemPrice.getItemName().equals(item)) {
                return itemPrice.getPrice();
            }
        }
        return 0; // Item price not found, return default value
    }

    private int getItemPriceFromOrder(String item, Customer customer) {
        List<String> order = customer.getOrder();
        int totalValue = 0;

        for (String orderedItem : order) {
            String[] parts = orderedItem.split("\\(Price: ");
            if (parts.length > 1) {
                String itemName = parts[0].trim();
                String priceString = parts[1].substring(0, parts[1].indexOf(" rupees")).trim();

                    try {
                        totalValue = Integer.parseInt(priceString);
                    } catch (NumberFormatException e) {
                        // Handle invalid price format
                        break;
                    }
                }
            }
        return totalValue;
        }

         // Item price not found or invalid, return default value



    private void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close database connection: " + e.getMessage());
        }
    }
}

class ItemPrice {
    private String itemName;
    private int price;

    public ItemPrice(String itemName, int price) {
        this.itemName = itemName;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }
}