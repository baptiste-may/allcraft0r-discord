package fr.may_baptiste.allcraft0r_discord.system.exception;

public class CommandExecutionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CommandExecutionException(String message) {
    super(message);
  }
}
