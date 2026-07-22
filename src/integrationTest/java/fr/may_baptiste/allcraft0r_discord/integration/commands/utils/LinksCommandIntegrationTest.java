package fr.may_baptiste.allcraft0r_discord.integration.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.utils.LinksCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LinksCommandIntegrationTest extends AbstractIntegration {

  @Autowired LinksCommand linksCommand;

  @Nested
  class Reply {

    @Test
    @DisplayName("should reply with links message")
    void shouldReplyWithLinksMessage() {
      final var event = buildEvent("links", "user-1");

      linksCommand.onCommandExecution(event);

      assertThat(captureReplyText(event)).isNotBlank();
    }

    @Test
    @DisplayName("should include YouTube links in buttons")
    void shouldIncludeYoutubeInReply() {
      final var event = buildEvent("links", "user-1");

      linksCommand.onCommandExecution(event);

      assertThat(linksCommand.getYtBase().getUrl()).contains("youtube.com");
      assertThat(linksCommand.getYtBestOf().getUrl()).contains("youtube.com");
    }
  }
}
