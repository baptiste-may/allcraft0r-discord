import { ChatInputCommandInteraction, SlashCommandBuilder } from "discord.js";

export default {
  data: new SlashCommandBuilder()
    .setName("tank")
    .setDescription("AMERICA ! F*CK YEAHH !!"),
  async execute(interaction: ChatInputCommandInteraction) {
    await interaction.reply("https://tenor.com/view/tank-gif-10952763");
  },
};
