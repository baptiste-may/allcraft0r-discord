package fr.may_baptiste.allcraft0r_discord.commands.fun;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TankCommand extends SlashCommand {

  private final String name = "tank";
  private final String description = "AMERICA ! F*CK YEAHH !!";
  private final CommandType type = CommandType.FUN;

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    event.reply("https://tenor.com/view/tank-gif-10952763").queue();
  }
}
