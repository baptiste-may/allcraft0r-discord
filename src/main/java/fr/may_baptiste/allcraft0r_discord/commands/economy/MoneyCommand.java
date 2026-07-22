package fr.may_baptiste.allcraft0r_discord.commands.economy;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class MoneyCommand extends SlashCommand {
  private final MoneyService moneyService;
  private final DiscordConfig discordConfig;

  private final String name = "money";
  private final String description = "Affiche son nombre de redstones";
  private final CommandType type = CommandType.ECONOMY;

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var money = moneyService.getMoney(event.getUser().getId());
    event
        .replyEmbeds(
            new EmbedBuilder()
                .setTitle(
                    "Tu as actuellement %s".formatted(discordConfig.formatRedstoneNumber(money)))
                .setColor(new Color(255, 192, 0))
                .build())
        .queue();
  }
}
