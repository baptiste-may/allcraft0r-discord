import {Image} from "@nextui-org/react";

export function RedstoneEmoji({size}: {
    size: number;
}) {
    return <Image
        src="/api/discord/emoji"
        alt="redstone-emoji"
        width={size}
        height={size}
        className="object-contain"
    />;
}