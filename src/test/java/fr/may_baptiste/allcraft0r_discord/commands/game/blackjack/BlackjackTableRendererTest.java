package fr.may_baptiste.allcraft0r_discord.commands.game.blackjack;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Card;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Hand;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Rank;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BlackjackTableRendererTest {

  @Nested
  class TableRendering {

    @Test
    @DisplayName("should render PNG image during ongoing game")
    void shouldRenderOngoingGame() {
      final var playerHand = new Hand();
      playerHand.addCard(new Card(Suit.SPADE, Rank.ACE));
      playerHand.addCard(new Card(Suit.HEART, Rank.TEN));

      final var masterHand = new Hand();
      masterHand.addCard(new Card(Suit.DIAMOND, Rank.FIVE));

      final var imageBytes =
          BlackjackTableRenderer.renderTable(
              playerHand, masterHand, 21, 5, "TestUser", 100L, null, false);

      assertThat(imageBytes).isNotNull().isNotEmpty();
      assertThat(imageBytes.length).isGreaterThan(1000);
    }

    @Test
    @DisplayName("should render table with win result banner")
    void shouldRenderWinBanner() {
      final var playerHand = new Hand();
      playerHand.addCard(new Card(Suit.SPADE, Rank.ACE));
      playerHand.addCard(new Card(Suit.HEART, Rank.KING));

      final var masterHand = new Hand();
      masterHand.addCard(new Card(Suit.CLUB, Rank.TEN));
      masterHand.addCard(new Card(Suit.HEART, Rank.SEVEN));

      final var imageBytes =
          BlackjackTableRenderer.renderTable(
              playerHand, masterHand, 21, 17, "TestUser", 100L, 200L, true);

      assertThat(imageBytes).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("should render table with blackjack result banner")
    void shouldRenderBlackjackBanner() {
      final var playerHand = new Hand();
      playerHand.addCard(new Card(Suit.SPADE, Rank.ACE));
      playerHand.addCard(new Card(Suit.HEART, Rank.KING));

      final var masterHand = new Hand();
      masterHand.addCard(new Card(Suit.CLUB, Rank.TEN));
      masterHand.addCard(new Card(Suit.HEART, Rank.SEVEN));

      final var imageBytes =
          BlackjackTableRenderer.renderTable(
              playerHand, masterHand, 21, 17, "TestUser", 100L, 250L, true);

      assertThat(imageBytes).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("should render table with push result banner")
    void shouldRenderPushBanner() {
      final var playerHand = new Hand();
      playerHand.addCard(new Card(Suit.SPADE, Rank.TEN));
      playerHand.addCard(new Card(Suit.HEART, Rank.EIGHT));

      final var masterHand = new Hand();
      masterHand.addCard(new Card(Suit.CLUB, Rank.NINE));
      masterHand.addCard(new Card(Suit.HEART, Rank.NINE));

      final var imageBytes =
          BlackjackTableRenderer.renderTable(
              playerHand, masterHand, 18, 18, "TestUser", 100L, 100L, true);

      assertThat(imageBytes).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("should render table with bust result banner")
    void shouldRenderBustBanner() {
      final var playerHand = new Hand();
      playerHand.addCard(new Card(Suit.SPADE, Rank.KING));
      playerHand.addCard(new Card(Suit.HEART, Rank.QUEEN));
      playerHand.addCard(new Card(Suit.CLUB, Rank.FIVE));

      final var masterHand = new Hand();
      masterHand.addCard(new Card(Suit.CLUB, Rank.TEN));

      final var imageBytes =
          BlackjackTableRenderer.renderTable(
              playerHand, masterHand, 25, 10, "TestUser", 100L, 0L, true);

      assertThat(imageBytes).isNotNull().isNotEmpty();
    }
  }
}
