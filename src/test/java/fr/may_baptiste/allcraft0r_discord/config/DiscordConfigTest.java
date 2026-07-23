package fr.may_baptiste.allcraft0r_discord.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DiscordConfigTest {

  @Nested
  class FormattingHelpers {

    @Test
    @DisplayName("should format redstone emoji and number correctly")
    void shouldFormatRedstoneEmojiAndNumber() {
      final var config = new DiscordConfig("token", 123L, 456L, "123456789");

      assertThat(config.getRedstoneEmoji()).isEqualTo("<:redstone:123456789>");
      assertThat(config.formatRedstoneNumber(42L)).isEqualTo("42 <:redstone:123456789>");
    }
  }
}
