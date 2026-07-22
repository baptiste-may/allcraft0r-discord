package fr.may_baptiste.allcraft0r_discord.integration.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import fr.may_baptiste.allcraft0r_discord.commands.utils.HelpCommand;
import fr.may_baptiste.allcraft0r_discord.core.CommandType;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HelpCommandIntegrationTest extends AbstractIntegration {

  @Autowired HelpCommand helpCommand;

  @Nested
  class EconomyType {

    @Test
    @DisplayName("should reply with economy title")
    void shouldReplyWithEconomyTitle() {
      final var event = buildEvent("help", "user-1");
      stubStringOption(event, "type", CommandType.ECONOMY.name());

      helpCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getTitle())
          .isEqualTo(CommandType.ECONOMY.getDescription());
    }

    @Test
    @DisplayName("should list economy commands")
    void shouldListEconomyCommands() {
      final var event = buildEvent("help", "user-1");
      stubStringOption(event, "type", CommandType.ECONOMY.name());

      helpCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getFields()).isNotEmpty();
    }
  }

  @Nested
  class FunType {

    @Test
    @DisplayName("should reply with fun title")
    void shouldReplyWithFunTitle() {
      final var event = buildEvent("help", "user-1");
      stubStringOption(event, "type", CommandType.FUN.name());

      helpCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getTitle()).isEqualTo(CommandType.FUN.getDescription());
    }
  }

  @Nested
  class UtilsType {

    @Test
    @DisplayName("should hide admin commands from help listing")
    void shouldHideAdminCommandsFromHelpListing() {
      final var event = buildEvent("help", "user-1");
      stubStringOption(event, "type", CommandType.UTILS.name());

      helpCommand.onCommandExecution(event);

      final var commandNames =
          captureReplyEmbed(event).getFields().stream().map(MessageEmbed.Field::getName).toList();

      assertThat(commandNames).noneMatch(name -> name.contains("/lock"));
      assertThat(commandNames).noneMatch(name -> name.contains("/unlock"));
    }
  }

  @Nested
  class AdminType {

    @Test
    @DisplayName("should not include admin type in command options choices")
    void shouldNotIncludeAdminTypeInCommandOptionsChoices() {
      final var choices = helpCommand.getCommandData().getOptions().get(0).getChoices();
      final var choiceNames =
          choices.stream()
              .map(net.dv8tion.jda.api.interactions.commands.Command.Choice::getName)
              .toList();

      assertThat(choiceNames).doesNotContain(CommandType.ADMIN.getDescription());
    }

    @Test
    @DisplayName("should return empty fields when admin type is passed")
    void shouldReturnEmptyFieldsWhenAdminTypeIsPassed() {
      final var event = buildEvent("help", "user-1");
      stubStringOption(event, "type", CommandType.ADMIN.name());

      helpCommand.onCommandExecution(event);

      assertThat(captureReplyEmbed(event).getFields()).isEmpty();
    }
  }
}
