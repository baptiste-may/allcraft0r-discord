import {ChatInputCommandInteraction, SlashCommandBuilder } from "discord.js";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("eyes")
        .setDescription("I'm watching you..."),
    async execute(interaction: ChatInputCommandInteraction) {
        await interaction.reply("👁👄👁");
    }
};