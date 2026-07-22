package fr.may_baptiste.allcraft0r_discord.commands.utils;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class LinksCommand extends SlashCommand {

  private final String name = "links";
  private final String description = "Affiche des liens en rapport avec allcraft0r";
  private final CommandType type = CommandType.UTILS;

  private final Button ytBase =
      Button.link(
          "https://www.youtube.com/channel/UCY8ryk_01LytUhgfA5X3vFg",
          "Chaîne Youtube de allcraft0r");
  private final Button ytBestOf =
      Button.link(
          "https://www.youtube.com/channel/UCQH2Kxrr6Y68ZcBWfJdtZ6A",
          "Chaîne Youtube Best Of Discord");
  private final Button x = Button.link("https://x.com/bestOfAllcraft", "Compte X Best of Discord");

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    event
        .reply("Voici plusieurs lien en rapport avec allcraft0r :")
        .addComponents(List.of(ActionRow.of(ytBase, ytBestOf, x)))
        .setEphemeral(true)
        .queue();
  }
}
