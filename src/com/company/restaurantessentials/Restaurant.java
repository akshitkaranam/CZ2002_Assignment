package com.company.restaurantessentials;

import com.company.menuItem.MenuItem;
import com.company.menuItem.PromotionPackage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * This class contains details of the Restaurant
 * 1. Name
 * 2. Address
 * 3. List of Table Objects
 * 4. List of Active Orders
 */

public class Restaurant {
    private static String name;
    private static String address;
    private static LocalTime openingTime ;
    private static LocalTime closingTime;
    private static Map<Integer, Table> tableList = new HashMap<>();
    private static List<Order> activeOrders = new ArrayList<>();

    /**
     * @return Map with the table number as a key and the Table object as the value
     */
    public static Map<Integer, Table> getTableList() {
        return tableList;
    }

    /**
     * Add a table given the table number and the number of seats
     *
     * @param tableNumber   the number of the table
     * @param numberOfSeats thr number of seats the table can accommodate
     */
    public static void addTable(int tableNumber, int numberOfSeats) {
        if (!tableList.containsKey(tableNumber)) {
            tableList.put(tableNumber, new Table(tableNumber, numberOfSeats));
        }
    }

    /**
     * Adds an active order to the Restaurant
     *
     * @param order the order object that needs to be activated when the Customer Checks-in
     */
    public static void addActiveOrder(Order order) {
        activeOrders.add(order);
    }

    /**
     * @return list of active orders
     */
    public static List<Order> getActiveOrders() {
        return activeOrders;
    }


    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Restaurant.name = name;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        Restaurant.address = address;
    }

    public static LocalTime getOpeningTime() {
        return openingTime;
    }

    public static void setOpeningTime(LocalTime openingTime) {
        Restaurant.openingTime = openingTime;
    }

    public static LocalTime getClosingTime() {
        return closingTime;
    }

    public static void setClosingTime(LocalTime closingTime) {
        Restaurant.closingTime = closingTime;
    }

    /**
     * Processes the current active reservations to a CSV File: orderReservations.csv
     */
    public static void processActiveReservationsToCSV() {

        try {
            Set<Order> activeReservationOrderSet = new HashSet<>();
            for (var table : tableList.entrySet()) {
                Map<LocalDate, TableDateSlots> copy = new HashMap<>(table.getValue().getTableDateSlotsList());
                for (var tableDateSlots : copy.entrySet()) {
                    activeReservationOrderSet
                            .addAll(table.getValue().getOrderReservationsByDateForTable(tableDateSlots.getKey()));
                }
            }

            List<Order> activeReservationOrderList = new ArrayList<>(activeReservationOrderSet);

            List<List<String>> records = new ArrayList<>();

            for (Order order : activeReservationOrderList) {
                if (order == null) {
                    continue;
                }
                List<String> tempList = new ArrayList<>();
                tempList.add(order.getCustomer().getName());
                tempList.add(order.getCustomer().getContactNumber());
                tempList.add(order.getDate().toString());
                tempList.add(Integer.toString(order.getTableNumber()));
                tempList.add(Integer.toString(order.getGroupSize()));
                tempList.add(order.getReservationStartTime().toString());
                tempList.add(order.getReservationEndTime().toString());
                tempList.add(Integer.toString(order.getOrderNumber()));
                records.add(tempList);
            }

            // create a writer

            FileWriter writer = new FileWriter("src/com/company/storeddata/orderReservations.csv", false);

            // write all records
            for (List<String> record : records) {
                writer.write(String.join(";", record));
                writer.write("\n");
            }

            //close the writer
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Processes the current active orders to a CSV File: activeOrders.csv
     */
    public static void processActiveOrderToCSV() {
        try {
            List<Order> activeOrderListCopy = new ArrayList<>(activeOrders);
            List<List<String>> records = new ArrayList<>();

            for (Order or : activeOrderListCopy) {
                String orderNumber = Integer.toString(or.getOrderNumber());

                Map<MenuItem, Integer> menuItemList = or.getItemsOrderedList();
                Map<String, Integer> menuItemStringList = new HashMap<>();
                for (var entry : menuItemList.entrySet()) {
                    menuItemStringList.put(entry.getKey().getItemName(), entry.getValue());
                }

                Map<PromotionPackage, Integer> promoPackList = or.getPromotionPackageOrderedList();
                Map<String, Integer> promoPackStringList = new HashMap<>();

                for (var entry : promoPackList.entrySet()) {
                    promoPackStringList.put(entry.getKey().getPackageName(), entry.getValue());
                }

                List<String> tempList = new ArrayList<>();
                tempList.add(orderNumber);
                tempList.add(menuItemStringList.toString());
                tempList.add(promoPackStringList.toString());
                records.add(tempList);
            }

            // create a writer

            FileWriter writer = new FileWriter("src/com/company/storeddata/activeOrders.csv", false);

            // write all records
            for (List<String> record : records) {
                writer.write(String.join(";", record));
                writer.write("\n");
            }

            //close the writer
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
