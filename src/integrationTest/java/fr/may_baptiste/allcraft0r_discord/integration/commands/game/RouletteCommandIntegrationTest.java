package fr.may_baptiste.allcraft0r_discord.integration.commands.game;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.game.RouletteCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RouletteCommandIntegrationTest extends AbstractIntegration {

  @Autowired RouletteCommand rouletteCommand;
  @Autowired MoneyService moneyService;

  @Nested
  class InsufficientFunds {

    @Test
    @DisplayName("should reject bet and reply with error text")
    void shouldRejectBetAndReplyWithErrorText() {
      final var event = buildEvent("roulette", "123456789");
      stubStringOption(event, "category", "red");
      stubLongOption(event, "mise", 99999L);

      rouletteCommand.onCommandExecution(event);

      assertThat(captureReplyText(event)).contains("❌");
    }

    @Test
    @DisplayName("should not deduct money from user")
    void shouldNotDeductMoneyFromUser() {
      final var event = buildEvent("roulette", "123456789");
      stubStringOption(event, "category", "red");
      stubLongOption(event, "mise", 99999L);

      rouletteCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("123456789")).isEqualTo(MoneyService.DEFAULT_MONEY);
    }
  }

  @Nested
  class SufficientFunds {

    @Test
    @DisplayName("should deduct bet from balance when starting roulette game")
    void shouldDeductBetFromBalance() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(500);
      userRepository.save(user);

      long bet = RouletteCommand.MINIMAL_BET;
      final var event = buildEvent("roulette", "123456789");
      stubStringOption(event, "category", "red");
      stubLongOption(event, "mise", bet);

      rouletteCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("123456789")).isEqualTo(500 - bet);
    }

    @Test
    @DisplayName("should start roulette game and reply with initial embed")
    void shouldStartRouletteGame() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(500);
      userRepository.save(user);

      final var event = buildEvent("roulette", "123456789");
      stubStringOption(event, "category", "red");
      stubLongOption(event, "mise", RouletteCommand.MINIMAL_BET);

      rouletteCommand.onCommandExecution(event);

      assertThat(captureReplyEmbedSingle(event).getTitle()).contains("🪩 Les jeux sont faits ! 🪩");
    }
  }
}
