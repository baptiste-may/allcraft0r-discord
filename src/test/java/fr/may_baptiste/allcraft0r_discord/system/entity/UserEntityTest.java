package fr.may_baptiste.allcraft0r_discord.system.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserEntityTest {

  @Nested
  class EntityProperties {

    @Test
    @DisplayName("should set and get properties correctly")
    void shouldSetAndGetProperties() {
      final var entity = new UserEntity();
      final var now = LocalDateTime.now();

      entity.setId("user-1");
      entity.setMoney(1000L);
      entity.setLastDaily(now);

      assertThat(entity.getId()).isEqualTo("user-1");
      assertThat(entity.getMoney()).isEqualTo(1000L);
      assertThat(entity.getLastDaily()).isEqualTo(now);
    }

    @Test
    @DisplayName("should implement equals, hashCode and toString correctly")
    void shouldSupportEqualsAndHashCode() {
      final var entity1 = new UserEntity();
      entity1.setId("user-1");
      entity1.setMoney(100L);

      final var entity2 = new UserEntity();
      entity2.setId("user-1");
      entity2.setMoney(100L);

      assertThat(entity1).isEqualTo(entity2);
      assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
      assertThat(entity1.toString()).contains("user-1");
    }
  }
}
