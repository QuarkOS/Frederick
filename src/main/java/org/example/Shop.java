package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {

    // Shop items
    public static List<Item> shopItems = List.of(
            new Item("Lay's Paprika", 1, 10),
            new Item("Lay's Salt", 1, 20),
            new Item("M&M's", 1, 15),
            new Item("Nic Nac's", 2.5, 30),
            new Item("Skittle's", 1, 15),
            new Item("Jumpy's", 1.7, 10),
            new Item("Red Bull Blue Edition", 1.5, 14),
            new Item("Red Bull Sea Blue Edition", 1.5, 24),
            new Item("Red Bull Orange Edition", 1.5, 10)
    );

    // Cart items
    public static Map<String, List<Item>> cartItems = new HashMap<>();

    // Buy an item
    public static MessageEmbed buy(Member member, Item item, int quantity) {
        Double total = item.getPrice() * quantity;

        if (item.getQuantity() < quantity || item.getQuantity() - quantity < 0) {
            return new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("Not enough stock!")
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .build();
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Success")
                .setColor(Color.GREEN)
                .addField("Item bought", item.getName() + " x" + quantity, false)
                .addField("Total price", String.format("%.2f €", total), false);


        System.out.println(item.getName() + " zum Warenkorb hinzugefügt! " + quantity + "x " + item.getPrice() + " €");

        if (!cartItems.containsKey(member.getId())) {
            cartItems.put(member.getId(), new ArrayList<>());
        }

        List<Item> li = cartItems.get(member.getId());
        li.add(new Item(item.getName(), item.getPrice(), quantity));
        cartItems.put(member.getId(), li);

        return eb.build();
    }

    // List items
    public static MessageEmbed listItems() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Shop Items");
        eb.setDescription("Here are the available items:");
        eb.setColor(Color.DARK_GRAY);

        for (Item item : shopItems) {
            eb.addField("**" + item.getName() + "**", String.format("**Price:** %.2f €\n**Quantity:** %d", item.getPrice(), item.getQuantity()), false);
        }

        eb.setTimestamp(Instant.now());

        return eb.build();
    }

    // View cart
    public static MessageEmbed viewCart(Member member) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Cart");
        eb.setDescription("Here are the items in your cart:");
        eb.setColor(Color.DARK_GRAY);
        double total = 0;

        if (cartItems.containsKey(member.getId())) {
            for (Item item : cartItems.get(member.getId())) {
                total += item.getPrice() * item.getQuantity();
                eb.addField("**" + item.getName() + "**", String.format("**Price:** %.2f €\n**Quantity:** %d", item.getPrice(), item.getQuantity()), false);
            }
        } else {
            eb.setDescription("Your cart is empty!");
        }

        eb.addField("Total", String.format("%.2f €", total), false);
        eb.setTimestamp(Instant.now());

        return eb.build();
    }

    // Checkout
    public static MessageEmbed checkout(Member member, String location) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Checkout");
        eb.setDescription("Here are the items in your cart:");
        eb.setColor(Color.DARK_GRAY);
        String name = member.getEffectiveName();
        double total = 0;
        String productDetails = "";

        StringBuilder orderDetails = new StringBuilder("New order!\n==================\n");
        try {

            for (Item item : cartItems.get(member.getId())) {
                System.out.println(item.getName() + " x" + item.getQuantity() + " for " + item.getPrice() + " €");
                total += item.getPrice() * item.getQuantity();
                eb.addField("**" + item.getName() + "**", String.format("**Price:** %.2f €\n**Quantity:** %d", item.getPrice(), item.getQuantity()), false);

                orderDetails.append(String.format("**%s**: %.2f € x%d\n", item.getName(), item.getPrice(), item.getQuantity()) + "\n**Name**: " + name + "\n**Location**: " + location + "\n");

                Item shopItem = shopItems.stream()
                        .filter(i -> i.getName().equals(item.getName()))
                        .findFirst()
                        .orElse(null);
                productDetails += item.getName() + " ";
                if (shopItem != null) {
                    shopItem.setQuantity(shopItem.getQuantity() - item.getQuantity());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String template = "BCD\n002\n1\nSCT\nRVSAATS019\nEmilio Schwaiger\nAT473501900000222471\nEUR" + String.format("%.2f", total) + "\n\n\n" + productDetails;
        String encodedTemplate = URLEncoder.encode(template, StandardCharsets.UTF_8);

        orderDetails.append("==================\n");
        orderDetails.append(String.format("**Total**: %.2f €", total));
        insertOrdertoJSON(cartItems.get(member.getId()));

        cartItems.get(member.getId()).clear();
        eb.setTimestamp(Instant.now());
        eb.addField("Total", String.format("%.2f €", total), false);
        eb.setImage("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + encodedTemplate);

        Dotenv dotenv = Dotenv.configure().directory("./").load();
        String guildID = dotenv.get("GUILD_ID");
        String orderChannelID = dotenv.get("ORDER_CHANNEL_ID");

        try {
            TextChannel orderChannel = Main.getJDA().getTextChannelById(orderChannelID);
            if (orderChannel != null) {
                MessageEmbed.AuthorInfo ai = new MessageEmbed.AuthorInfo(member.getNickname(), null, member.getAvatarUrl(), null);
                orderChannel.sendMessageEmbeds(new MessageEmbed(null, "New order!", orderDetails.toString(), EmbedType.RICH, null, 0, null, null, ai, null, null, null, null)).queue();
            } else {
                System.err.println("Order channel not found.");
            }
        } catch (RuntimeException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }

        return eb.build();
    }

    // Insert order to JSON
    public static void insertOrdertoJSON(List<Item> orderList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Item> existingOrders = new ArrayList<>();

        // Read existing orders from the file
        try (FileReader reader = new FileReader("orders.json")) {
            existingOrders = gson.fromJson(reader, new TypeToken<List<Item>>() {
            }.getType());
            if (existingOrders == null) {
                existingOrders = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add new orders to the existing orders
        existingOrders.addAll(orderList);

        // Write updated orders back to the file
        String json = gson.toJson(existingOrders);
        try (FileWriter writer = new FileWriter("orders.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove Item from cart
    public static void removeItemFromCart(Member member, String itemToRemove, int quantity) {
        List<Item> items = cartItems.get(member.getId());
        if (items != null) {
            items.removeIf(item -> {
                if (item.getName().equals(itemToRemove)) {
                    if (item.getQuantity() > quantity) {
                        item.setQuantity(item.getQuantity() - quantity);
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            });
            cartItems.put(member.getId(), items);
        }
    }

    // Review
    public static MessageEmbed suggest(String suggestion) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Review");
        eb.addField("Suggestion", suggestion, false);
        eb.setColor(Color.BLUE);
        eb.setTimestamp(Instant.now());

        EmbedBuilder thanksEmbed = new EmbedBuilder();
        thanksEmbed.setTitle("Thank you for your suggestion!");
        thanksEmbed.setColor(Color.GREEN);
        thanksEmbed.setTimestamp(Instant.now());

        Dotenv dotenv = Dotenv.configure().directory("./").load();
        String suggestionChannelID = dotenv.get("SUGGESTION_CHANNEL_ID");

        try {
            TextChannel suggestionChannel = Main.getJDA().getTextChannelById(suggestionChannelID);
            if (suggestionChannel != null) {
                suggestionChannel.sendMessageEmbeds(eb.build()).queue();
            } else {
                System.err.println("Suggestion channel not found.");
            }
        } catch (RuntimeException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }

        return thanksEmbed.build();
    }

    // Help
    public static MessageEmbed help() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Help");
        eb.setDescription("Here are the available commands:");
        eb.setColor(Color.GREEN);
        eb.addField("/shop", "Displays the shop items", false);
        eb.addField("/buy", "Buy an item from the shop", false);
        eb.addField("/checkout", "Checkout the cart", false);
        eb.addField("/cart", "Check cart contents", false);
        eb.addField("/remove", "Remove an item from the cart", false);
        eb.addField("/suggest", "Review your experience", false);
        eb.addField("/help", "Displays this message", false);
        eb.setTimestamp(Instant.now());
        return eb.build();
    }

    // Notify all about a certain message
    public static void notifyAll(Announcement announcement) {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        String guildID = dotenv.get("GUILD_ID");

        try {
            User[] users = Main.getJDA().getUsers().toArray(new User[0]);

            for (User user : users) {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage(announcement).queue();
                });
            }
        } catch (RuntimeException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}