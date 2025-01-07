import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder} from "discord.js";
import {getMoney} from "@/libs/money";
import {redstoneEmoji} from "@/libs/discordEmojis";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("money")
        .setDescription("Affiche son nombre de redstones"),
    async execute(interaction: ChatInputCommandInteraction) {
        const n = await getMoney(interaction.user.id);
        const embed = new EmbedBuilder()
            .setTitle(`Tu as actuellement ${n} ${redstoneEmoji}`)
            .setColor(Colors.Gold);
        await interaction.reply({
            embeds: [embed],
        });
    }
};