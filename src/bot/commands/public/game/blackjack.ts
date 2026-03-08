import {
  ActionRowBuilder,
  ButtonBuilder,
  ButtonStyle,
  ChatInputCommandInteraction,
  Colors,
  EmbedBuilder,
  Events,
  Interaction,
  MessageFlags,
  SlashCommandBuilder,
  Snowflake,
  User,
} from "discord.js";
import { redstoneEmoji } from "@/libs/discordEmojis";
import { CardName, Hand, ICard, SolitaireGameType } from "typedeck";
import { strHand } from "@/utils";
import { addMoney, getMoney } from "@/libs/money";

function cardWeight(card: ICard) {
  switch (card.cardName) {
    case CardName.Ace:
      return null;
    case CardName.Jack:
      return 10;
    case CardName.Queen:
      return 10;
    case CardName.King:
      return 10;
    default:
      return card.cardName.valueOf() + 1;
  }
}

function calculateWeight(hand: Hand) {
  let res = 0;
  for (const card of hand.getCards()) {
    const weight = cardWeight(card);
    if (weight === null) {
      if (res + 11 > 21) res++;
      else res += 11;
    } else res += weight;
  }
  return res;
}

class Game {
  author: User;
  mise: number;
  playersCards = new Hand();
  masterCards = new Hand();
  deck = new SolitaireGameType().createDeck();
  gameEnded = false;

  constructor(author: User, mise: number) {
    this.author = author;
    this.mise = mise;
    this.deck.shuffle();
    this.deck.deal(this.playersCards, 2);
    this.deck.deal(this.masterCards, 1);
  }

  createEmbed() {
    return new EmbedBuilder()
      .setColor(Colors.DarkGreen)
      .setTitle("🃏 Blackjack 🃏")
      .setDescription(
        `Mise : ${this.mise} ${redstoneEmoji}` +
          (this.gameEnded
            ? ` | Gain : ${this.calculatePlayerGain()} ${redstoneEmoji}`
            : ""),
      )
      .addFields(
        {
          name: "Cartes du croupier",
          value:
            strHand(this.masterCards) +
            "\n=> " +
            calculateWeight(this.masterCards),
          inline: false,
        },
        {
          name: "Cartes de " + this.author.displayName,
          value:
            strHand(this.playersCards) +
            "\n=> " +
            calculateWeight(this.playersCards),
          inline: false,
        },
      );
  }

  isGameStoped() {
    return calculateWeight(this.playersCards) >= 21;
  }

  playerAskCard() {
    this.deck.deal(this.playersCards, 1);
    if (this.isGameStoped()) this.playerAskStop();
  }

  playerAskStop() {
    this.gameEnded = true;
    while (calculateWeight(this.masterCards) < 16) {
      this.deck.deal(this.masterCards, 1);
    }
  }

  playerAskDouble() {
    this.mise *= 2;
    this.playerAskCard();
  }

  calculatePlayerGain() {
    const playerSum = calculateWeight(this.playersCards);
    const masterSum = calculateWeight(this.masterCards);
    if (playerSum > 21) return 0;
    if (playerSum === 21 && this.playersCards.getCount() === 2) {
      if (masterSum === 21 && this.masterCards.getCount() === 2)
        return this.mise;
      return this.mise + Math.round((this.mise * 3) / 2);
    }
    if (masterSum > 21) return this.mise * 2;
    if (playerSum < masterSum) return 0;
    if (playerSum === masterSum) return this.mise;
    if (playerSum > masterSum) return this.mise * 2;
    return 0;
  }
}
const DEFAULT_MISE = 50;
const games: Record<Snowflake, Game> = {};

export default {
  data: new SlashCommandBuilder()
    .setName("blackjack")
    .setDescription("Lance une partie de blackjack")
    .addNumberOption((option) =>
      option
        .setName("mise")
        .setDescription("La mise de départ")
        .setRequired(false)
        .setMinValue(25),
    ),
  async execute(interaction: ChatInputCommandInteraction) {
    const mise = parseInt(
      String(interaction.options.getNumber("mise", false) || DEFAULT_MISE),
    );
    const userMoney = await getMoney(interaction.user.id);
    if (userMoney < mise)
      return interaction.reply({
        content: "❌ Tu n'as pas assez de redstone !",
        flags: MessageFlags.Ephemeral,
      });

    await addMoney(interaction.user.id, -mise);

    const nextCardButton = new ButtonBuilder()
      .setCustomId("blackjack-nextcard")
      .setLabel("Carte !")
      .setStyle(ButtonStyle.Primary);

    const stopButton = new ButtonBuilder()
      .setCustomId("blackjack-stop")
      .setLabel("Stop !")
      .setStyle(ButtonStyle.Danger);

    const doubleButton = new ButtonBuilder()
      .setCustomId("blackjack-double")
      .setLabel("Double la mise !")
      .setStyle(ButtonStyle.Success);

    const buttons = new ActionRowBuilder().addComponents(
      nextCardButton,
      stopButton,
      doubleButton,
    );

    const game = new Game(interaction.user, mise);
    if (game.isGameStoped()) {
      game.playerAskStop();
      nextCardButton.setDisabled(true);
      stopButton.setDisabled(true);
      doubleButton.setDisabled(true);
      await addMoney(interaction.user.id, game.calculatePlayerGain());
    }

    await interaction.reply({
      embeds: [game.createEmbed()],
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-expect-error
      components: [buttons],
    });
    const message = await interaction.fetchReply();

    games[message.id] = game;
  },
  listeners: [
    {
      event: Events.InteractionCreate,
      on: async (interaction: Interaction) => {
        if (!interaction.isMessageComponent()) return;
        if (!interaction.customId.startsWith("blackjack")) return;
        const action = interaction.customId.replace("blackjack-", "");
        if (!(interaction.message.id in games))
          return await interaction.reply({
            content: "❌ La partie n'existe pas!",
            flags: MessageFlags.Ephemeral,
          });
        const game = games[interaction.message.id];
        if (game.author.id !== interaction.user.id)
          return await interaction.reply({
            content: "❌ Tu ne peux pas jouer à la place des autres !",
            flags: MessageFlags.Ephemeral,
          });
        const buttons = ActionRowBuilder.from(
          interaction.message.components[0] as any,
        );
        const stop = () => {
          delete games[interaction.message.id];
          buttons.setComponents(
            buttons.components.map((button) =>
              // eslint-disable-next-line @typescript-eslint/ban-ts-comment
              // @ts-expect-error
              ButtonBuilder.from(button).setDisabled(true),
            ),
          );
        };
        switch (action) {
          case "nextcard":
            game.playerAskCard();
            break;
          case "stop":
            game.playerAskStop();
            break;
          case "double":
            const userMoney = await getMoney(interaction.user.id);
            if (userMoney < game.mise * 2)
              return await interaction.reply({
                content:
                  "❌ Tu n'as pas assez de redstone pour doubler ta mise !",
                flags: MessageFlags.Ephemeral,
              });
            await addMoney(interaction.user.id, -game.mise);
            game.playerAskDouble();
            break;
        }
        if (game.gameEnded) {
          stop();
          await addMoney(interaction.user.id, game.calculatePlayerGain());
        }
        await interaction.update({
          embeds: [game.createEmbed()],
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-expect-error
          components: [buttons],
        });
      },
    },
  ],
};
