package fr.may_baptiste.allcraft0r_discord.commands.game.spin;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SpinGame {
  public static final long REVEAL_DELAY_MS = 1000;
  public static final List<String> EMOJIS = List.of("🍇", "🍒", "🫐", "🍉", "🍎", "🍌");
  public static final String SPINNING_EMOJI = "⏬";

  private final DiscordConfig discordConfig;
  private final long bet;
  private final Consumer<Long> callback;
  private final int[] slots = new int[3];
  private int revealed = 0;
  private Long profit = null;

  public SpinGame(
      SlashCommandInteractionEvent event,
      DiscordConfig discordConfig,
      long bet,
      Consumer<Long> callback) {
    this.discordConfig = discordConfig;
    this.bet = bet;
    this.callback = callback;
    event
        .replyEmbeds(buildEmbed())
        .queueAfter(REVEAL_DELAY_MS, TimeUnit.MILLISECONDS, this::revealNext);
  }

  private void revealSlot() {
    slots[revealed++] = ThreadLocalRandom.current().nextInt(EMOJIS.size());
  }

  private boolean isFinished() {
    return revealed == slots.length;
  }

  private boolean isJackpot() {
    return slots[0] == slots[1] && slots[1] == slots[2];
  }

  private void revealNext(InteractionHook hook) {
    revealSlot();

    if (isFinished()) {
      profit = isJackpot() ? bet * EMOJIS.size() : 0L;
    }

    final var embed = buildEmbed();
    if (isFinished()) {
      hook.editOriginalEmbeds(embed).queue();
      callback.accept(profit);
    } else {
      hook.editOriginalEmbeds(embed)
          .queueAfter(REVEAL_DELAY_MS, TimeUnit.MILLISECONDS, _ -> revealNext(hook));
    }
  }

  private MessageEmbed buildEmbed() {
    return new EmbedBuilder()
        .setTitle("\uD83C\uDFB0 Spiiinnnnnn \uD83C\uDFB0")
        .setColor(new Color(255, 192, 0))
        .setDescription(createDescription())
        .build();
  }

  private String createDescription() {
    return "Mise : %s\n".formatted(discordConfig.formatRedstoneNumber(bet))
        + (profit != null
            ? "Gain : %s\n".formatted(discordConfig.formatRedstoneNumber(profit))
            : "")
        + drawMachine();
  }

  private String drawMachine() {
    return
"""
```
  %s | %s | %s
> %s | %s | %s <
  %s | %s | %s
```
"""
        .formatted(
            getEmoji(0, -1),
            getEmoji(1, -1),
            getEmoji(2, -1),
            getEmoji(0, 0),
            getEmoji(1, 0),
            getEmoji(2, 0),
            getEmoji(0, 1),
            getEmoji(1, 1),
            getEmoji(2, 1));
  }

  private String getEmoji(int slotIndex, int rowOffset) {
    if (slotIndex >= revealed) {
      return SPINNING_EMOJI;
    }
    return EMOJIS.get(
        (slots[slotIndex] + rowOffset % EMOJIS.size() + EMOJIS.size()) % EMOJIS.size());
  }
}
