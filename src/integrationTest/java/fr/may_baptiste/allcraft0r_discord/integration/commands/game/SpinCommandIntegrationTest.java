package fr.may_baptiste.allcraft0r_discord.integration.commands.game;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.game.SpinCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SpinCommandIntegrationTest extends AbstractIntegration {

  @Autowired SpinCommand spinCommand;
  @Autowired MoneyService moneyService;

  @Nested
  class InsufficientFunds {

    @Test
    @DisplayName("should reject bet and reply with error text")
    void shouldRejectBetAndReplyWithErrorText() {
      final var event = buildEvent("spin", "user-1");
      stubLongOption(event, "mise", 99999L);

      spinCommand.onCommandExecution(event);

      assertThat(captureReplyText(event)).contains("❌");
    }

    @Test
    @DisplayName("should not deduct money from user")
    void shouldNotDeductMoneyFromUser() {
      final var event = buildEvent("spin", "user-1");
      stubLongOption(event, "mise", 99999L);

      spinCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("user-1")).isEqualTo(MoneyService.DEFAULT_MONEY);
    }
  }

  @Nested
  class SufficientFunds {

    @Test
    @DisplayName("should deduct bet from balance")
    void shouldDeductBetFromBalance() {
      UserEntity user = new UserEntity();
      user.setId("user-1");
      user.setMoney(500);
      userRepository.save(user);

      long bet = SpinCommand.MINIMAL_BET;
      final var event = buildEvent("spin", "user-1");
      stubLongOption(event, "mise", bet);

      spinCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("user-1")).isEqualTo(500 - bet);
    }

    @Test
    @DisplayName("should start spin game")
    void shouldStartTheSpinGame() {
      final var event = buildEvent("spin", "user-1");
      stubLongOption(event, "mise", SpinCommand.MINIMAL_BET);

      spinCommand.onCommandExecution(event);

      assertThat(captureReplyEmbedSingle(event).getTitle()).contains("🎰");
    }
  }
}
