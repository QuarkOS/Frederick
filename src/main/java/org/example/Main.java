package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

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
                        .map(item -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(item.getName(), item.getName()))
                        .collect(Collectors.toList()));



        guild.upsertCommand(Commands.slash("buy", "Buy an item from the shop")
                        .addOptions(itemOption)
                        .addOption(OptionType.INTEGER, "quantity", "The quantity to buy", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));


        guild.upsertCommand(Commands.slash("checkout", "Checkout the cart")
                        .addOption(OptionType.STRING, "location", "Your location", true))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));

        guild.upsertCommand(Commands.slash("cart", "Check cart contents"))
                .queue(command -> System.out.println("Slash command created: " + command.getName()));
    }

    public static JDA getJDA() {
        return builder;
    }
}
