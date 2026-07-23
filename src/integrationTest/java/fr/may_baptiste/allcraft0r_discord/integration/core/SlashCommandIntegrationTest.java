package fr.may_baptiste.allcraft0r_discord.integration.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.commands.economy.DailyCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.exception.CommandExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class SlashCommandIntegrationTest extends AbstractIntegration {

  @MockitoSpyBean DailyCommand dailyCommand;

  @Nested
  class Routing {

    @Test
    @DisplayName("should ignore event when command name does not match")
    void shouldIgnoreEventWithDifferentCommandName() {
      final var event = buildEvent("other", "user-1");

      dailyCommand.onSlashCommandInteraction(event);

      verify(event, never()).replyEmbeds(any());
      verify(event, never()).reply(anyString());
    }

    @Test
    @DisplayName("should execute command when command name matches")
    void shouldExecuteCommandWhenNameMatches() {
      final var event = buildEvent("daily", "user-1");

      dailyCommand.onSlashCommandInteraction(event);

      verify(event).replyEmbeds(any());
    }
  }

  @Nested
  class ExceptionHandling {

    @Test
    @DisplayName("should reply with error message when CommandExecutionException is thrown")
    void shouldReplyWithErrorMessageOnCommandExecutionException() {
      final var event = buildEvent("daily", "user-1");
      doThrow(new CommandExecutionException("something went wrong"))
          .when(dailyCommand)
          .onCommandExecution(any());

      dailyCommand.onSlashCommandInteraction(event);

      assertThat(captureReplyText(event)).contains("something went wrong");
    }
  }
}
