package fr.may_baptiste.allcraft0r_discord.config;

import fr.may_baptiste.allcraft0r_discord.core.SlashCommand;
import fr.may_baptiste.allcraft0r_discord.system.exception.AdminChannelIdInvalidException;
import fr.may_baptiste.allcraft0r_discord.system.exception.GuildIdInvalidException;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
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
public class DiscordConfig {

  @Value("${discord.bot.token}")
  private String botToken;

  @Value("${discord.guild.id}")
  private long guildId;

  private Guild guild;

  @Value("${discord.admin_channel.id}")
  private long adminChannelId;

  private TextChannel adminChannel;

  @Value("${discord.redstone_emoji.id}")
  private String redstoneEmojiId;

  public String getRedstoneEmoji() {
    return "<:redstone:%s>".formatted(redstoneEmojiId);
  }

  public String formatRedstoneNumber(long nb) {
    return "%d %s".formatted(nb, getRedstoneEmoji());
  }

  @Bean
  public JDA jda(List<SlashCommand> commands)
      throws InterruptedException, GuildIdInvalidException, AdminChannelIdInvalidException {

    JDA jda =
        JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(commands.toArray())
            .build();

    jda.awaitReady();
    log.info("Connected as {} ({})", jda.getSelfUser().getName(), jda.getSelfUser().getId());

    guild = jda.getGuildById(guildId);
    if (guild == null) {
      throw new GuildIdInvalidException();
    }
    log.info("Guild found: {} ({})", guild.getName(), guild.getId());

    adminChannel = guild.getChannelById(TextChannel.class, adminChannelId);
    if (adminChannel == null) {
      throw new AdminChannelIdInvalidException();
    }
    log.info("Admin channel found: {} ({})", adminChannel.getName(), adminChannel.getId());

    log.info("Found {} commands:", commands.size());
    commands.forEach(cmd -> log.info("\t- {}: {}", cmd.getDescription(), cmd.getCommandDisplay()));
    guild
        .updateCommands()
        .addCommands(commands.stream().map(SlashCommand::getCommandData).toList())
        .queue();

    return jda;
  }
}
