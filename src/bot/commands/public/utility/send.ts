import {ChatInputCommandInteraction, Client, Colors, EmbedBuilder, SlashCommandBuilder, TextChannel} from "discord.js";
import assert from "node:assert";
const GUILD_ID = process.env.DISCORD_GUILD_ID;
if (!GUILD_ID) throw new Error("DISCORD_GUILD_ID is required");
const ADMIN_CHANNEL_ID = process.env.ADMIN_CHANNEL_ID;
if (!ADMIN_CHANNEL_ID) throw new Error("ADMIN_CHANNEL_ID is required");

module.exports = {
    data: new SlashCommandBuilder()
        .setName("send")
        .setDescription("Envoie un message aux personnes de puissances")
        .addStringOption(option =>
            option.setName("message").setDescription("Votre message").setRequired(true)),
    async execute(interaction: ChatInputCommandInteraction, client: Client) {
        const guild = await client.guilds.fetch(GUILD_ID);
        const adminChannel = await guild.channels.fetch(ADMIN_CHANNEL_ID) as TextChannel | null;
        assert(adminChannel !== null);
        adminChannel.send({
            content: `Message de \`${interaction.user.username}\`:\n> ${interaction.options.getString("message", true)}`
        });

        const embed = new EmbedBuilder()
            .setTitle("Votre message a été correctement envoyé.")
            .setColor(Colors.Green);
        await interaction.reply({embeds: [embed], ephemeral: true});
    }
};