package fr.may_baptiste.allcraft0r_discord.system.exception;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.system.exception.commands.CannotExecuteDailyException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DomainExceptionsTest {

  @Nested
  class ExceptionConstructors {

    @Test
    @DisplayName("should store nextAvailableDaily in CannotExecuteDailyException")
    void shouldStoreNextAvailableDaily() {
      final var nextTime = LocalDateTime.now().plusHours(12);
      final var ex = new CannotExecuteDailyException(nextTime);

      assertThat(ex.getNextAvailableDaily()).isEqualTo(nextTime);
    }

    @Test
    @DisplayName("should set message in CommandExecutionException")
    void shouldSetCommandExecutionExceptionMessage() {
      final var ex = new CommandExecutionException("Test error message");

      assertThat(ex.getMessage()).isEqualTo("Test error message");
    }

    @Test
    @DisplayName("should instantiate GuildIdInvalidException and AdminChannelIdInvalidException")
    void shouldInstantiateConfigExceptions() {
      final var guildEx = new GuildIdInvalidException();
      final var adminEx = new AdminChannelIdInvalidException();

      assertThat(guildEx).isNotNull();
      assertThat(adminEx).isNotNull();
    }
  }
}
