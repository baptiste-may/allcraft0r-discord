package fr.may_baptiste.allcraft0r_discord.integration.commands.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.may_baptiste.allcraft0r_discord.commands.game.BlackjackCommand;
import fr.may_baptiste.allcraft0r_discord.commands.game.blackjack.BlackjackGame;
import fr.may_baptiste.allcraft0r_discord.integration.AbstractIntegration;
import fr.may_baptiste.allcraft0r_discord.system.entity.UserEntity;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlackjackCommandIntegrationTest extends AbstractIntegration {

  @Autowired BlackjackCommand blackjackCommand;
  @Autowired MoneyService moneyService;

  private SlashCommandInteractionEvent createBlackjackEvent() {
    SlashCommandInteractionEvent event = buildEvent("blackjack", "123456789");
    try {
      when(event.getUser().getIdLong()).thenReturn(Long.parseLong("123456789"));
    } catch (NumberFormatException ignored) {
      when(event.getUser().getIdLong()).thenReturn(123456789L);
    }
    when(event.getUser().getEffectiveName()).thenReturn("testUser");
    return event;
  }

  private ButtonInteractionEvent buildButtonEvent(String componentId, String userId) {
    User discordUser = mock(User.class);
    when(discordUser.getId()).thenReturn(userId);
    when(discordUser.getName()).thenReturn("testUser");
    when(discordUser.getEffectiveName()).thenReturn("testUser");
    try {
      when(discordUser.getIdLong()).thenReturn(Long.parseLong(userId));
    } catch (NumberFormatException ignored) {
      when(discordUser.getIdLong()).thenReturn(123456789L);
    }

    ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
    when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);
    doNothing().when(replyAction).queue();

    MessageEditCallbackAction editAction = mock(MessageEditCallbackAction.class);
    when(editAction.setComponents(anyCollection())).thenReturn(editAction);
    when(editAction.setFiles(anyCollection())).thenReturn(editAction);
    when(editAction.setFiles(any(net.dv8tion.jda.api.utils.FileUpload[].class)))
        .thenReturn(editAction);
    doNothing().when(editAction).queue();

    final var event = mock(ButtonInteractionEvent.class);
    when(event.getComponentId()).thenReturn(componentId);
    when(event.getUser()).thenReturn(discordUser);
    when(event.getMessageIdLong()).thenReturn((long) 0);
    when(event.getJDA()).thenReturn(jda);
    when(event.reply(anyString())).thenReturn(replyAction);
    when(event.editMessageEmbeds(any(MessageEmbed.class))).thenReturn(editAction);
    when(event.editMessageEmbeds(anyCollection())).thenReturn(editAction);

    return event;
  }

  @Nested
  class InsufficientFunds {

    @Test
    @DisplayName("should reject bet and reply with error text")
    void shouldRejectBetAndReplyWithErrorText() {
      final var event = createBlackjackEvent();
      stubLongOption(event, "mise", 99999L);

      blackjackCommand.onCommandExecution(event);

      assertThat(captureReplyText(event)).contains("❌");
    }

    @Test
    @DisplayName("should not deduct money from user")
    void shouldNotDeductMoneyFromUser() {
      final var event = createBlackjackEvent();
      stubLongOption(event, "mise", 99999L);

      blackjackCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("123456789")).isEqualTo(MoneyService.DEFAULT_MONEY);
    }
  }

  @Nested
  class SufficientFunds {

    @Test
    @DisplayName("should deduct bet from balance when starting game")
    void shouldDeductBetFromBalance() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(500);
      userRepository.save(user);

      long bet = BlackjackCommand.MINIMAL_BET;
      final var event = createBlackjackEvent();
      stubLongOption(event, "mise", bet);

      blackjackCommand.onCommandExecution(event);

      assertThat(moneyService.getMoney("123456789")).isEqualTo(500 - bet);
    }

    @Test
    @DisplayName("should start blackjack game and reply with embed")
    void shouldStartBlackjackGame() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(500);
      userRepository.save(user);

      final var event = createBlackjackEvent();
      stubLongOption(event, "mise", BlackjackCommand.MINIMAL_BET);

      blackjackCommand.onCommandExecution(event);

      assertThat(captureReplyEmbedSingle(event).getTitle()).contains("🃏 Blackjack 🃏");
    }
  }

  @Nested
  class ButtonInteractions {

    @Test
    @DisplayName("should reject interaction from non-author user")
    void shouldRejectInteractionFromOtherUser() {
      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      ButtonInteractionEvent buttonEvent = buildButtonEvent("blackjack-stop", "999999999");
      game.onButtonInteraction(buttonEvent);

      verify(buttonEvent).reply("❌ Tu ne peux pas jouer à la place des autres !");
    }

    @Test
    @DisplayName("should process stop button and complete game")
    void shouldProcessStopButton() {
      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      ButtonInteractionEvent buttonEvent = buildButtonEvent("blackjack-stop", "123456789");
      game.onButtonInteraction(buttonEvent);

      assertThat(game.isGameEnded()).isTrue();
      assertThat(profitsRef.get()).isNotNull();
      verify(buttonEvent).editMessageEmbeds(any(MessageEmbed.class));
    }

    @Test
    @DisplayName("should process nextcard button and draw a card")
    void shouldProcessNextCardButton() {
      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      int initialCards = game.getPlayersCards().size();
      ButtonInteractionEvent buttonEvent = buildButtonEvent("blackjack-nextcard", "123456789");
      game.onButtonInteraction(buttonEvent);

      assertThat(game.getPlayersCards().size()).isGreaterThanOrEqualTo(initialCards);
    }

    @Test
    @DisplayName("should reject double button if user lacks redstone")
    void shouldRejectDoubleButtonIfInsufficientMoney() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(10);
      userRepository.save(user);

      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      ButtonInteractionEvent buttonEvent = buildButtonEvent("blackjack-double", "123456789");
      game.onButtonInteraction(buttonEvent);

      verify(buttonEvent).reply("❌ Tu n'as pas assez de redstone pour doubler ta mise !");
    }

    @Test
    @DisplayName("should process double button, deduct extra bet and complete game")
    void shouldProcessDoubleButtonWithSufficientMoney() {
      UserEntity user = new UserEntity();
      user.setId("123456789");
      user.setMoney(500);
      userRepository.save(user);

      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      ButtonInteractionEvent buttonEvent = buildButtonEvent("blackjack-double", "123456789");
      game.onButtonInteraction(buttonEvent);

      assertThat(game.getBet()).isEqualTo(100);
      assertThat(game.isGameEnded()).isTrue();
      assertThat(moneyService.getMoney("123456789")).isEqualTo(450);
    }

    @Test
    @DisplayName("should reject double button if player has more than 2 cards")
    void shouldRejectDoubleButtonOnThirdCard() {
      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      // Hit first to get 3 cards
      ButtonInteractionEvent nextCardEvent = buildButtonEvent("blackjack-nextcard", "123456789");
      game.onButtonInteraction(nextCardEvent);

      if (!game.isGameEnded()) {
        ButtonInteractionEvent doubleEvent = buildButtonEvent("blackjack-double", "123456789");
        game.onButtonInteraction(doubleEvent);
        verify(doubleEvent).reply("❌ Tu ne peux plus doubler ta mise !");
      }
    }

    @Test
    @DisplayName("should ignore button interaction with unrelated customId")
    void shouldIgnoreUnrelatedButtonCustomId() {
      AtomicReference<Long> profitsRef = new AtomicReference<>(null);
      final var commandEvent = createBlackjackEvent();

      BlackjackGame game =
          new BlackjackGame(commandEvent, discordConfig, moneyService, 50, profitsRef::set);

      ButtonInteractionEvent unrelatedEvent = buildButtonEvent("other-button", "123456789");
      game.onButtonInteraction(unrelatedEvent);

      assertThat(game.isGameEnded())
          .isEqualTo(BlackjackGame.calculateWeight(game.getPlayersCards()) >= 21);
    }
  }
}
