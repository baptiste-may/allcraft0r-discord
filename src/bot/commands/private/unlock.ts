import {
    ChatInputCommandInteraction,
    Colors,
    EmbedBuilder,
    PermissionFlagsBits,
    SlashCommandBuilder,
    TextChannel
} from "discord.js";

module.exports = {
    data: new SlashCommandBuilder()
        .setName("unlock")
        .setDescription("Unlock un channel")
        .addChannelOption(option => option
            .setName("channel")
            .setDescription("Le channel cible")
            .setRequired(true))
        .addStringOption(option => option
            .setName("reason")
            .setDescription("La raison")
            .setRequired(false))
        .setDefaultMemberPermissions(PermissionFlagsBits.ManageMessages),
    async execute(interaction: ChatInputCommandInteraction) {
        const target = interaction.options.getChannel("channel", true) as TextChannel;
        const permissions = target.permissionsFor(target.guild.roles.everyone).toArray();

        if (!permissions.includes("SendMessages")) {
            await target.permissionOverwrites.edit(target.guild.roles.everyone, {SendMessages: true});
            let embed = new EmbedBuilder()
                .setTitle("Le channel a été unlock !")
                .setColor(Colors.Green);
            await interaction.reply({embeds: [embed], ephemeral: true});
            embed = new EmbedBuilder()
                .setTitle("Ce channel a été unlock ✅")
                .setAuthor({
                    name: interaction.user.tag,
                    iconURL: interaction.user.avatarURL() || undefined
                })
                .setColor(Colors.Green);
            const reason = interaction.options.getString("reason", false);
            if (reason) embed.setDescription(reason);
            await target.send({embeds: [embed]});
        } else {
            const embed = new EmbedBuilder()
                .setTitle("Le channel est déjà unlock !")
                .setColor(Colors.Yellow);
            await interaction.reply({embeds: [embed], ephemeral: true});
        }
    }
};