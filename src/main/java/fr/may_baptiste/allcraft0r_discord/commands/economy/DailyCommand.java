package fr.may_baptiste.allcraft0r_discord.commands.economy;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.system.exception.commands.CannotExecuteDailyException;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.Color;
import java.time.ZoneOffset;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class DailyCommand extends SlashCommand {
  private final MoneyService moneyService;
  private final DiscordConfig discordConfig;

  private final String name = "daily";
  private final String description = "Récupère sa redstone quotidienne";
  private final CommandType type = CommandType.ECONOMY;

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    try {
      final var currentMoney = moneyService.executeDaily(event.getUser().getId());
      event
          .replyEmbeds(
              List.of(
                  new EmbedBuilder()
                      .setTitle(
                          "Tu as reçu %s\nTu as maintenant %s"
                              .formatted(
                                  discordConfig.formatRedstoneNumber(MoneyService.DAILY_MONEY),
                                  discordConfig.formatRedstoneNumber(currentMoney)))
                      .setColor(Color.GREEN)
                      .build()))
          .queue();
    } catch (CannotExecuteDailyException exception) {
      event
          .replyEmbeds(
              List.of(
                  new EmbedBuilder()
                      .setTitle(
                          "Tu as déjà récupéré ta redstone quotidienne.\nTu pourra executer cette commande à nouveau **<t:%d:R>**"
                              .formatted(
                                  exception.getNextAvailableDaily().toEpochSecond(ZoneOffset.UTC)))
                      .setColor(Color.RED)
                      .build()))
          .setEphemeral(true)
          .queue();
    }
  }
}
