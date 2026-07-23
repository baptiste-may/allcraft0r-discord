package fr.may_baptiste.allcraft0r_discord.commands.game.spin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SpinGameTest {

  @Nested
  class ConstantsAndConfig {

    @Test
    @DisplayName("should contain expected emojis and reveal delay constant")
    void shouldHaveValidConstants() {
      assertThat(SpinGame.REVEAL_DELAY_MS).isEqualTo(1000L);
      assertThat(SpinGame.EMOJIS).containsExactly("🍇", "🍒", "🫐", "🍉", "🍎", "🍌");
      assertThat(SpinGame.SPINNING_EMOJI).isEqualTo("⏬");
    }
  }

  @Nested
  class GameInitialization {

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("should reply initial spinning embed with reveal delay")
    void shouldReplyInitialEmbed() {
      final var event = mock(SlashCommandInteractionEvent.class);
      final var replyAction = mock(ReplyCallbackAction.class);
      final var discordConfig = mock(DiscordConfig.class);

      when(discordConfig.formatRedstoneNumber(100L)).thenReturn("100 🔴");
      when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(replyAction);

      new SpinGame(event, discordConfig, 100L, _ -> {});

      verify(event).replyEmbeds(any(MessageEmbed.class));
      verify(replyAction)
          .queueAfter(eq(SpinGame.REVEAL_DELAY_MS), eq(TimeUnit.MILLISECONDS), any(Consumer.class));
    }
  }
}
