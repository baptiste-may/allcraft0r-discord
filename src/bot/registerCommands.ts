import {
    ChatInputCommandInteraction, Client, Collection, EmbedBuilder, Events, REST,
    RESTPostAPIChatInputApplicationCommandsJSONBody, Routes, SlashCommandBuilder
} from "discord.js";
import path from "node:path";
import fs from "node:fs";
import yaml from "js-yaml";

const __dirname = import.meta.dirname;

type FileCommandData = {
    data: SlashCommandBuilder;
    execute: (interaction: ChatInputCommandInteraction, client?: Client) => void;
}

type CategoryCommands = Record<string, {
    name: string;
    description: string;
    icon: string;
    color: number;
    commands: FileCommandData[];
}>

/**
 * Fetch commands in a folder
 * @param pathname {String} The path name of the folder
 * @return The commands
 */
async function getCommands(pathname: string): Promise<FileCommandData[]> {
    const cmds: FileCommandData[] = [];
    try {
        for (const file of fs.readdirSync(pathname)) {
            if (file.split(".")[file.split(".").length - 1] === "ts") {
                try {
                    cmds.push((await (import(path.join(pathname, file)))).default as FileCommandData);
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                } catch (err) {
                    console.warn(`⚠️ Command ${path.join(pathname, file)} is not correct.`);
                }
            }
        }
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (err) {
        console.warn(`⚠️ No commands have been found in ${pathname}`);
        return [];
    }
    return cmds;
}

/**
 * Fetch public and private commands and prepare deploy function
 * @param client {Client} Discord Client
 * @return Public and private commands, also the deploy function
 */
export default async function registerCommands(client: Client): Promise<{
    privateCommands: FileCommandData[],
    publicCommands: CategoryCommands,
    deployCommands: (token: string, clientId: string, guildId: string) => Promise<void>
}> {

    const allCommands: Collection<string, FileCommandData> = new Collection();
    const JSONCommands: RESTPostAPIChatInputApplicationCommandsJSONBody[] = [];

    const commandsFolder = path.join(__dirname, "commands");

    console.log(`🔎 Searching for commands...`);

    const privateCommands: FileCommandData[] = await getCommands(path.join(commandsFolder, "private"));
    Object.values(privateCommands).forEach(cmd => {
        allCommands.set(cmd.data.name, cmd);
        JSONCommands.push(cmd.data.toJSON());
    });

    const publicCommands: CategoryCommands = {};
    for (const folder of fs.readdirSync(path.join(commandsFolder, "public"))) {
        const data: {
            name: string;
            description: string;
            icon: string;
            color: number;
            commands: FileCommandData[];
        } = {
            name: folder,
            description: `Categorie ${folder}`,
            icon: "command",
            color: 0,
            commands: await getCommands(path.join(commandsFolder, "public", folder))
        };

        try {
            const labelFile = fs.readFileSync(path.join(commandsFolder, "public", folder, "label.yaml"));
            const {name, description, icon, color} = yaml.load(labelFile.toString("utf-8")) as {
                name: string | undefined;
                description: string | undefined;
                icon: string | undefined;
                color: number | undefined;
            };
            if (name) data.name = name;
            if (description) data.description = description;
            if (icon) data.icon = icon;
            if (color) data.color = color;
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (err) {
            console.warn(`⚠️ Label file for category ${folder} not found.`);
        }

        publicCommands[folder] = data;
    }

    const helpCommand = new SlashCommandBuilder()
        .setName("help")
        .setDescription("Liste des commandes")
        .addStringOption(option => option
            .setName("type")
            .setDescription("Le type les commandes")
            .setRequired(true)
            .addChoices(...Object.entries(publicCommands).map(([id, {name}]) => {return {
                name: name, value: id
            }}))) as SlashCommandBuilder;
    publicCommands[publicCommands.hasOwnProperty("utility") ? "utility" : Object.keys(publicCommands)[0]].commands.push({
        data: helpCommand,
        execute: async (interaction: ChatInputCommandInteraction) => {
            const {name, description, color, commands} = publicCommands[interaction.options.getString("type", true)];
            const embed = new EmbedBuilder()
                .setTitle(name)
                .setDescription(description)
                .setColor(color);
            for (const cmd of commands) {
                let optionsTxt = "";
                for (const option of cmd.data.options) {
                    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                    // @ts-expect-error
                    const { name, required }: {
                        name: string;
                        required: boolean;
                    } = option;
                    optionsTxt += required ? "<" : "(";
                    optionsTxt += name;
                    optionsTxt += required ? ">" : ")";
                    optionsTxt += " ";
                }
                embed.addFields({
                    name: `◽️ ${cmd.data.name} ${optionsTxt}`,
                    value: `> ${cmd.data.description}`,
                    inline: true
                });
            }
            await interaction.reply({embeds: [embed], ephemeral: true});
        }
    });

    Object.values(publicCommands).forEach(cat => cat.commands.forEach(cmd => {
        allCommands.set(cmd.data.name, cmd);
        JSONCommands.push(cmd.data.toJSON());
    }));

    console.log(`✅ ${allCommands.size - 1} commands founded.`)

    client.on(Events.InteractionCreate, async interaction => {
        if (!interaction.isChatInputCommand()) return;

        const command = allCommands.get(interaction.commandName);

        if (!command) return console.error(`⚠️ No command matching ${interaction.commandName} was found.`);

        try {
            command.execute(interaction, client);
        } catch (error) {
            const res = {
                content: `Une erreur s'est produite : \`\`\`\n${error}\n\`\`\``,
                ephemeral: true
            }
            interaction.reply(res);
            if (interaction.replied || interaction.deferred)
                await interaction.followUp(res);
            else
                await interaction.reply(res);
        }
    });

    /**
     * Deploy slash commands on discord
     * @param token {string} Discord Token
     * @param clientId {string} Client ID
     * @param guildId {string} Guild ID
     */
    const deployCommands = async (token: string, clientId: string, guildId: string) => {

        console.log(`📦 Deploying ${JSONCommands.length} commands...`)

        const rest = new REST().setToken(token);
        await rest.put(
            Routes.applicationGuildCommands(clientId, guildId),
            { body: JSONCommands },
        );

        console.log("✅ Done!")
    };

    return {privateCommands, publicCommands, deployCommands};
}