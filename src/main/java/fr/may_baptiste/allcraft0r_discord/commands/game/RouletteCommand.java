package fr.may_baptiste.allcraft0r_discord.commands.game;

import fr.may_baptiste.allcraft0r_discord.commands.game.roulette.RouletteImageRenderer;
import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashGameCommand;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class RouletteCommand extends SlashGameCommand {

  public static final String ROULETTE_GIF_URL = "https://i.giphy.com/26uf2YTgF5upXUTm0.webp";
  public static final int SPIN_DELAY_SECONDS = 3;

  private static final Map<String, String> CATEGORIES_TEXT =
      Map.of(
          "red", "sur le rouge !",
          "black", "sur le noir !",
          "green", "sur le vert !",
          "even", "sur les nombres pairs !",
          "odd", "sur les nombres impairs !");

  private final String name = "roulette";
  private final String description = "Lance un tour de roulette";
  private final CommandType type = CommandType.GAME;

  private final MoneyService moneyService;
  private final DiscordConfig discordConfig;

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  @Override
  public SlashCommandData getCommandData() {
    return Commands.slash(getName(), getDescription())
        .addOptions(
            new OptionData(OptionType.STRING, "category", "La catégorie choisie", true)
                .addChoice("Couleur Rouge", "red")
                .addChoice("Couleur Noir", "black")
                .addChoice("Couleur Vert", "green")
                .addChoice("Nombres Pairs", "even")
                .addChoice("Nombres Impairs", "odd"),
            new OptionData(OptionType.INTEGER, "mise", "La mise de départ", false)
                .setMinValue(MINIMAL_BET));
  }

  @Override
  public void onCommandExecution(SlashCommandInteractionEvent event) {
    canPlay(event, moneyService)
        .ifPresent(
            bet -> {
              final var option = event.getOption("category");
              if (option == null) {
                return;
              }

              final var category = option.getAsString();
              final var categoryText = CATEGORIES_TEXT.getOrDefault(category, "");
              final var userId = event.getUser().getId();

              final var initialEmbed =
                  new EmbedBuilder()
                      .setColor(new Color(46, 125, 50))
                      .setTitle("🪩 Les jeux sont faits ! 🪩")
                      .setDescription(
                          "Mise : %s %s"
                              .formatted(discordConfig.formatRedstoneNumber(bet), categoryText))
                      .setImage(ROULETTE_GIF_URL)
                      .build();

              event
                  .replyEmbeds(initialEmbed)
                  .queue(
                      _ ->
                          scheduler.schedule(
                              () -> spinRoulette(event, userId, bet, category, categoryText),
                              SPIN_DELAY_SECONDS,
                              TimeUnit.SECONDS));
            });
  }

  private void spinRoulette(
      SlashCommandInteractionEvent event,
      String userId,
      long bet,
      String category,
      String categoryText) {
    final int res = ThreadLocalRandom.current().nextInt(37);
    final long gain = calculateGain(bet, category, res);

    if (gain > 0) {
      moneyService.addMoney(userId, gain);
    }

    final var resColor = getNumberColorEmoji(res);
    final var imageBytes = RouletteImageRenderer.renderWheel(res);
    final var fileUpload = FileUpload.fromData(imageBytes, "result.png");

    final var resultEmbed =
        new EmbedBuilder()
            .setColor(gain == 0 ? new Color(220, 38, 38) : new Color(22, 163, 74))
            .setTitle(gain == 0 ? "🪩 Dommage ! 🪩" : "🪩 Bravo ! 🪩")
            .setDescription(
                "Mise : %s %s\nRésultat : %s %d\nGain : %s"
                    .formatted(
                        discordConfig.formatRedstoneNumber(bet),
                        categoryText,
                        resColor,
                        res,
                        discordConfig.formatRedstoneNumber(gain)))
            .setImage("attachment://result.png")
            .build();

    event.getHook().editOriginalEmbeds(resultEmbed).setFiles(fileUpload).queue();
  }

  public static long calculateGain(long bet, String category, int resultNb) {
    if (resultNb == 0) {
      if ("green".equalsIgnoreCase(category)) {
        return bet * 35;
      }
      return 0;
    }

    return switch (category.toLowerCase()) {
      case "red" -> RouletteImageRenderer.isRed(resultNb) ? bet * 2 : 0;
      case "black" -> RouletteImageRenderer.isBlack(resultNb) ? bet * 2 : 0;
      case "even" -> resultNb % 2 == 0 ? bet * 2 : 0;
      case "odd" -> resultNb % 2 == 1 ? bet * 2 : 0;
      default -> 0;
    };
  }

  private static String getNumberColorEmoji(int number) {
    if (number == 0) {
      return "🟢";
    }
    if (RouletteImageRenderer.isRed(number)) {
      return "🔴";
    }
    return "⚫️";
  }
}
