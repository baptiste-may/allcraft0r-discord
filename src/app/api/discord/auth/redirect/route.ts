import {NextRequest, NextResponse} from "next/server";
const CLIENT_ID = process.env.DISCORD_ID;
const CLIENT_SECRET = process.env.DISCORD_SECRET;
const REDIRECT_URL = process.env.AUTH_REDIRECT_URL;
if (!CLIENT_ID || !CLIENT_SECRET || !REDIRECT_URL) throw new Error("DISCORD_ID, DISCORD_SECRET and AUTH_REDIRECT_URL must be provided");

export async function GET(req: NextRequest): Promise<NextResponse> {
    const {searchParams, origin} = new URL(req.url);
    const code = searchParams.get("code");

    if (code === null) return NextResponse.json(null, {status: 403});

    const res = await fetch(`https://discord.com/api/v10/oauth2/token`, {
        method: "POST",
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-expect-error
        body: new URLSearchParams({
            client_id: CLIENT_ID,
            client_secret: CLIENT_SECRET,
            code: code,
            grant_type: "authorization_code",
            redirect_uri: REDIRECT_URL,
            scope: "identify",
        }).toString(),
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        }
    });
    const data: {
        access_token: string;
    } = await res.json();

    return NextResponse.redirect(`${origin}?token=${data.access_token}`);
}