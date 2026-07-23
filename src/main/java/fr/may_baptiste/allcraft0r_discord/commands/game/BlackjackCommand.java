package fr.may_baptiste.allcraft0r_discord.commands.game;

import fr.may_baptiste.allcraft0r_discord.commands.game.blackjack.BlackjackGame;
import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashGameCommand;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class BlackjackCommand extends SlashGameCommand {
  private final MoneyService moneyService;
  private final DiscordConfig discordConfig;

  private final String name = "blackjack";
  private final String description = "Lance une partie de blackjack";
  private final CommandType type = CommandType.GAME;

  @Override
  public void onCommandExecution(SlashCommandInteractionEvent event) {
    canPlay(event, moneyService)
        .ifPresent(
            bet ->
                new BlackjackGame(
                    event,
                    discordConfig,
                    moneyService,
                    bet,
                    profits -> moneyService.addMoney(event.getUser().getId(), profits)));
  }
}
