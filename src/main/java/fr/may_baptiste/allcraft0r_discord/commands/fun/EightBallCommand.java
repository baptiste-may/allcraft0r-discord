package fr.may_baptiste.allcraft0r_discord.commands.fun;

import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EightBallCommand extends SlashCommand {

  private final String name = "8ball";
  private final String description = "Seul l'avenir est ici";
  private final CommandType type = CommandType.FUN;

  private final List<String> responses =
      List.of(
          "Essaye plus tard",
          "Essaye encore",
          "Pas d'avis",
          "C'est ton destin",
          "Le sort en est jeté",
          "Une chance sur deux",
          "Repose ta question",
          "D'après moi, oui",
          "C'est certain",
          "Oui, absolument",
          "Tu peux compter dessus",
          "Sans aucun doute",
          "Très probable",
          "Oui",
          "C'est bien parti",
          "C'est non",
          "Peu probable",
          "Faut pas rêver",
          "N'y compte pas",
          "Impossible");

  private String getRandomResponse() {
    final var index = ThreadLocalRandom.current().nextInt(responses.size());
    return responses.get(index);
  }

  @Override
  public SlashCommandData getCommandData() {
    return super.getCommandData().addOption(OptionType.STRING, "question", "Votre question", true);
  }

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var question = event.getOption("question", OptionMapping::getAsString);
    event
        .replyEmbeds(
            List.of(
                new EmbedBuilder()
                    .setTitle("\uD83D\uDD2E 8 Ball \uD83D\uDD2E")
                    .addField("__%s__".formatted(question), getRandomResponse(), true)
                    .setColor(new Color(128, 0, 128))
                    .build()))
        .queue();
  }
}
