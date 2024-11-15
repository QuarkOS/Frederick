package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Announcement {

    PRODUCT(null, null, Announcement::printPRODUCT),
    ONLINE(null, null),
    BACKINSTOCK(null, null);

    private String message;
    private Item item;

    Announcement(Item item, String message, Function<Item, MessageEmbed> function) {
        this.item = item;
        this.message = message;
        MessageEmbed embed = function.apply(item);
    }

    public String getMessage() {
        return message;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static MessageEmbed printPRODUCT(Item item) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("New Product Alert!");
        eb.setDescription("A new product has been added to the store!");
        eb.addField("Product Name", item.getName(), false);
        eb.addField("Product Price", String.valueOf(item.getPrice()), false);
        eb.addField("Product Stock", String.valueOf(item.getQuantity()), false);

        eb.addField("Additional Information", message, false);

        return eb.build();
    }

    public MessageEmbed printONLINE() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("@Here\nOnline Store is now open!");
        eb.setDescription("The online store is now online and ready to take orders!");

        eb.addField("Additional Information", message, false);
        return eb.build();
    }

    public MessageEmbed printOUTOFSTOCK() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Out of Stock Alert!");
        eb.setDescription(item.getName() + " is out of stock!");

        return ;
    }
}
