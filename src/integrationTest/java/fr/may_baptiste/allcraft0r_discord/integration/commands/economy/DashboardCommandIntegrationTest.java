package fr.may_baptiste.allcraft0r_discord.integration.commands.economy;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.economy.DashboardCommand;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import java.awt.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DashboardCommandIntegrationTest extends AbstractIntegration {

  @Autowired DashboardCommand dashboardCommand;

  @Nested
  class EmptyLeaderboard {

    @Test
    @DisplayName("should reply with red embed")
    void shouldReplyWithRedEmbed() {
      final var event = buildEvent("dashboard", "user-1");

      dashboardCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getColor()).isEqualTo(Color.RED);
    }

    @Test
    @DisplayName("should have empty or null description")
    void shouldHaveEmptyDescription() {
      final var event = buildEvent("dashboard", "user-1");

      dashboardCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getDescription()).isNullOrEmpty();
    }
  }

  @Nested
  class WithUsers {

    @BeforeEach
    void seedUsers() {
      UserEntity u1 = new UserEntity();
      u1.setId("rich-user");
      u1.setMoney(1000);
      userRepository.save(u1);

      UserEntity u2 = new UserEntity();
      u2.setId("poor-user");
      u2.setMoney(100);
      userRepository.save(u2);
    }

    @Test
    @DisplayName("should display users in description")
    void shouldDisplayUsersInDescription() {
      final var event = buildEvent("dashboard", "rich-user");

      dashboardCommand.onCommandExecution(event);

      String description = captureReplyEmbed(event).getDescription();
      assertThat(description).contains("<@rich-user>").contains("<@poor-user>");
    }

    @Test
    @DisplayName("should display users in descending order of money")
    void shouldDisplayUsersInDescendingMoneyOrder() {
      final var event = buildEvent("dashboard", "rich-user");

      dashboardCommand.onCommandExecution(event);

      final var description = captureReplyEmbed(event).getDescription();
      assertThat(description).isNotNull();
      assertThat(description.indexOf("<@rich-user>"))
          .isLessThan(description.indexOf("<@poor-user>"));
    }
  }
}
