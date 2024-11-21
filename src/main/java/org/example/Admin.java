package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Map;

public class Admin {
    public static void main(String[] args) {
        System.out.println("Admin panel");
    }

    public static MessageEmbed removeOrder(int id, String reason, Map<Integer, Order> orders) {
        Order order = orders.get(id);

        // Embed for order not found
        EmbedBuilder notFoundEmbed = new EmbedBuilder()
                .setTitle("Order Not Found")
                .setColor(Color.RED)
                .setDescription("Order ID: " + id + " not found!");

        if (order == null) {
            System.out.println("Order not found");
            return notFoundEmbed.build();
        }

        // Embed for order removed
        EmbedBuilder removedEmbed = new EmbedBuilder()
                .setTitle("Order Removed")
                .setDescription("Order ID: " + id + " removed for reason: " + reason)
                .setColor(Color.GREEN);

        orders.remove(id);
        System.out.println("Order removed: " + order);
        return removedEmbed.build();
    }

    public static MessageEmbed confirmOrder(int id, Map<Integer, Order> unconfirmedOrders, Map<Integer, Order> confirmedOrders) {
        Order order = unconfirmedOrders.get(id);

        // Embed for order not found
        EmbedBuilder notFoundEmbed = new EmbedBuilder()
                .setTitle("Order Not Found")
                .setColor(Color.RED)
                .setDescription("Order ID: " + id + " not found!");

        if (order == null) {
            System.out.println("Order not found");
            return notFoundEmbed.build();
        }

        unconfirmedOrders.remove(id);
        confirmedOrders.put(id, order);
        System.out.println("Order confirmed: " + order);
        return new EmbedBuilder()
                .setTitle("Order Confirmed")
                .setDescription("Order ID: " + id + " confirmed!")
                .setColor(Color.GREEN)
                .build();
    }

    public static MessageEmbed viewAllOrders(Map<Integer, Order> orders) {
        // Embed for no orders
        EmbedBuilder noOrdersEmbed = new EmbedBuilder()
                .setTitle("No Orders")
                .setColor(Color.RED)
                .setDescription("No orders found!");

        // Embed for orders
        EmbedBuilder ordersEmbed = new EmbedBuilder()
                .setTitle("Orders")
                .setColor(Color.GREEN);

        if (orders.isEmpty()) {
            System.out.println("No orders found");
            return noOrdersEmbed.build();
        }

        orders.forEach((id, order) -> {
            ordersEmbed.appendDescription("Order-ID: " + id + "\n" + order.toString() + "\n\n");
        });

        return ordersEmbed.build();
    }
}