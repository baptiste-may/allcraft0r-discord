package fr.may_baptiste.allcraft0r_discord.commands.admin;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LockCommand extends SlashCommand {

  private final String name = "lock";
  private final String description = "Permet de lock un channel";
  private final CommandType type = null;

  @Override
  public SlashCommandData getCommandData() {
    return super.getCommandData()
        .addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "Le channel cible", true)
                .setChannelTypes(ChannelType.TEXT))
        .addOption(OptionType.STRING, "raison", "La raison du lock", false)
        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
  }

  @Override
  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var target =
        Objects.requireNonNull(event.getOption("channel", OptionMapping::getAsChannel))
            .asTextChannel();
    final var reason = event.getOption("raison", null, OptionMapping::getAsString);
    final var everyoneRole = target.getGuild().getPublicRole();
    final var canEveryoneSendMessages = everyoneRole.hasPermission(target, Permission.MESSAGE_SEND);

    if (!canEveryoneSendMessages) {
      event
          .replyEmbeds(
              List.of(
                  new EmbedBuilder()
                      .setTitle("Le channel est déjà lock !")
                      .setColor(Color.YELLOW)
                      .build()))
          .setEphemeral(true)
          .queue();
      return;
    }

    target
        .upsertPermissionOverride(everyoneRole)
        .deny(Permission.MESSAGE_SEND)
        .queue(
            _ -> {
              event
                  .replyEmbeds(
                      List.of(
                          new EmbedBuilder()
                              .setTitle("Le channel a été lock !")
                              .setColor(Color.GREEN)
                              .build()))
                  .setEphemeral(true)
                  .queue();

              final var embed =
                  new EmbedBuilder()
                      .setTitle("Ce channel a été lock ⛔️")
                      .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                      .setColor(Color.RED);

              if (reason != null && !reason.isBlank()) {
                embed.setDescription(reason);
              }

              target.sendMessageEmbeds(embed.build()).queue();
            });
  }
}
