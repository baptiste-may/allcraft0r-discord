import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder} from "discord.js";
import {redstoneEmoji} from "@/libs/discordEmojis";
import {delay, randomNumber} from "@/utils";
import {addMoney, getMoney} from "@/libs/money";

const DEFAULT_MISE = 50;
const EMOJIS = [
    "🍇", "🍒", "🫐", "🍉", "🍎", "🍌"
];
const SPINNING_EMOJI = "⏬";

function getEmoji(index?: number, position?: -1 | 0 | 1) {
    if (index === undefined || position === undefined) return SPINNING_EMOJI;
    let newIndex = (index + position) % EMOJIS.length;
    if (newIndex < 0) newIndex += EMOJIS.length;
    return EMOJIS[newIndex];
}

function drawMachine(index1?: number, index2?: number, index3?: number) {
    return `\`\`\`
  ${getEmoji(index1, -1)} | ${getEmoji(index2, -1)} | ${getEmoji(index3, -1)}
> ${getEmoji(index1,  0)} | ${getEmoji(index2,  0)} | ${getEmoji(index3,  0)} <
  ${getEmoji(index1,  1)} | ${getEmoji(index2,  1)} | ${getEmoji(index3,  1)}
\`\`\``;
}

module.exports = {
    data: new SlashCommandBuilder()
        .setName("spin")
        .setDescription("Lance une partie de machine à sous")
        .addNumberOption(option => option
            .setName("mise")
            .setDescription("La mise de départ")
            .setRequired(false)
            .setMinValue(25)),
    async execute(interaction: ChatInputCommandInteraction) {
        const mise = parseInt(String(interaction.options.getNumber("mise", false) || DEFAULT_MISE));

        const userMoney = await getMoney(interaction.user.id);
        if (userMoney < mise) return interaction.reply({
            content: "❌ Tu n'as pas assez de redstone !",
            ephemeral: true
        });

        await addMoney(interaction.user.id, -mise);

        const embed = new EmbedBuilder()
            .setTitle("🎰 Spiiinnnnnn 🎰")
            .setColor(Colors.Gold);

        let index1: number | undefined = undefined;
        let index2: number | undefined = undefined;
        let index3: number | undefined = undefined;
        let gain: number | undefined = undefined;
        const updateDescription = () => {
            embed.setDescription(`Mise : ${mise} ${redstoneEmoji}${gain === undefined ? "" : `\nGain : ${gain} ${redstoneEmoji}`}\n${drawMachine(index1, index2, index3)}`);
        }
        updateDescription();
        await interaction.reply({
            embeds: [embed]
        });

        await delay(1000);

        index1 = randomNumber(EMOJIS.length - 1);
        updateDescription();
        await interaction.editReply({
            embeds: [embed]
        });

        await delay(1000);

        index2 = randomNumber(EMOJIS.length - 1);
        updateDescription();
        await interaction.editReply({
            embeds: [embed]
        });

        await delay(1000);

        index3 = randomNumber(EMOJIS.length - 1);
        const isWinner = index1 === index2 && index2 === index3;
        gain = isWinner ? mise * EMOJIS.length : 0;
        await addMoney(interaction.user.id, gain);

        updateDescription();
        embed.setTitle(isWinner ? "🎰 Jackpot ! 🎰" : "🎰 Dommage ! 🎰");

        await interaction.editReply({
            embeds: [embed]
        });
    }
};