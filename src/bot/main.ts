import {Client, Events, GatewayIntentBits} from "discord.js";
import registerCommands from "./registerCommands";

// Creating client
const client = new Client({ intents: [GatewayIntentBits.Guilds] });

// Fetching commands
const {deployCommands, publicCommands} = await registerCommands(client);

client.once(Events.ClientReady, readyClient => {
    console.log(`✅ Discord Bot ready! Logged in as ${readyClient.user.tag}`);
});

export {
    client as default,
    deployCommands,
    publicCommands
};