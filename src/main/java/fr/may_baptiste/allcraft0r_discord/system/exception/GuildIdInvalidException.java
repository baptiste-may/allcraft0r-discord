package fr.may_baptiste.allcraft0r_discord.system.exception;

import lombok.Getter;

@Getter
public class GuildIdInvalidException extends Exception {
  private static final long serialVersionUID = 1L;

  private final long guildId;

  public GuildIdInvalidException(long guildId) {
    super("Guild ID is invalid");
    this.guildId = guildId;
  }

  public GuildIdInvalidException() {
    this(0L);
  }
}
