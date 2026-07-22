package fr.may_baptiste.allcraft0r_discord.system.exception;

import java.io.Serial;

public class CommandExecutionException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public CommandExecutionException(String message) {
    super(message);
  }
}
