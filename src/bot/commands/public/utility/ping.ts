import {SlashCommandBuilder, ChatInputCommandInteraction, EmbedBuilder, Colors} from "discord.js";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("ping")
        .setDescription("Lance une balle de ping pong, voit en combien de temps je la renvoie"),
    async execute(interaction: ChatInputCommandInteraction) {
        const embed = new EmbedBuilder()
            .setTitle("🏓 Pong !")
            .setDescription(`⏳ ${new Date().getTime() - interaction.createdTimestamp}ms`)
            .setColor(Colors.Orange);
        await interaction.reply({embeds: [embed], ephemeral: true});
    },
};
