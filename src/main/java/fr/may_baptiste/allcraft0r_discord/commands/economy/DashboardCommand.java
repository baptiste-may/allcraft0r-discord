package fr.may_baptiste.allcraft0r_discord.commands.economy;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.Color;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class DashboardCommand extends SlashCommand {
  private final MoneyService moneyService;
  private final DiscordConfig discordConfig;

  private final String name = "dashboard";
  private final String description = "Affiche les 10 membres ayant le plus de redstone";
  private final CommandType type = CommandType.ECONOMY;

  private final List<String> emojis =
      List.of("🏆", "🥈", "🥉", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟");

  public void onCommandExecution(SlashCommandInteractionEvent event) {
    final var leaderboard = moneyService.getLeaderboard(10);

    final var description = new StringBuilder();
    for (int i = 0; i < Math.min(10, leaderboard.size()); i++) {
      final var user = leaderboard.get(i);
      description
          .repeat("#", Math.min(i + 1, 3))
          .append(" ")
          .append(emojis.get(i))
          .append(" <@")
          .append(user.id())
          .append("> - **")
          .append(discordConfig.formatRedstoneNumber(user.money()))
          .append("**\n");
    }

    event
        .replyEmbeds(
            List.of(
                new EmbedBuilder()
                    .setTitle(
                        "Dashboard de redstone %s".formatted(discordConfig.getRedstoneEmoji()))
                    .setDescription(description.toString())
                    .setColor(Color.RED)
                    .build()))
        .queue();
  }
}
