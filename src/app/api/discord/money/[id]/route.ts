import {NextRequest, NextResponse} from "next/server";
import {getMoney} from "@/libs/money";

export async function GET(_: NextRequest, {params}: {
    params: Promise<{
        id: string;
    }>
}): Promise<NextResponse> {
    const {id} = await params;
    const money = await getMoney(id);
    return NextResponse.json(money);
}