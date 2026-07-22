package fr.may_baptiste.allcraft0r_discord.system.exception;

public class AdminChannelIdInvalidException extends Exception {
  private static final long serialVersionUID = 1L;

  public AdminChannelIdInvalidException() {
    super("Admin channel ID is invalid");
  }
}
