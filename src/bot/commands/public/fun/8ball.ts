import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder} from "discord.js";

const MESSAGES = [
    "Essaye plus tard",
    "Essaye encore",
    "Pas d'avis",
    "C'est ton destin",
    "Le sort en est jeté",
    "Une chance sur deux",
    "Repose ta question",
    "D'après moi, oui",
    "C'est certain",
    "Oui, absolument",
    "Tu peux compter dessus",
    "Sans aucun doute",
    "Très probable",
    "Oui",
    "C'est bien parti",
    "C'est non",
    "Peu probable",
    "Faut pas rêver",
    "N'y compte pas",
    "Impossible"
]

module.exports = {
    data: new SlashCommandBuilder()
        .setName("8ball")
        .setDescription("Seul l'avenir est ici")
        .addStringOption(option =>
            option.setName("question").setDescription("Votre question").setRequired(true)),
    async execute(interaction: ChatInputCommandInteraction) {
        const embed = new EmbedBuilder()
            .setTitle("🔮 8 Ball 🔮")
            .addFields({
                name: `__${interaction.options.getString("question", true)}__`,
                value: MESSAGES[Math.floor(Math.random() * MESSAGES.length)],
                inline: true
            })
            .setColor(Colors.Purple);
        await interaction.reply({embeds: [embed]});
    }
};