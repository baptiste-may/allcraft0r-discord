package fr.may_baptiste.allcraft0r_discord.integration.commands.fun;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.fun.EyesCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EyesCommandIntegrationTest extends AbstractIntegration {
  @Autowired EyesCommand eyesCommand;

  @Test
  @DisplayName("should reply with eyes emoji")
  void shouldReplyWithEyesEmoji() {
    final var event = buildEvent("eyes", "user-1");

    eyesCommand.onCommandExecution(event);

    assertThat(captureReplyText(event)).contains("\uD83D\uDC41\uD83D\uDC44\uD83D\uDC41");
  }
}
