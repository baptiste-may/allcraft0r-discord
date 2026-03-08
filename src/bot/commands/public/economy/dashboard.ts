import {
  ChatInputCommandInteraction,
  Colors,
  EmbedBuilder,
  SlashCommandBuilder,
} from "discord.js";
import { redstoneEmoji } from "@/libs/discordEmojis";
import { getLeaderboard } from "@/libs/money";

const emojis = ["🏆", "🥈", "🥉", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"];

export default {
  data: new SlashCommandBuilder()
    .setName("dashboard")
    .setDescription("Affiche les 10 membres ayant le plus de redstone"),
  async execute(interaction: ChatInputCommandInteraction) {
    const leaderboard = await getLeaderboard();

    let description = "";
    for (let i = 0; i < Math.min(10, leaderboard.length); i++) {
      const { id, money } = leaderboard[i];
      if (i < 2) {
        description += "#".repeat(i + 1);
      } else {
        description += "###";
      }
      description += ` ${emojis[i]} <@${id}> - **${money}**\n`;
    }

    const embed = new EmbedBuilder()
      .setTitle(`Dashboard de redstone ${redstoneEmoji}`)
      .setDescription(description)
      .setColor(Colors.Red);

    await interaction.reply({
      embeds: [embed],
    });
  },
};
