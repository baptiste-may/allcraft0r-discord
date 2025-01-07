import {NextResponse} from "next/server";
import {getLeaderboard} from "@/libs/money";
const TOKEN = process.env.DISCORD_TOKEN;
if (!TOKEN) throw new Error("Token is required");

export async function GET(): Promise<NextResponse> {

    const stream = new ReadableStream({
        async start(controller) {
            const leaderboard = (await getLeaderboard()).map(user => Object.assign(user, {user: {}}));
            let progress = 1/3;
            const progressStep = (2/3) / leaderboard.length;
            const updateProgress = () => controller.enqueue(`data: ${JSON.stringify({ progress })}\n\n`);
            updateProgress();

            for (const elt of leaderboard) {
                const res = await fetch(`https://discord.com/api/v10/users/${elt.id}`, {
                    headers: {
                        Authorization: "Bot " + TOKEN,
                    }
                });
                elt.user = await res.json();
                progress += progressStep;
                updateProgress();
            }

            controller.enqueue(`data: ${JSON.stringify({ complete: true, res: leaderboard })}\n\n`);

            controller.close();
        },
    });

    return new NextResponse(stream, {
        headers: {
            "Content-Type": "text/event-stream",
            "Cache-Control": "no-cache",
            Connection: "keep-alive",
        },
    });
}