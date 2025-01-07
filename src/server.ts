import next from "next";
import express from "express";
import DiscordClient, {deployCommands, publicCommands} from "./bot/main";
import "dotenv/config";
const TOKEN = process.env.DISCORD_TOKEN;
if (!TOKEN) throw new Error("DISCORD_TOKEN is required");
const CLIENT_ID = process.env.DISCORD_ID;
if (!CLIENT_ID) throw new Error("DISCORD_ID is required");
const GUILD_ID = process.env.DISCORD_GUILD_ID;
if (!GUILD_ID) throw new Error("DISCORD_GUILD_ID is required");

const port = parseInt(process.env.PORT || "3000", 10);
const dev = process.env.NODE_ENV !== "production";
const app = next({dev});
const handle = app.getRequestHandler();

// Prepare Front app
app.prepare().then(async () => {

    console.log("✅ Frontend app ready!");

    // Creating server
    const server = express();

    server.use(express.json());

    // Creating discord bot
    const discordClient = DiscordClient;
    // Deploying slash commands
    await deployCommands(TOKEN, CLIENT_ID, GUILD_ID);

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-expect-error
    server.get("/api/getCommands", (_, res) => {
        return res.json(publicCommands);
    });

    // Log to discord
    await discordClient.login(TOKEN);

    // Add Next route to server
    server.get("*", (req, res) => handle(req, res));

    // Start server
    server.listen(port, () => {
        console.log("✅ Server ready!");
    });
});