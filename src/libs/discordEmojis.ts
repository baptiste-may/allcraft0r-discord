/**
 * Redstone Emoji in Discord
 */
export const redstoneEmoji = (() => {
    const REDSTONE_EMOJI_ID = process.env.REDSTONE_EMOJI_ID;
    if (!REDSTONE_EMOJI_ID) throw new Error("REDSTONE_EMOJI_ID is required");
    return `<:redstone:${REDSTONE_EMOJI_ID}>`;
})();