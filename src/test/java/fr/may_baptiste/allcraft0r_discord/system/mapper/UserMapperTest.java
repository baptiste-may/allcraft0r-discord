package fr.may_baptiste.allcraft0r_discord.system.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  @Nested
  class EntityToDTOMapping {

    @Test
    @DisplayName("should map UserEntity to UserMoneyDTO correctly")
    void shouldMapUserEntityToDTO() {
      final var entity = new UserEntity();
      entity.setId("123456789");
      entity.setMoney(500L);

      final var dto = userMapper.toUserMoneyDTO(entity);

      assertThat(dto).isNotNull();
      assertThat(dto.id()).isEqualTo("123456789");
      assertThat(dto.money()).isEqualTo(500L);
    }

    @Test
    @DisplayName("should return null when mapping null entity")
    void shouldReturnNullForNullEntity() {
      final var dto = userMapper.toUserMoneyDTO(null);

      assertThat(dto).isNull();
    }
  }
}
