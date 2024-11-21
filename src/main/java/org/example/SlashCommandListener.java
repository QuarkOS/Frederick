package org.example;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        var hook = event.deferReply(true).complete();

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

            hook.editOriginalEmbeds(Shop.buy(event.getMember(), item, quantity)).queue();
        }

        if (event.getName().equals("checkout")) {
            String location = event.getOption("location").getAsString();
            // Display cart items
            MessageEmbed embed = Shop.checkout(event.getMember(), location);
            hook.editOriginalEmbeds(embed).queue();
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

        if (event.getName().equals("confirm")) {
            int id = (int) event.getOption("id").getAsLong();
            Admin.confirmOrder(id, Shop.unconfirmedOrders, Shop.confirmedOrders);
            event.reply("Order confirmed!").setEphemeral(true).queue();
        }

        if (event.getName().equals("removeorder")) {
            int id = (int) event.getOption("id").getAsLong();
            String reason = event.getOption("reason").getAsString();
            Admin.removeOrder(id, reason, Shop.unconfirmedOrders);
            event.reply("Order removed!").setEphemeral(true).queue();
        }

        if (event.getName().equals("vieworders")) {
            hook.editOriginalEmbeds(Admin.viewAllOrders(Shop.unconfirmedOrders)).queue();
        }


    }
}