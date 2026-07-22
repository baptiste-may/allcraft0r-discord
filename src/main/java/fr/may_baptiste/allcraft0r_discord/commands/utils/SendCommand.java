package fr.may_baptiste.allcraft0r_discord.commands.utils;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class SendCommand extends SlashCommand {

  private final String name = "send";
  private final String description = "Envoie un message aux personnes de puissances";
  private final CommandType type = CommandType.UTILS;

  private final DiscordConfig discordConfig;

  @Override
  public SlashCommandData getCommandData() {
    return super.getCommandData().addOption(OptionType.STRING, "message", "Votre message", true);
  }

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var message = Objects.requireNonNull(event.getOption("message")).getAsString();
    discordConfig
        .getAdminChannel()
        .sendMessageEmbeds(
            new EmbedBuilder()
                .setTitle(message)
                .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                .setColor(Color.WHITE)
                .build())
        .queue(
            _ ->
                event
                    .replyEmbeds(
                        List.of(
                            new EmbedBuilder()
                                .setTitle("Votre message a été correctement envoyé.")
                                .setColor(Color.GREEN)
                                .build()))
                    .setEphemeral(true)
                    .queue());
  }
}
