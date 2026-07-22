package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuitTest {

  @Nested
  class SuitAttributes {

    @Test
    @DisplayName("should provide correct emojis and symbols for each suit")
    void shouldProvideEmojisAndSymbols() {
      assertThat(Suit.SPADE.getEmoji()).isEqualTo("♠️");
      assertThat(Suit.SPADE.getSymbol()).isEqualTo("♠");

      assertThat(Suit.HEART.getEmoji()).isEqualTo("♥️");
      assertThat(Suit.HEART.getSymbol()).isEqualTo("♥");

      assertThat(Suit.DIAMOND.getEmoji()).isEqualTo("♦️");
      assertThat(Suit.DIAMOND.getSymbol()).isEqualTo("♦");

      assertThat(Suit.CLUB.getEmoji()).isEqualTo("♣️");
      assertThat(Suit.CLUB.getSymbol()).isEqualTo("♣");
    }
  }
}
