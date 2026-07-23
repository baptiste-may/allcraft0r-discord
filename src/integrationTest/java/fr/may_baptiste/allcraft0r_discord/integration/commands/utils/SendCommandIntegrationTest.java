package fr.may_baptiste.allcraft0r_discord.integration.commands.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import fr.may_baptiste.allcraft0r_discord.commands.utils.SendCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SendCommandIntegrationTest extends AbstractIntegration {

  @Autowired SendCommand sendCommand;

  @Nested
  class SendToAdminChannel {

    @Test
    @DisplayName("should forward message to admin channel")
    void shouldForwardMessageToAdminChannel() {
      TextChannel adminChannel = mockAdminChannel();
      final var event = buildEvent("send", "user-1");
      stubStringOption(event, "message", "Hello admins!");

      sendCommand.onCommandExecution(event);

      verify(adminChannel).sendMessageEmbeds(any(MessageEmbed.class), any(MessageEmbed[].class));
    }
  }
}
