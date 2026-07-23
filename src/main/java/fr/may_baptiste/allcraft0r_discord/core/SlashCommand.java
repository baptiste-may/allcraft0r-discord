package fr.may_baptiste.allcraft0r_discord.core;

import fr.may_baptiste.allcraft0r_discord.system.exception.CommandExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Slf4j
public abstract class SlashCommand extends ListenerAdapter {

  public abstract String getName();

  public abstract String getDescription();

  public abstract CommandType getType();

  public SlashCommandData getCommandData() {
    return Commands.slash(getName(), getDescription());
  }

  public String getCommandDisplay() {
    return "/"
        + getName()
        + " "
        + getCommandData().getOptions().stream()
            .map(
                option ->
                    option.isRequired()
                        ? "<%s>".formatted(option.getName())
                        : "(%s)".formatted(option.getName()))
            .collect(Collectors.joining(" "));
  }

  public abstract void onCommandExecution(SlashCommandInteractionEvent event);

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals(getName())) return;
    log.info(
        "{} ({}) called slash command {}",
        event.getUser().getName(),
        event.getUser().getId(),
        getName());
    try {
      onCommandExecution(event);
    } catch (CommandExecutionException exception) {
      log.warn(
          "An error occurred while {} ({}) executing command {} : {}",
          event.getUser().getName(),
          event.getUser().getId(),
          getName(),
          exception.getMessage());
      final var errorMessage =
          "Une erreur s'est produite : ```\n%s\n```".formatted(exception.getMessage());
      if (event.isAcknowledged()) {
        event.getHook().sendMessage(errorMessage).setEphemeral(true).queue();
      } else {
        event.reply(errorMessage).setEphemeral(true).queue();
      }
    }
  }
}
