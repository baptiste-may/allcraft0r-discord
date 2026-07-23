package fr.may_baptiste.allcraft0r_discord.commands.utils;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.awt.*;
import java.util.List;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PingCommand extends SlashCommand {

  private final String name = "ping";
  private final String description = "Lance une balle de ping pong";
  private final CommandType type = CommandType.UTILS;

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    event
        .replyEmbeds(
            List.of(
                new EmbedBuilder()
                    .setTitle("\uD83C\uDFD3 Pong !")
                    .setDescription("⏳ %dms".formatted(event.getJDA().getGatewayPing()))
                    .setColor(Color.ORANGE)
                    .build()))
        .setEphemeral(true)
        .queue();
  }
}
