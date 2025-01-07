import {NextResponse} from "next/server";
const CLIENT_ID = process.env.DISCORD_ID;
const REDIRECT_URL = process.env.AUTH_REDIRECT_URL;
if (!CLIENT_ID || !REDIRECT_URL) throw new Error("DISCORD_ID and AUTH_REDIRECT_URL are required");

export function GET(): NextResponse {
    return NextResponse.redirect(`https://discord.com/oauth2/authorize?client_id=${CLIENT_ID}&response_type=code&redirect_uri=${encodeURIComponent(REDIRECT_URL || "")}&scope=identify`);
}