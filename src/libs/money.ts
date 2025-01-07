import prisma from "@/libs/prisma";
import {isSameDay} from "date-fns";

export const DEFAULT_MONEY = parseInt(process.env.DEFAULT_MONEY || "250");
export const DAILY_MONEY = parseInt(process.env.DAILY_MONEY || "100");

/**
 * Fetch user money
 * @param id {String} User ID
 * @return Money of the user
 */
export async function getMoney(id: string): Promise<number> {
    const money = await prisma.user.findUnique({
        where: {
            id,
        },
        select: {
            money: true,
        }
    });

    if (money) return money.money;

    await prisma.user.create({
        data: {
            id,
            money: DEFAULT_MONEY,
        }
    });

    return DEFAULT_MONEY;
}

/**
 * Add money to user
 * @param id {String} User ID
 * @param amount {number} Amount of money
 */
export async function addMoney(id: string, amount: number) {
    const money = await getMoney(id);

    await prisma.user.update({
        where: {
            id,
        },
        data: {
            money: money + amount,
        }
    });
}

/**
 * Get money leaderboard
 * @return The leaderboard
 */
export async function getLeaderboard(): Promise<{ id: string; money: number; }[]> {
    return await prisma.user.findMany({
        orderBy: {
            money: "desc"
        },
        select: {
            id: true,
            money: true,
        }
    });
}

/**
 * Execute daily command
 * @param id {String} User ID
 * @return true if the money was given
 */
export async function executeDaily(id: string): Promise<boolean> {
    const lastDaily = await prisma.user.findUnique({
        where: {
            id,
        },
        select: {
            lastDaily: true,
        }
    });

    if (!lastDaily || !lastDaily.lastDaily || !isSameDay(new Date(lastDaily.lastDaily), new Date())) {
        await prisma.user.update({
            where: {
                id,
            },
            data: {
                lastDaily: new Date(),
            }
        });
        await addMoney(id, DAILY_MONEY);
        return true;
    }

    return false;
}