import type {Config} from "tailwindcss";
import {nextui} from "@nextui-org/react";

export default {
    content: [
        "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
        "./node_modules/@nextui-org/theme/dist/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                background: "var(--background)",
                foreground: "var(--foreground)",
            },
        },
    },
    plugins: [nextui({
        themes: {
            light: {
                colors: {
                    primary: {
                        "50": "#FEE7EF",
                        "100": "#FDD0DF",
                        "200": "#FAA0BF",
                        "300": "#F871A0",
                        "400": "#F54180",
                        "500": "#F31260",
                        "600": "#C20E4D",
                        "700": "#920B3A",
                        "800": "#610726",
                        "900": "#310413",
                        DEFAULT: "#F54180"
                    }
                }
            },
            dark: {
                colors: {
                    primary: {
                        "50": "#FEE7EF",
                        "100": "#FDD0DF",
                        "200": "#FAA0BF",
                        "300": "#F871A0",
                        "400": "#F54180",
                        "500": "#F31260",
                        "600": "#C20E4D",
                        "700": "#920B3A",
                        "800": "#610726",
                        "900": "#310413",
                        DEFAULT: "#C20E4D"
                    }
                }
            }
        }
    })],
    darkMode: "class",
} satisfies Config;