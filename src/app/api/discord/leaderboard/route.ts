import { NextResponse } from "next/server";
import { getLeaderboard } from "@/libs/money";

const TOKEN = process.env.DISCORD_TOKEN;

export async function GET(): Promise<NextResponse> {
  if (!TOKEN) {
    return NextResponse.json({ error: "Token is required" }, { status: 500 });
  }

  try {
    const leaderboard = await getLeaderboard();

    const stream = new ReadableStream({
      async start(controller) {
        // Initial progress
        controller.enqueue(`data: ${JSON.stringify({ progress: 0 })}\n\n`);

        if (leaderboard.length === 0) {
          controller.enqueue(
            `data: ${JSON.stringify({ complete: true, res: [] })}\n\n`,
          );
          controller.close();
          return;
        }

        const results = [];
        const BATCH_SIZE = 5;

        for (let i = 0; i < leaderboard.length; i += BATCH_SIZE) {
          const batch = leaderboard.slice(i, i + BATCH_SIZE);
          const batchPromises = batch.map(async (elt) => {
            try {
              const res = await fetch(
                `https://discord.com/api/v10/users/${elt.id}`,
                {
                  headers: { Authorization: "Bot " + TOKEN },
                  next: { revalidate: 3600 },
                },
              );

              if (!res.ok) {
                return {
                  ...elt,
                  user: {
                    global_name: "Inconnu",
                    username: "inconnu",
                    avatar: null,
                  },
                };
              }

              const userData = await res.json();
              return { ...elt, user: userData };
            } catch (error) {
              return {
                ...elt,
                user: {
                  global_name: "Inconnu",
                  username: "inconnu",
                  avatar: null,
                },
              };
            }
          });

          const batchResults = await Promise.all(batchPromises);
          results.push(...batchResults);

          const progress = Math.min(0.9, (i + BATCH_SIZE) / leaderboard.length);
          controller.enqueue(`data: ${JSON.stringify({ progress })}\n\n`);
        }

        controller.enqueue(
          `data: ${JSON.stringify({ complete: true, res: results })}\n\n`,
        );
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
  } catch (error) {
    console.error("[Leaderboard API] Fatal error:", error);
    return NextResponse.json(
      { error: "Internal Server Error" },
      { status: 500 },
    );
  }
}
