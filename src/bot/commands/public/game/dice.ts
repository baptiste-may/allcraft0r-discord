import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder} from "discord.js";

const DEFAULT_FACES = 6;
const DEFAULT_NUMBER = 1;

module.exports = {
    data: new SlashCommandBuilder()
        .setName("dice")
        .setDescription("Lance un ou plusieurs dé(s)")
        .addNumberOption(option => option
            .setName("faces")
            .setDescription("Le nombre de faces")
            .setRequired(false)
            .setMinValue(2)
            .setMaxValue(150))
        .addNumberOption(option => option
            .setName("number")
            .setDescription("Le nombre de dés lancés")
            .setRequired(false)
            .setMinValue(1)),
    async execute(interaction: ChatInputCommandInteraction) {

        const faces = interaction.options.getNumber("faces", false) || DEFAULT_FACES;
        const number = interaction.options.getNumber("number", false) || DEFAULT_NUMBER;

        const random = () => Math.floor(Math.random() * faces) + 1;

        const res: number[] = [];
        for (let i = 0; i < number; i++) {
            res.push(random());
        }
        const embed = new EmbedBuilder()
            .setTitle(`🎲 Résultat 🎲`)
            .setDescription("# [" + res.join("] [") + "]")
            .setColor(Colors.White)
            .setFooter({
                iconURL: interaction.user.avatarURL() || undefined,
                text: `Tirage: ${number} x d${faces}`
            });
        await interaction.reply({embeds: [embed]});
    }
};