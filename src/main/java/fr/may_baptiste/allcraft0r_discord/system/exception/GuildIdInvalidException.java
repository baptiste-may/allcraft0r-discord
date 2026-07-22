package fr.may_baptiste.allcraft0r_discord.system.exception;

public class GuildIdInvalidException extends Exception {
  private static final long serialVersionUID = 1L;

  public GuildIdInvalidException() {
    super("Guild ID is invalid");
  }
}
