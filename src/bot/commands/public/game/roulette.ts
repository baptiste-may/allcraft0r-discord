import {
    AttachmentBuilder,
    ChatInputCommandInteraction,
    Colors,
    EmbedBuilder,
    SlashCommandBuilder
} from "discord.js";
import {redstoneEmoji} from "@/libs/discordEmojis";
import {createCanvas} from "@napi-rs/canvas";
import {delay, randomNumber} from "@/utils";
import {addMoney, getMoney} from "@/libs/money";

const DEFAULT_MISE = 50;
const categoriesText: Record<string, string> = {
    red: "sur le rouge !",
    black: "sur le noir !",
    green: "sur le vert !",
    even: "sur les nombres pairs !",
    odd: "sur les nombres impairs !"
};
const NUMBERS_WITH_ZERO = [
    6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26, 0, 32, 15, 19, 4, 21, 2, 25, 17, 34
];
const NUMBERS_WITHOUT_ZERO = NUMBERS_WITH_ZERO.filter(nb => nb !== 0);

function calculateGain(mise: number, category: string, resultNb: number) {
    if (resultNb === 0) {
        if (category === "green") return mise * 35;
        return 0;
    }
    switch (category) {
        case "red":
            if (NUMBERS_WITHOUT_ZERO.indexOf(resultNb) % 2 === 1) return mise * 2;
            else return 0;
        case "black":
            if (NUMBERS_WITHOUT_ZERO.indexOf(resultNb) % 2 === 0) return mise * 2;
            else return 0;
        case "even":
            if (resultNb % 2 === 0) return mise * 2;
            else return 0;
        case "odd":
            if (resultNb % 2 === 1) return mise * 2;
            else return 0;
    }
    return 0;
}

const IMAGE_SIZE = 500;
const CENTER = IMAGE_SIZE / 2;
const PADDING = 40;
const createRouletteImage = async (resultNb: number) => {
    const canvas = createCanvas(IMAGE_SIZE, IMAGE_SIZE);
    const ctx = canvas.getContext("2d");

    ctx.fillStyle = "#004400"
    ctx.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

    ctx.beginPath();
    ctx.arc(CENTER, CENTER, CENTER - PADDING + 20, 0, Math.PI * 2);
    ctx.fillStyle = "#D4AF37";
    ctx.fill();

    const rotation = randomNumber(360) / 180 * Math.PI;
    let isDark = true;
    const anglePerSection = (2 * Math.PI) / NUMBERS_WITH_ZERO.length;
    for (let i = 0; i < NUMBERS_WITH_ZERO.length; i++) {
        const nb = NUMBERS_WITH_ZERO[i];
        const color = nb === 0 ? "#00AA00" : (isDark ? "#000000" : "#FF0000");
        const startAngle = i * anglePerSection + rotation;
        const endAngle = (i + 1) * anglePerSection + rotation;
        const middleAngle = startAngle + anglePerSection / 2;

        ctx.beginPath();
        ctx.moveTo(CENTER, CENTER);
        ctx.arc(CENTER, CENTER, CENTER - PADDING, startAngle, endAngle);
        ctx.closePath();
        ctx.fillStyle = color;
        ctx.fill();
        ctx.strokeStyle = "#FFFFFF";
        ctx.lineWidth = 3;
        ctx.stroke();

        ctx.save();
        ctx.translate(CENTER, CENTER);
        ctx.rotate(middleAngle);
        ctx.translate(IMAGE_SIZE / 2.3 - PADDING, 0);
        ctx.rotate(Math.PI / 2);

        ctx.fillStyle = "white";
        ctx.font = "bold 16px Arial";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(nb.toString(), 0, 0);

        if (nb === resultNb) {
            ctx.restore();
            const distance = IMAGE_SIZE / 3 - PADDING;
            const x = CENTER + Math.cos(middleAngle) * distance;
            const y = CENTER + Math.sin(middleAngle) * distance;
            ctx.beginPath();
            ctx.arc(x, y, 6, 0, Math.PI * 2);
            const gradent = ctx.createRadialGradient(
                x - 2, y - 2, 1,
                x, y, 8
            );
            gradent.addColorStop(0, "#FFFFFF");
            gradent.addColorStop(0.3, "#F0F0F0");
            gradent.addColorStop(0.7, "#D0D0D0");
            gradent.addColorStop(1, "#A0A0A0");
            ctx.fillStyle = gradent;
            ctx.fill();
        }

        if (nb !== 0) isDark = !isDark;
        ctx.restore();
    }

    ctx.beginPath();
    ctx.arc(CENTER, CENTER, IMAGE_SIZE / 2.65 - PADDING, 0, Math.PI * 2);
    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 3;
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(CENTER, CENTER, IMAGE_SIZE / 3.5 - PADDING, 0, Math.PI * 2);
    ctx.fillStyle = "#B8941F";
    ctx.fill();
    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 3;
    ctx.stroke();

    return await canvas.encode("webp");
}

module.exports = {
    data: new SlashCommandBuilder()
        .setName("roulette")
        .setDescription("Lance un tour de roulette")
        .addStringOption(option => option
            .setName("category")
            .setDescription("La catégorie choisis")
            .setRequired(true)
            .addChoices(
                {
                    name: "Couleur Rouge",
                    value: "red"
                },
                {
                    name: "Couleur Noir",
                    value: "black"
                },
                {
                    name: "Couleur Vert",
                    value: "green"
                },
                {
                    name: "Nombres Pairs",
                    value: "even"
                },
                {
                    name: "Nombres Impairs",
                    value: "odd"
                }
            ))
        .addNumberOption(option => option
            .setName("mise")
            .setDescription("La mise de départ")
            .setRequired(false)
            .setMinValue(25)),
    async execute(interaction: ChatInputCommandInteraction) {

        const category = interaction.options.getString("category", true);
        const mise = parseInt(String(interaction.options.getNumber("mise", false) || DEFAULT_MISE));

        const userMoney = await getMoney(interaction.user.id);
        if (userMoney < mise) return interaction.reply({
            content: "❌ Tu n'as pas assez de redstone !",
            ephemeral: true
        });

        await addMoney(interaction.user.id, -mise);

        const embed = new EmbedBuilder()
            .setTitle("🪩 Les jeux sont faits ! 🪩")
            .setDescription(`Mise : ${mise} ${redstoneEmoji} ${categoriesText[category]}`)
            .setColor(Colors.DarkGreen)
            .setImage("https://i.giphy.com/26uf2YTgF5upXUTm0.webp");

        await interaction.reply({
            embeds: [embed]
        });

        await delay(3000);

        const res = randomNumber(36);
        const resColor = res === 0 ? "🟢" : (NUMBERS_WITHOUT_ZERO.indexOf(res) % 2 === 0 ? "⚫️" : "🔴");
        const image = await createRouletteImage(res);
        const imageAttachment = new AttachmentBuilder(image, {name: "result.png"});
        const gain = calculateGain(mise, category, res);

        await addMoney(interaction.user.id, gain);

        embed
            .setTitle(gain === 0 ? "🪩 Dommage ! 🪩" : "🪩 Bravo ! 🪩")
            .setImage("attachment://result.png")
            .setDescription(`Mise : ${mise} ${redstoneEmoji} ${categoriesText[category]}\nRésultat : ${resColor} ${res}\nGain : ${gain} ${redstoneEmoji}`);

        await interaction.editReply({
            embeds: [embed],
            files: [imageAttachment]
        });
    }
};