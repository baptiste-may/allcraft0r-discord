package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import org.jspecify.annotations.NonNull;

public record Card(Suit suit, Rank rank) {
  @Override
  public @NonNull String toString() {
    return "[ %s %s ]".formatted(suit.getEmoji(), rank.getSymbol());
  }
}
