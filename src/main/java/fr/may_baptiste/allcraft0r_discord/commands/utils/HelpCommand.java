package fr.may_baptiste.allcraft0r_discord.commands.utils;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class HelpCommand extends SlashCommand {

  private final String name = "help";
  private final String description = "Liste des commandes disponibles";
  private final CommandType type = CommandType.UTILS;

  private final List<SlashCommand> commands;

  @Override
  public SlashCommandData getCommandData() {
    return super.getCommandData()
        .addOptions(
            new OptionData(OptionType.STRING, "type", "Le type des commandes", true)
                .addChoices(getTypesAsChoices()));
  }

  private Command.Choice[] getTypesAsChoices() {
    return Arrays.stream(CommandType.values())
        .filter(type -> type != CommandType.ADMIN)
        .map(type -> new Command.Choice(type.getDescription(), type.name()))
        .toArray(Command.Choice[]::new);
  }

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var typeValue = Objects.requireNonNull(event.getOption("type")).getAsString();
    final var type = CommandType.valueOf(typeValue);

    final var embed = new EmbedBuilder().setTitle(type.getDescription()).setColor(type.getColor());

    for (var field : getCommandsAsFields(type)) {
      embed.addField(field);
    }

    event.replyEmbeds(List.of(embed.build())).setEphemeral(true).queue();
  }

  private MessageEmbed.Field[] getCommandsAsFields(CommandType commandType) {
    if (commandType == CommandType.ADMIN) {
      return new MessageEmbed.Field[0];
    }
    return commands.stream()
        .filter(cmd -> commandType.equals(cmd.getType()))
        .map(
            cmd ->
                new MessageEmbed.Field(
                    "◽️ " + cmd.getCommandDisplay(), "> " + cmd.getDescription(), true))
        .toArray(MessageEmbed.Field[]::new);
  }
}
