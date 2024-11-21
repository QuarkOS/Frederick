package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    private static JDA builder;

    public static void main(String[] args) {
        System.out.println("Starting bot...");

        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        String token = dotenv.get("DISCORD_TOKEN");

        // Check if token is null
        if (token == null) {
            System.err.println("Token not found! Please check your .env file.");
            return;
        }

        try {
            builder = JDABuilder.createDefault(token)
                    .addEventListeners(new SlashCommandListener())
                    .build();
            builder.awaitReady();

            Guild guild = builder.getGuildById(dotenv.get("GUILD_ID"));
            addCommands(guild);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCommands(Guild guild) {
        // Create the shop command
        guild.upsertCommand(Commands.slash("shop", "Displays the shop items"))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // Create the buy command with dynamic options
        OptionData itemOption = new OptionData(OptionType.STRING, "item", "The item to buy", true)
                .addChoices(Shop.shopItems.stream()
                        .map(item -> new Command.Choice(item.getName(), item.getName()))
                        .collect(Collectors.toList()));


        // Buy command
        guild.upsertCommand(Commands.slash("buy", "Buy an item from the shop")
                        .addOptions(itemOption)
                        .addOption(OptionType.INTEGER, "quantity", "The quantity to buy", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));


        // Checkout command
        guild.upsertCommand(Commands.slash("checkout", "Checkout the cart")
                        .addOption(OptionType.STRING, "location", "Your location", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));


        // Cart command
        guild.upsertCommand(Commands.slash("cart", "Check cart contents"))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));


        // Remove command
        guild.upsertCommand(Commands.slash("remove", "Remove an item from the cart")
                        .addOptions(itemOption)
                        .addOption(OptionType.INTEGER, "quantity", "The quantity to remove", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));


        // Review command
        guild.upsertCommand(Commands.slash("suggest", "Review your experience")
                        .addOption(OptionType.STRING, "suggestion", "Suggest a new product", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // Help command
        guild.upsertCommand(Commands.slash("help", "Get help"))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // Confirm command
        guild.upsertCommand(Commands.slash("confirm", "Confirm an order")
                        .addOption(OptionType.INTEGER, "id", "The order ID", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // Remove order command
        guild.upsertCommand(Commands.slash("removeorder", "Remove an order")
                        .addOption(OptionType.INTEGER, "id", "The order ID", true)
                        .addOption(OptionType.STRING, "reason", "The reason for removal", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // View orders command
        guild.upsertCommand(Commands.slash("vieworders", "View all orders"))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        // Send Announcement command
        OptionData announcementOption = new OptionData(OptionType.STRING, "announcement", "The type of announcement", true)
                .addChoices(
                        Arrays.stream(Announcement.values())
                                .map(announcement -> new Command.Choice(announcement.name(), announcement.name()))
                                .collect(Collectors.toList())
                );

        guild.upsertCommand(Commands.slash("sendannouncement", "Send an announcement")
                        .addOptions(announcementOption))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));
    }

    public static JDA getJDA() {
        return builder;
    }
}