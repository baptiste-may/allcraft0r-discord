import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder} from "discord.js";
import {DAILY_MONEY, executeDaily} from "@/libs/money";
import {redstoneEmoji} from "@/libs/discordEmojis";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("daily")
        .setDescription("Récupère sa redstone quotidienne"),
    async execute(interaction: ChatInputCommandInteraction) {
        const id = interaction.user.id;
        const res = await executeDaily(id);
        if (!res) {
            const embed = new EmbedBuilder()
                .setTitle("Tu as déjà récupéré ta redstone quotidienne.")
                .setColor(Colors.Red);
            await interaction.reply({
                embeds: [embed],
                ephemeral: true
            });
        } else {
            const embed = new EmbedBuilder()
                .setTitle(`Tu as reçu ${DAILY_MONEY} ${redstoneEmoji}`)
                .setColor(Colors.Green);
            await interaction.reply({
                embeds: [embed],
            });
        }
    }
};