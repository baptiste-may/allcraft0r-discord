import {Client, Events, GatewayIntentBits} from "discord.js";
import registerCommands from "./registerCommands";
import path from "node:path";
import fs from "node:fs";
import yaml from "js-yaml";
import {schedule} from "node-cron";
import {addMoney} from "@/libs/money";
import chalk from "chalk";

const __dirname = import.meta.dirname;

// Creating client
const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent,
        GatewayIntentBits.GuildMessagePolls
    ]
});

// Fetching commands
const {deployCommands, publicCommands} = await registerCommands(client);

const activityFile = fs.readFileSync(path.join(__dirname, "activity.yml"));
const {activities} = yaml.load(activityFile.toString("utf-8")) as {
    activities: {
        type: number;
        text: string;
    }[]
};

client.on(Events.MessageCreate, async event => {
    if (event.author.bot) return;
    if (event.content.length < 10) return;
    if (Math.random() < 0.5) return;
    await addMoney(event.author.id, 1);
});

client.once(Events.ClientReady, readyClient => {
    console.log(chalk.green(`✅ Discord Bot ready! Logged in as ${readyClient.user.tag}`));

    let currentActivity = 0;
    const updateActivity = () => {
        const {text, type} = activities[currentActivity];
        client.user?.setActivity({
            name: text,
            type: type
        });
        currentActivity = (currentActivity + 1) % activities.length;
    }
    updateActivity();
    schedule("* * * * *", updateActivity);
});

export {
    client as default,
    deployCommands,
    publicCommands
};