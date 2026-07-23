package fr.may_baptiste.allcraft0r_discord.integration.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.may_baptiste.allcraft0r_discord.commands.utils.PingCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PingCommandIntegrationTest extends AbstractIntegration {

  @Autowired PingCommand pingCommand;

  @Nested
  class Reply {

    @Test
    @DisplayName("should include gateway ping in description")
    void shouldIncludeGatewayPingInDescription() {
      when(jda.getGatewayPing()).thenReturn(42L);
      final var event = buildEvent("ping", "user-1");

      pingCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getDescription()).contains("42");
    }
  }
}
