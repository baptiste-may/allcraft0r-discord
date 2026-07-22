package fr.may_baptiste.allcraft0r_discord.core;

import java.awt.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommandType {
  ECONOMY("Commandes economiques", new Color(15105570)),
  FUN("Commandes funs", new Color(16776960)),
  GAME("Commandes de jeu", new Color(6463722)),
  UTILS("Commandes utilitaires", new Color(5763719));

  private final String description;
  private final Color color;
}
