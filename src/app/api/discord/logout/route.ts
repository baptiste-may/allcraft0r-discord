import {NextRequest, NextResponse} from "next/server";
const CLIENT_ID = process.env.DISCORD_ID;
const CLIENT_SECRET = process.env.DISCORD_SECRET;
if (!CLIENT_ID || !CLIENT_SECRET) throw new Error("DISCORD_ID and DISCORD_SECRET must be provided");

export async function GET(req: NextRequest): Promise<NextResponse> {
    const {origin, searchParams} = new URL(req.url);
    const end = () => NextResponse.redirect(origin);

    const token = searchParams.get("token");
    if (token === null) return end();

    try {
        await fetch(`https://discord.com/api/v10/oauth2/token/revoke`, {
            method: "POST",
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            body: new URLSearchParams({
                client_id: CLIENT_ID,
                client_secret: CLIENT_SECRET,
                token: token,
                token_type_hint: "access_token"
            }).toString(),
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        });
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (err) {}

    return end();
}