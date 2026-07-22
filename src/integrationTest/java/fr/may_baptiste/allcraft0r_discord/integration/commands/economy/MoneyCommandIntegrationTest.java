package fr.may_baptiste.allcraft0r_discord.integration.commands.economy;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.economy.MoneyCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MoneyCommandIntegrationTest extends AbstractIntegration {

  @Autowired MoneyCommand moneyCommand;

  @Nested
  class NewUser {

    @Test
    @DisplayName("should display default balance")
    void shouldDisplayDefaultBalance() {
      final var event = buildEvent("money", "user-1");

      moneyCommand.onCommandExecution(event);

      assertThat(captureReplyEmbedSingle(event).getTitle())
          .contains(String.valueOf(MoneyService.DEFAULT_MONEY));
    }
  }

  @Nested
  class ExistingUser {

    @Test
    @DisplayName("should display current balance")
    void shouldDisplayCurrentBalance() {
      UserEntity user = new UserEntity();
      user.setId("user-1");
      user.setMoney(9999);
      userRepository.save(user);

      final var event = buildEvent("money", "user-1");

      moneyCommand.onCommandExecution(event);

      assertThat(captureReplyEmbedSingle(event).getTitle()).contains("9999");
    }
  }
}
