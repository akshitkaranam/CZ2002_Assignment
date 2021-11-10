package com.company.restaurantfunctions;

import com.company.menuItem.MenuItem;
import com.company.menuItem.PromotionPackage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class Invoice implements Comparable<Invoice> {

    private final Order order;
    private double priceBeforeTax = 0.0;
    private double taxPrice = 0.0;
    private double priceAfterTax = 0.0;
    private double memberDiscount = 0.0;
    private boolean invoicePaid = false;
    private final LocalDateTime localDateTime;



    public Invoice(Order order) {
        this.order = order;
        localDateTime = LocalDateTime.now();
        calculateFinalPrice();
    }

    public Invoice(Order order,LocalDateTime localDateTime) {
        this.order = order;
        this.localDateTime = localDateTime;
        invoicePaid = true;
        calculateFinalPrice();

    }

    private void calculateFinalPrice() {
        boolean customerIsMember = order.getCustomer().isMember();
        Map<MenuItem, Integer> orderItemList = order.getItemsOrderedList();
        Map<PromotionPackage, Integer> orderPackageList = order.getPromotionPackageOrderedList();

        for (Map.Entry<MenuItem, Integer> menuItemQuantityEntry : orderItemList.entrySet()) {
            priceBeforeTax += (menuItemQuantityEntry.getKey().getItemPrice() * menuItemQuantityEntry.getValue());
        }

        for (Map.Entry<PromotionPackage, Integer> promotionPackageQuantityEntry : orderPackageList.entrySet()) {
            priceBeforeTax += (promotionPackageQuantityEntry.getKey().getPackagePrice()
                    * promotionPackageQuantityEntry.getValue());
        }

        if(customerIsMember){
            //5% discount for members
            memberDiscount = priceBeforeTax * 0.05;
        }

        taxPrice = (priceBeforeTax - memberDiscount) * 0.07;
        priceAfterTax = priceBeforeTax + taxPrice;
    }


    public boolean isInvoicePaid() {
        return invoicePaid;
    }

    public void generateReceipt() {

        invoicePaid = true;
        Map<MenuItem, Integer> orderItemList = order.getItemsOrderedList();
        Map<PromotionPackage, Integer> orderPackageList = order.getPromotionPackageOrderedList();
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println(Restaurant.name);
        System.out.println(Restaurant.address);

        System.out.println("Table Number: " + order.getTableNumber());
        System.out.println(localDateTime);
        System.out.println("Customer: " + order.getCustomer().getName());
        System.out.println("Served by: " + order.getStaff().getName() );
        System.out.println("----------------------------------------------");

        for (Map.Entry<MenuItem, Integer> menuItemQuantityEntry : orderItemList.entrySet()) {
            System.out.println(menuItemQuantityEntry.getValue() + " "
                    + menuItemQuantityEntry.getKey().getItemName() + "            "
            +(menuItemQuantityEntry.getKey().getItemPrice()* menuItemQuantityEntry.getValue()));
        }

        for (Map.Entry<PromotionPackage, Integer> promotionPackageQuantityEntry : orderPackageList.entrySet()) {
            System.out.println(promotionPackageQuantityEntry.getValue() + " "
                    + promotionPackageQuantityEntry.getKey().getPackageName() + "            "
                    +(promotionPackageQuantityEntry.getKey().getPackagePrice()* promotionPackageQuantityEntry.getValue()));
        }


        System.out.println("----------------------------------------------");
        System.out.println("Subtotal: " + priceBeforeTax);
        if(order.getCustomer().isMember()){
            System.out.println("Member's Discount: -" + memberDiscount);
        }
        System.out.println("Taxes: " + taxPrice);
        System.out.println("----------------------------------------------");
        System.out.println("Total: " + priceAfterTax);
        System.out.println("----------------------------------------------");
        System.out.println();
        System.out.println();
        InvoiceList.addInvoice(this);
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public int compareTo(Invoice in) {
        return this.localDateTime.compareTo(in.localDateTime);
    }

    public Order getOrder() {
        return order;
    }

    public LocalDate getDate() {
        return localDateTime.toLocalDate();
    }
    public Month getMonth() {
        return localDateTime.getMonth();
    }

    public double getPriceAfterTax() {
        return priceAfterTax;
    }
}
