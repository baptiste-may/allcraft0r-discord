package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeckTest {

  @Nested
  class DeckInitialization {

    @Test
    @DisplayName("should initialize deck with 52 standard cards")
    void shouldInitializeWith52Cards() {
      final var deck = new Deck();
      final var hand = new Hand();
      deck.deal(hand, 52);

      assertThat(hand.size()).isEqualTo(52);
      assertThatThrownBy(deck::draw)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("Deck is empty");
    }
  }

  @Nested
  class DeckOperations {

    @Test
    @DisplayName("should draw single card from deck")
    void shouldDrawCard() {
      final var deck = new Deck();
      final var card = deck.draw();

      assertThat(card).isNotNull();
    }

    @Test
    @DisplayName("should deal requested number of cards to hand")
    void shouldDealCardsToHand() {
      final var deck = new Deck();
      final var hand = new Hand();

      deck.deal(hand, 5);
      assertThat(hand.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("should shuffle deck without throwing exceptions")
    void shouldShuffleDeck() {
      final var deck = new Deck();
      deck.shuffle();

      final var card = deck.draw();
      assertThat(card).isNotNull();
    }
  }
}
