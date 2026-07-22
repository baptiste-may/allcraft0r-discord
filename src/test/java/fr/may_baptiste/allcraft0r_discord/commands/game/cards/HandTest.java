package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HandTest {

  @Nested
  class HandState {

    @Test
    @DisplayName("should start empty")
    void shouldStartEmpty() {
      final var hand = new Hand();

      assertThat(hand.size()).isZero();
      assertThat(hand.getCards()).isEmpty();
      assertThat(hand.toString()).isEmpty();
    }

    @Test
    @DisplayName("should add cards and increment size")
    void shouldAddCards() {
      final var hand = new Hand();
      hand.addCard(new Card(Suit.SPADE, Rank.ACE));
      hand.addCard(new Card(Suit.HEART, Rank.KING));

      assertThat(hand.size()).isEqualTo(2);
      assertThat(hand.toString()).isEqualTo("[ ♠️ A ] [ ♥️ K ]");
    }
  }
}
