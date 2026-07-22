package fr.may_baptiste.allcraft0r_discord.system.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserMoneyDTOTest {

  @Nested
  class DTOProperties {

    @Test
    @DisplayName("should hold id and money fields")
    void shouldHoldFields() {
      final var dto = new UserMoneyDTO("user-100", 2500L);

      assertThat(dto.id()).isEqualTo("user-100");
      assertThat(dto.money()).isEqualTo(2500L);
    }
  }
}
