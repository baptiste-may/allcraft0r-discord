import { ChatInputCommandInteraction, SlashCommandBuilder } from "discord.js";

export default {
  data: new SlashCommandBuilder()
    .setName("eyes")
    .setDescription("I'm watching you..."),
  async execute(interaction: ChatInputCommandInteraction) {
    await interaction.reply("👁👄👁");
  },
};
