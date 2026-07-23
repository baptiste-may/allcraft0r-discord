package fr.may_baptiste.allcraft0r_discord.integration.commands.economy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import fr.may_baptiste.allcraft0r_discord.commands.economy.DailyCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

class DailyCommandIntegrationTest extends AbstractIntegration {

  @Autowired DailyCommand dailyCommand;
  @Autowired MoneyService moneyService;

  private static final String USER_ID = "test-user-42";

  @Nested
  class FirstTimeClaim {

    @Test
    @DisplayName("should create user and add daily money to balance")
    void shouldCreateUserAndAddDailyMoney() {
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      UserEntity saved = userRepository.findById(USER_ID).orElseThrow();
      assertThat(saved.getMoney()).isEqualTo(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);
      assertThat(saved.getLastDaily()).isNotNull();
    }

    @Test
    @DisplayName("should reply with green embed")
    void shouldReplyWithGreenEmbed() {
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      MessageEmbed embed = captureReplyEmbed(event);
      assertThat(embed.getColor()).isEqualTo(Color.GREEN);
      assertThat(embed.getTitle()).contains("Tu as reçu");
      assertThat(embed.getTitle()).contains("Tu as maintenant");
    }
  }

  @Nested
  class AlreadyClaimed {

    @BeforeEach
    void givenUserAlreadyClaimedToday() {
      UserEntity user = new UserEntity();
      user.setId(USER_ID);
      user.setMoney(MoneyService.DEFAULT_MONEY + MoneyService.DAILY_MONEY);
      user.setLastDaily(LocalDateTime.now());
      userRepository.save(user);
    }

    @Test
    @DisplayName("should not change balance")
    void shouldNotChangeBalance() {
      long balanceBefore = moneyService.getMoney(USER_ID);
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney(USER_ID)).isEqualTo(balanceBefore);
    }

    @Test
    @DisplayName("should reply with red ephemeral embed")
    void shouldReplyWithRedEphemeralEmbed() {
      final var event = buildEvent("daily", USER_ID);
      ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
      when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);
      doNothing().when(replyAction).queue();

      ArgumentCaptor<List<MessageEmbed>> embedCaptor = ArgumentCaptor.captor();
      when(event.replyEmbeds(embedCaptor.capture())).thenReturn(replyAction);

      dailyCommand.onCommandExecution(event);

      MessageEmbed embed = embedCaptor.getValue().getFirst();
      assertThat(embed.getColor()).isEqualTo(Color.RED);
      assertThat(embed.getTitle()).contains("Tu as déjà récupéré ta redstone quotidienne");
      assertThat(embed.getTitle()).contains("<t:");
      verify(replyAction).setEphemeral(true);
    }
  }

  @Nested
  class ClaimedYesterday {

    @BeforeEach
    void givenUserClaimedYesterday() {
      UserEntity user = new UserEntity();
      user.setId(USER_ID);
      user.setMoney(MoneyService.DEFAULT_MONEY);
      user.setLastDaily(LocalDateTime.now().minusDays(1).minusSeconds(1));
      userRepository.save(user);
    }

    @Test
    @DisplayName("should add daily money to existing balance")
    void shouldAddDailyMoneyToExistingBalance() {
      long balanceBefore = moneyService.getMoney(USER_ID);
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney(USER_ID))
          .isEqualTo(balanceBefore + MoneyService.DAILY_MONEY);
    }

    @Test
    @DisplayName("should update lastDaily timestamp")
    void shouldUpdateLastDailyTimestamp() {
      LocalDateTime dailyBefore = userRepository.findById(USER_ID).orElseThrow().getLastDaily();
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      LocalDateTime dailyAfter = userRepository.findById(USER_ID).orElseThrow().getLastDaily();
      assertThat(dailyAfter).isAfter(dailyBefore);
    }

    @Test
    @DisplayName("should reply with green embed")
    void shouldReplyWithGreenEmbed() {
      final var event = buildEvent("daily", USER_ID);

      dailyCommand.onCommandExecution(event);

      MessageEmbed embed = captureReplyEmbed(event);
      assertThat(embed.getColor()).isEqualTo(Color.GREEN);
      assertThat(embed.getTitle()).contains("Tu as reçu");
      assertThat(embed.getTitle()).contains("Tu as maintenant");
    }
  }
}
