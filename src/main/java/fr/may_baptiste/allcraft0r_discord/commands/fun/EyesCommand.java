package fr.may_baptiste.allcraft0r_discord.commands.fun;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EyesCommand extends SlashCommand {

  private final String name = "eyes";
  private final String description = "I'm watching you...";
  private final CommandType type = CommandType.FUN;

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    event.reply("\uD83D\uDC41\uD83D\uDC44\uD83D\uDC41").queue();
  }
}
