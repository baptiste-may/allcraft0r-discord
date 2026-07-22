package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RankTest {

  @Nested
  class RankValues {

    @Test
    @DisplayName("should match expected symbol and numeric value")
    void shouldMatchSymbolAndValue() {
      assertThat(Rank.ACE.getSymbol()).isEqualTo("A");
      assertThat(Rank.ACE.getValue()).isEqualTo(1);

      assertThat(Rank.TEN.getSymbol()).isEqualTo("10");
      assertThat(Rank.TEN.getValue()).isEqualTo(10);

      assertThat(Rank.JACK.getSymbol()).isEqualTo("J");
      assertThat(Rank.JACK.getValue()).isEqualTo(10);

      assertThat(Rank.QUEEN.getSymbol()).isEqualTo("Q");
      assertThat(Rank.QUEEN.getValue()).isEqualTo(10);

      assertThat(Rank.KING.getSymbol()).isEqualTo("K");
      assertThat(Rank.KING.getValue()).isEqualTo(10);
    }
  }
}
