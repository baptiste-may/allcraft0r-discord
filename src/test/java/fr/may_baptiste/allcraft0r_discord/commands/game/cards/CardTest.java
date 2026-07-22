package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CardTest {

  @Nested
  class StringRepresentation {

    @Test
    @DisplayName("should format string correctly with suit emoji and rank symbol")
    void shouldFormatStringCorrectly() {
      final var card = new Card(Suit.SPADE, Rank.ACE);
      assertThat(card.toString()).isEqualTo("[ ♠️ A ]");
    }

    @Test
    @DisplayName("should format heart ten card correctly")
    void shouldFormatHeartTenCorrectly() {
      final var card = new Card(Suit.HEART, Rank.TEN);
      assertThat(card.toString()).isEqualTo("[ ♥️ 10 ]");
    }
  }

  @Nested
  class RecordProperties {

    @Test
    @DisplayName("should expose suit and rank components")
    void shouldExposeSuitAndRank() {
      final var card = new Card(Suit.DIAMOND, Rank.KING);
      assertThat(card.suit()).isEqualTo(Suit.DIAMOND);
      assertThat(card.rank()).isEqualTo(Rank.KING);
    }
  }
}
