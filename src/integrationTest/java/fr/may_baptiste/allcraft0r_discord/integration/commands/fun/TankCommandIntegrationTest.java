package fr.may_baptiste.allcraft0r_discord.integration.commands.fun;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.fun.TankCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TankCommandIntegrationTest extends AbstractIntegration {

  @Autowired TankCommand tankCommand;

  @Nested
  class Reply {

    @Test
    @DisplayName("should reply with Tenor GIF link")
    void shouldReplyWithTenorLink() {
      final var event = buildEvent("tank", "user-1");

      tankCommand.onCommandExecution(event);

      assertThat(captureReplyText(event)).contains("tenor.com");
    }
  }
}
