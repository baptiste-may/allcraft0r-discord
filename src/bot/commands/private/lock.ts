import {ChatInputCommandInteraction, Colors, EmbedBuilder, SlashCommandBuilder, TextChannel} from "discord.js";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("lock")
        .setDescription("Permet de lock un channel")
        .addChannelOption(option => option
            .setName("channel")
            .setDescription("Le channel cible")
            .setRequired(true))
        .addStringOption(option => option
            .setName("reason")
            .setDescription("La raison")
            .setRequired(false)),
    async execute(interaction: ChatInputCommandInteraction) {
        const target = interaction.options.getChannel("channel", true) as TextChannel;
        const permissions = target.permissionsFor(target.guild.roles.everyone).toArray();

        if (permissions.includes("SendMessages")) {
            await target.permissionOverwrites.create(target.guild.roles.everyone, {SendMessages: false});
            let embed = new EmbedBuilder()
                .setTitle("Le channel a été lock !")
                .setColor(Colors.Green);
            await interaction.reply({embeds: [embed], ephemeral: true});
            embed = new EmbedBuilder()
                .setTitle("Ce channel a été lock ⛔️")
                .setAuthor({
                    name: interaction.user.tag,
                    iconURL: interaction.user.avatarURL() || undefined
                })
                .setColor(Colors.Red);
            const reason = interaction.options.getString("reason", false);
            if (reason) embed.setDescription(reason);
            await target.send({embeds: [embed]});
        } else {
            const embed = new EmbedBuilder()
                .setTitle("Le channel est déjà lock !")
                .setColor(Colors.Yellow);
            await interaction.reply({embeds: [embed], ephemeral: true});
        }
    }
}