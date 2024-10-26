package org.example;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("shop")) {
            // Display shop items
            MessageEmbed embed = Shop.listItems();
            event.replyEmbeds(embed).setEphemeral(true).queue();
        }

        if (event.getName().equals("buy")) {
            // Buy an item
            String itemName = event.getOption("item").getAsString();
            int quantity = (int) event.getOption("quantity").getAsLong();
            Item item = Shop.shopItems.stream().filter(i -> i.getName().equals(itemName)).findFirst().orElse(null);

            if (item == null) {
                event.reply("Item not found!").setEphemeral(true).queue();
                return;
            }

            event.replyEmbeds(Shop.buy(event.getMember(), item, quantity)).setEphemeral(true).queue();
        }

        if (event.getName().equals("checkout")) {
            String location = event.getOption("location").getAsString();
            // Display cart items
            MessageEmbed embed = Shop.checkout(event.getMember(), location);
            event.replyEmbeds(embed).setEphemeral(true).queue();
        }

        if (event.getName().equals("cart")) {
            event.replyEmbeds(Shop.viewCart(event.getMember())).setEphemeral(true).queue();
        }

        if (event.getName().equals("remove")) {
            String item = event.getOption("item").getAsString();
            int quantity = (int) event.getOption("quantity").getAsLong();
            Shop.removeItemFromCart(event.getMember(), item, quantity);
            event.reply("Item removed from cart!").setEphemeral(true).queue();
        }

        if (event.getName().equals("suggest")) {
            String suggestion = event.getOption("suggestion").getAsString();
            event.replyEmbeds(Shop.suggest(suggestion)).setEphemeral(true).queue();
        }

        if (event.getName().equals("help")) {
            event.replyEmbeds(Shop.help()).setEphemeral(true).queue();
        }
    }
}