package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Deck {
  private final List<Card> cards = new ArrayList<>();

  public Deck() {
    for (final var suit : Suit.values()) {
      for (final var rank : Rank.values()) {
        cards.add(new Card(suit, rank));
      }
    }
  }

  public void shuffle() {
    Collections.shuffle(cards, ThreadLocalRandom.current());
  }

  public Card draw() {
    if (cards.isEmpty()) {
      throw new IllegalStateException("Deck is empty");
    }
    return cards.removeFirst();
  }

  public void deal(Hand hand, int count) {
    for (int i = 0; i < count; i++) {
      hand.addCard(draw());
    }
  }
}
