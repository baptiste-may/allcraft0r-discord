package fr.may_baptiste.allcraft0r_discord.system.exception;

import lombok.Getter;

@Getter
public class AdminChannelIdInvalidException extends Exception {
  private static final long serialVersionUID = 1L;

  private final long channelId;

  public AdminChannelIdInvalidException(long channelId) {
    super("Admin channel ID is invalid");
    this.channelId = channelId;
  }

  public AdminChannelIdInvalidException() {
    this(0L);
  }
}
