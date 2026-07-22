package fr.may_baptiste.allcraft0r_discord.core;

import java.awt.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommandType {
  ADMIN("Commandes d'administration", new Color(15158332)),
  ECONOMY("Commandes économiques", new Color(15105570)),
  FUN("Commandes fun", new Color(16776960)),
  GAME("Commandes de jeu", new Color(6463722)),
  UTILS("Commandes utilitaires", new Color(5763719));

  private final String description;
  private final Color color;
}
