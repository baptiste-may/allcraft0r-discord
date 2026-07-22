package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Hand {
  private final List<Card> cards = new ArrayList<>();

  public void addCard(Card card) {
    cards.add(card);
  }

  public int size() {
    return cards.size();
  }

  @Override
  public String toString() {
    return cards.stream().map(Card::toString).collect(Collectors.joining(" "));
  }
}
