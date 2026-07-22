package fr.may_baptiste.allcraft0r_discord.commands.game.blackjack;

import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Deck;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Hand;
import fr.may_baptiste.allcraft0r_discord.commands.game.cards.Rank;
import fr.may_baptiste.allcraft0r_discord.config.DiscordConfig;
import fr.may_baptiste.allcraft0r_discord.system.service.MoneyService;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

public final class BlackjackGame extends ListenerAdapter {

  private static final String NEXT_CARD_ID = "blackjack-nextcard";
  private static final String STOP_ID = "blackjack-stop";
  private static final String DOUBLE_ID = "blackjack-double";

  private final DiscordConfig discordConfig;
  private final MoneyService moneyService;
  private final Consumer<Long> callback;
  private final long authorId;
  private final String authorName;

  @Getter private final Hand playersCards = new Hand();
  @Getter private final Hand masterCards = new Hand();
  private final Deck deck = new Deck();

  @Getter private long bet;
  @Getter private boolean gameEnded = false;
  private long messageId = 0;

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> timeoutTask;

  public BlackjackGame(
      SlashCommandInteractionEvent event,
      DiscordConfig discordConfig,
      MoneyService moneyService,
      long bet,
      Consumer<Long> callback) {
    this.discordConfig = discordConfig;
    this.moneyService = moneyService;
    this.bet = bet;
    this.callback = callback;
    this.authorId = event.getUser().getIdLong();
    this.authorName = event.getUser().getEffectiveName();

    deck.shuffle();
    deck.deal(playersCards, 2);
    deck.deal(masterCards, 1);

    if (isGameStopped()) {
      playerAskStop();
      event
          .replyEmbeds(createEmbed())
          .setFiles(createFileUpload())
          .setComponents(List.of(createButtons(true, true, true)))
          .queue(
              _ -> {
                scheduler.shutdown();
                callback.accept(calculatePlayerGain());
              });
      return;
    }

    final var jda = event.getJDA();
    jda.addEventListener(this);
    resetTimeout(jda);

    event
        .replyEmbeds(createEmbed())
        .setFiles(createFileUpload())
        .setComponents(List.of(createButtons(false, false, false)))
        .queue(hook -> hook.retrieveOriginal().queue(msg -> this.messageId = msg.getIdLong()));
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    if (!event.getComponentId().startsWith("blackjack-")) {
      return;
    }
    if (messageId != 0 && event.getMessageIdLong() != messageId) {
      return;
    }
    if (event.getUser().getIdLong() != authorId) {
      event.reply("❌ Tu ne peux pas jouer à la place des autres !").setEphemeral(true).queue();
      return;
    }

    resetTimeout(event.getJDA());

    final var action = event.getComponentId().replace("blackjack-", "");
    switch (action) {
      case "nextcard" -> playerAskCard();
      case "stop" -> playerAskStop();
      case "double" -> {
        if (playersCards.size() != 2) {
          event.reply("❌ Tu ne peux plus doubler ta mise !").setEphemeral(true).queue();
          return;
        }
        final var userMoney = moneyService.getMoney(String.valueOf(authorId));
        if (userMoney < bet) {
          event
              .reply("❌ Tu n'as pas assez de redstone pour doubler ta mise !")
              .setEphemeral(true)
              .queue();
          return;
        }
        moneyService.addMoney(String.valueOf(authorId), -bet);
        bet *= 2;
        playerAskDouble();
      }
      default -> {
        return;
      }
    }

    if (gameEnded) {
      cleanup(event.getJDA());
      callback.accept(calculatePlayerGain());
      event
          .editMessageEmbeds(createEmbed())
          .setFiles(createFileUpload())
          .setComponents(List.of(createButtons(true, true, true)))
          .queue();
    } else {
      event
          .editMessageEmbeds(createEmbed())
          .setFiles(createFileUpload())
          .setComponents(List.of(createButtons(false, false, true)))
          .queue();
    }
  }

  private void cleanup(JDA jda) {
    if (timeoutTask != null) {
      timeoutTask.cancel(false);
    }
    scheduler.shutdown();
    if (jda != null) {
      jda.removeEventListener(this);
    }
  }

  private void resetTimeout(JDA jda) {
    if (timeoutTask != null) {
      timeoutTask.cancel(false);
    }
    timeoutTask = scheduler.schedule(() -> cleanup(jda), 3, TimeUnit.MINUTES);
  }

  public boolean isGameStopped() {
    return calculateWeight(playersCards) >= 21;
  }

  public void playerAskCard() {
    deck.deal(playersCards, 1);
    if (isGameStopped()) {
      playerAskStop();
    }
  }

  public void playerAskStop() {
    this.gameEnded = true;
    while (calculateWeight(masterCards) < 17) {
      deck.deal(masterCards, 1);
    }
  }

  public void playerAskDouble() {
    deck.deal(playersCards, 1);
    playerAskStop();
  }

  public long calculatePlayerGain() {
    final var playerSum = calculateWeight(playersCards);
    final var masterSum = calculateWeight(masterCards);

    if (playerSum > 21) {
      return 0;
    }
    if (playerSum == 21 && playersCards.size() == 2) {
      if (masterSum == 21 && masterCards.size() == 2) {
        return bet;
      }
      return bet + Math.round((double) (bet * 3) / 2.0);
    }
    if (masterSum > 21) {
      return bet * 2;
    }
    if (playerSum < masterSum) {
      return 0;
    }
    if (playerSum == masterSum) {
      return bet;
    }
    return bet * 2;
  }

  private FileUpload createFileUpload() {
    final var imageBytes =
        BlackjackTableRenderer.renderTable(
            playersCards,
            masterCards,
            calculateWeight(playersCards),
            calculateWeight(masterCards),
            authorName,
            bet,
            gameEnded ? calculatePlayerGain() : null,
            gameEnded);
    return FileUpload.fromData(imageBytes, "blackjack.png");
  }

  private MessageEmbed createEmbed() {
    final var description =
        "Mise : %s".formatted(discordConfig.formatRedstoneNumber(bet))
            + (gameEnded
                ? " | Gain : %s"
                    .formatted(discordConfig.formatRedstoneNumber(calculatePlayerGain()))
                : "");

    return new EmbedBuilder()
        .setColor(new Color(46, 125, 50))
        .setTitle("🃏 Blackjack 🃏")
        .setDescription(description)
        .addField("Cartes du croupier", masterCards + "\n=> " + calculateWeight(masterCards), false)
        .addField(
            "Cartes de " + authorName,
            playersCards + "\n=> " + calculateWeight(playersCards),
            false)
        .setImage("attachment://blackjack.png")
        .build();
  }

  private ActionRow createButtons(
      boolean disableNextCard, boolean disableStop, boolean disableDouble) {
    return ActionRow.of(
        Button.primary(NEXT_CARD_ID, "Carte !").withDisabled(disableNextCard),
        Button.danger(STOP_ID, "Stop !").withDisabled(disableStop),
        Button.success(DOUBLE_ID, "Double la mise !").withDisabled(disableDouble));
  }

  public static int calculateWeight(Hand hand) {
    int nonAceSum = 0;
    int aceCount = 0;
    for (final var card : hand.getCards()) {
      if (card.rank() == Rank.ACE) {
        aceCount++;
      } else {
        nonAceSum += card.rank().getValue();
      }
    }
    int total = nonAceSum + aceCount;
    for (int i = 0; i < aceCount; i++) {
      if (total + 10 <= 21) {
        total += 10;
      }
    }
    return total;
  }
}
