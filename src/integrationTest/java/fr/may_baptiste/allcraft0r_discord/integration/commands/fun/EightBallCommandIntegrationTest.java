package fr.may_baptiste.allcraft0r_discord.integration.commands.fun;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.fun.EightBallCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EightBallCommandIntegrationTest extends AbstractIntegration {

  @Autowired EightBallCommand eightBallCommand;

  @Nested
  class Reply {

    @Test
    @DisplayName("should include question in embed")
    void shouldIncludeQuestionInEmbed() {
      final var event = buildEvent("8ball", "user-1");
      stubStringOption(event, "question", "Will this work?");

      eightBallCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getFields())
          .extracting(f -> f.getName())
          .anyMatch(name -> name.contains("Will this work?"));
    }

    @Test
    @DisplayName("should include a response in embed")
    void shouldIncludeAResponseInEmbed() {
      final var event = buildEvent("8ball", "user-1");
      stubStringOption(event, "question", "Any question?");

      eightBallCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getFields())
          .extracting(f -> f.getValue())
          .anyMatch(value -> eightBallCommand.getResponses().contains(value));
    }
  }
}
