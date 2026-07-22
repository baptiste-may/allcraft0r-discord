package fr.may_baptiste.allcraft0r_discord.commands.game.blackjack;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Card;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Deck;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Hand;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Rank;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BlackjackGameTest {

  @Nested
  class WeightCalculation {

    @Test
    @DisplayName("should calculate number cards accurately")
    void shouldCalculateNumberCardsAccurately() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.SPADE, Rank.FIVE));
      hand.addCard(new Card(Suit.HEART, Rank.EIGHT));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(13);
    }

    @Test
    @DisplayName("should calculate face cards as 10")
    void shouldCalculateFaceCardsAs10() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.DIAMOND, Rank.JACK));
      hand.addCard(new Card(Suit.CLUB, Rank.QUEEN));
      hand.addCard(new Card(Suit.SPADE, Rank.KING));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(30);
    }

    @Test
    @DisplayName("should count Ace as 11 when under or equal to 21")
    void shouldCountAceAs11WhenPossible() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.HEART, Rank.ACE));
      hand.addCard(new Card(Suit.SPADE, Rank.NINE));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(20);
    }

    @Test
    @DisplayName("should count Ace as 1 when total exceeds 21")
    void shouldCountAceAs1WhenBusting() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.HEART, Rank.ACE));
      hand.addCard(new Card(Suit.SPADE, Rank.NINE));
      hand.addCard(new Card(Suit.CLUB, Rank.FIVE));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(15);
    }

    @Test
    @DisplayName("should correctly handle multiple Aces")
    void shouldHandleMultipleAces() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.HEART, Rank.ACE));
      hand.addCard(new Card(Suit.SPADE, Rank.ACE));
      hand.addCard(new Card(Suit.CLUB, Rank.NINE));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(21);
    }

    @Test
    @DisplayName("should correctly handle three Aces")
    void shouldHandleThreeAces() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.HEART, Rank.ACE));
      hand.addCard(new Card(Suit.SPADE, Rank.ACE));
      hand.addCard(new Card(Suit.CLUB, Rank.ACE));

      assertThat(BlackjackGame.calculateWeight(hand)).isEqualTo(13);
    }
  }

  @Nested
  class DeckAndHandTests {

    @Test
    @DisplayName("should initialize a 52 card deck and deal cards to hand")
    void shouldDealCardsProperly() {
      Deck deck = new Deck();
      Hand hand = new Hand();

      deck.deal(hand, 2);

      assertThat(hand.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("should format hand string representation correctly")
    void shouldFormatHandString() {
      Hand hand = new Hand();
      hand.addCard(new Card(Suit.SPADE, Rank.ACE));
      hand.addCard(new Card(Suit.HEART, Rank.TEN));

      assertThat(hand.toString()).isEqualTo("[ ♠️ A ] [ ♥️ 10 ]");
    }
  }
}
