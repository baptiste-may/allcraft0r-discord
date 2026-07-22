package fr.may_baptiste.allcraft0r_discord.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommandTypeTest {

  @Nested
  class EnumValues {

    @Test
    @DisplayName("should contain all expected command types with names and colors")
    void shouldContainExpectedTypes() {
      final var types = CommandType.values();
      assertThat(types)
          .containsExactlyInAnyOrder(
              CommandType.ECONOMY, CommandType.FUN, CommandType.GAME, CommandType.UTILS);

      for (final var type : types) {
        assertThat(type.getDescription()).isNotBlank();
        assertThat(type.getColor()).isNotNull();
      }
    }
  }
}
