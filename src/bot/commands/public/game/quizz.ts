import {ChatInputCommandInteraction, SlashCommandBuilder} from "discord.js";

/**
 * Decodes a Base64 encoded string into a UTF-8 string.
 *
 * @param input - The Base64 encoded string to decode.
 * @returns The decoded UTF-8 string.
 */
function decodeBase64(input: string) {
    const buffer = Buffer.from(input, "base64");
    return buffer.toString("utf8");
}

type Question = {
    type: "boolean" | "multiple";
    question: string;
    correct_answer: string;
    incorrect_answers: string[];
    difficulty: "easy" | "medium" | "hard";
    category: string;
};

/**
 * Fetches a random question from the Open Trivia Database.
 *
 * @param requestedType The type of question to fetch. If `null`, a random question type will be chosen.
 * @returns The fetched question.
 */
async function getRandomQuestion(requestedType: null | "boolean" | "multiple"): Promise<Question> {
    const res = await fetch(`https://opentdb.com/api.php?amount=1&difficulty=easy&encode=base64${requestedType === null ? "" : ("&type=" + requestedType)}`);
    // eslint-disable-next-line prefer-const
    const {type, question, correct_answer, incorrect_answers, difficulty, category} = (await res.json()).results[0] as Question;
    return {
        type: decodeBase64(type) as "boolean" | "multiple",
        question: decodeBase64(question),
        correct_answer: decodeBase64(correct_answer),
        incorrect_answers: incorrect_answers.map(decodeBase64),
        difficulty: decodeBase64(difficulty) as "easy" | "medium" | "hard",
        category: decodeBase64(category)
    };
}

module.exports = {
    data: new SlashCommandBuilder()
        .setName("quizz")
        .setDescription("Lance une question de culture général")
        .addStringOption(option => option
            .setName("type")
            .setDescription("Le type de question")
            .addChoices(
                {
                    name: "Vrai / Faux",
                    value: "boolean"
                },
                {
                    name: "Choix multiples",
                    value: "multiple"
                }
            )),
    async execute(interaction: ChatInputCommandInteraction) {

        const optionType = interaction.options.getString("type", false) as null | "boolean" | "multiple";
        const {type, question, correct_answer, incorrect_answers} = await getRandomQuestion(optionType);

        await interaction.reply({
            poll: {
                question: {
                    text: question
                },
                answers: type === "boolean" ? [
                    {
                        text: "Vrai",
                        emoji: "✅"
                    },
                    {
                        text: "Faux",
                        emoji: "❌"
                    }
                ] : [...incorrect_answers, correct_answer].map((text) => ({
                    text,
                })),
                allowMultiselect: false,
                duration: 1
            },
            content: `Réponse: ||${type === "multiple" ? correct_answer : (correct_answer === "True" ? "✅ Vrai" : "❌ Faux")}||`
        });
    }
};