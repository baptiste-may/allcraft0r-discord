import {NextResponse} from "next/server";
const EMOJI_ID = process.env.REDSTONE_EMOJI_ID;
if (!EMOJI_ID) throw new Error("REDSTONE_EMOJI_ID is required");

export function GET(): NextResponse {
    return NextResponse.redirect(`https://cdn.discordapp.com/emojis/${EMOJI_ID}.webp`);
}