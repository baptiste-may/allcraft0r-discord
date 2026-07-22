package fr.may_baptiste.allcraft0r_discord.commands.game.cards;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Suit {
  SPADE("♠️", "♠"),
  HEART("♥️", "♥"),
  DIAMOND("♦️", "♦"),
  CLUB("♣️", "♣");

  private final String emoji;
  private final String symbol;
}
