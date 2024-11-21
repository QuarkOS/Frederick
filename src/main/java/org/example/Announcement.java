package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.Function;

public enum Announcement {

    PRODUCT(Announcement::printPRODUCT),
    ONLINE(Announcement::printONLINE),
    BACKINSTOCK(Announcement::printBACKINSTOCK),
    OUTOFSTOCK(Announcement::printOUTOFSTOCK);

    private final Function<Item, MessageEmbed> function;

    Announcement(Function<Item, MessageEmbed> function) {
        this.function = function;
    }

    public MessageEmbed createMessage(Item item) {
        return function.apply(item);
    }

    public static MessageEmbed printPRODUCT(Item item) {
        return new EmbedBuilder()
                .setTitle("New Product Alert!")
                .setDescription("A new product has been added to the store!")
                .addField("Product Name", item.getName(), false)
                .addField("Product Price", String.valueOf(item.getPrice()), false)
                .addField("Product Stock", String.valueOf(item.getQuantity()), false)
                .build();
    }

    public static MessageEmbed printONLINE(Item item) {
        return new EmbedBuilder()
                .setTitle("@Here\nOnline Store is now open!")
                .setDescription("The online store is now online and ready to take orders!")
                .build();
    }

    public static MessageEmbed printBACKINSTOCK(Item item) {
        return new EmbedBuilder()
                .setTitle("Back in Stock!")
                .setDescription(item.getName() + " is back in stock!")
                .build();
    }

    public static MessageEmbed printOUTOFSTOCK(Item item) {
        return new EmbedBuilder()
                .setTitle("Out of Stock Alert!")
                .setDescription(item.getName() + " is out of stock!")
                .build();
    }
}