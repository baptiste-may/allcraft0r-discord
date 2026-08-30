package fr.may_baptiste.allcraft0r_discord.config;

import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.system.exception.AdminChannelIdInvalidException;
import fr.may_baptiste.allcraft0r_discord.system.exception.GuildIdInvalidException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
@NoArgsConstructor
public class DiscordConfig {

  @Getter(AccessLevel.NONE)
  @Value("${discord.bot.token}")
  private String botToken;

  @Value("${discord.guild.id}")
  private long guildId;

  @Value("${discord.admin_channel.id}")
  private long adminChannelId;

  @Value("${discord.redstone_emoji.id}")
  private String redstoneEmojiId;

  private Guild guild;

  private TextChannel adminChannel;

  public DiscordConfig(String botToken, long guildId, long adminChannelId, String redstoneEmojiId) {
    this.botToken = botToken;
    this.guildId = guildId;
    this.adminChannelId = adminChannelId;
    this.redstoneEmojiId = redstoneEmojiId;
  }

  public String getRedstoneEmoji() {
    return "<:redstone:%s>".formatted(redstoneEmojiId);
  }

  public String formatRedstoneNumber(long nb) {
    return String.format(Locale.FRANCE, "%,d %s", nb, getRedstoneEmoji());
  }

  @Bean
  public JDA jda(List<SlashCommand> commands)
      throws InterruptedException, GuildIdInvalidException, AdminChannelIdInvalidException {

    final var jda =
        JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(commands.toArray())
            .build();

    final var connectionFuture =
        CompletableFuture.runAsync(
            () -> {
              try {
                jda.awaitStatus(Status.CONNECTED);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                    "Interrupted while awaiting JDA status CONNECTED", e);
              }
            });

    try {
      connectionFuture.get(30, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      connectionFuture.cancel(true);
      throw new IllegalStateException("Timed out waiting for JDA to reach status CONNECTED", e);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      throw new IllegalStateException("Failed while awaiting JDA status CONNECTED", e.getCause());
    }
    log.info("Connected as {} ({})", jda.getSelfUser().getName(), jda.getSelfUser().getId());

    guild = jda.getGuildById(guildId);
    if (guild == null) {
      throw new GuildIdInvalidException(guildId);
    }
    log.info("Guild found: {} ({})", guild.getName(), guild.getId());

    adminChannel = guild.getChannelById(TextChannel.class, adminChannelId);
    if (adminChannel == null) {
      throw new AdminChannelIdInvalidException(adminChannelId);
    }
    log.info("Admin channel found: {} ({})", adminChannel.getName(), adminChannel.getId());

    log.info("Found {} commands:", commands.size());
    commands.forEach(cmd -> log.info("\t- {}: {}", cmd.getDescription(), cmd.getCommandDisplay()));
    guild
        .updateCommands()
        .addCommands(commands.stream().map(SlashCommand::getCommandData).toList())
        .queue(
            _ -> log.info("Successfully registered slash commands"),
            error -> log.error("Failed to register slash commands", error));

    return jda;
  }
}
