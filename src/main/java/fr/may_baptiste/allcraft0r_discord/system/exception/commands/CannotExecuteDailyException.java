package fr.may_baptiste.allcraft0r_discord.system.exception.commands;

import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CannotExecuteDailyException extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  private final LocalDateTime nextAvailableDaily;
}
