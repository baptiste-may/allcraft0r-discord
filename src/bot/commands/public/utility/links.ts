import {
    ActionRowBuilder,
    ButtonBuilder,
    ButtonStyle,
    ChatInputCommandInteraction,
    SlashCommandBuilder
} from "discord.js";
const WEBSITE_URL = process.env.WEBSITE_URL;
if (!WEBSITE_URL) throw new Error("WEBSITE_URL must be provided");

module.exports = {
    data: new SlashCommandBuilder()
        .setName("links")
        .setDescription("Affiche des liens en rapport avec allcraft0r"),
    async execute(interaction: ChatInputCommandInteraction) {
        const ytBase = new ButtonBuilder()
            .setLabel("Chaîne Youtube de allcraft0r")
            .setURL("https://www.youtube.com/channel/UCY8ryk_01LytUhgfA5X3vFg")
            .setStyle(ButtonStyle.Link);
        const ytBestOf = new ButtonBuilder()
            .setLabel("Chaîne Youtube Best Of Discord")
            .setURL("https://www.youtube.com/channel/UCQH2Kxrr6Y68ZcBWfJdtZ6A")
            .setStyle(ButtonStyle.Link);
        const twiter = new ButtonBuilder()
            .setLabel("Compte Twitter Best of Discord")
            .setURL("https://twitter.com/bestOfAllcraft?s=09")
            .setStyle(ButtonStyle.Link);
        const website = new ButtonBuilder()
            .setLabel("Site web du bot Discord")
            .setURL(WEBSITE_URL)
            .setStyle(ButtonStyle.Link);

        const row = new ActionRowBuilder()
            .addComponents(ytBase, ytBestOf, twiter, website);

        await interaction.reply({
            content: "Voici plusieurs lien en rapport avec allcraft0r :",
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            components: [row],
            ephemeral: true
        });
    }
};