import type {Metadata} from "next";
import "./globals.css";
import {ReactNode} from "react";
import {NextUIProvider} from "@nextui-org/react";
import Header from "@/components/Header";
import {ThemeProvider} from "next-themes";
import {AuthProvider} from "@/components/AuthProvider";
import PageTransitionHandler from "@/components/PageTransition";

export const metadata: Metadata = {
    title: "Allcraft0r's Discord",
    description: "Site web pour le discord d'Allcraft0r",
    icons: "/logo.webp"
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: ReactNode;
}>) {
    return (
        <html lang="fr" className="dark" suppressHydrationWarning>
        <body>
        <NextUIProvider>
            <ThemeProvider attribute="class" defaultTheme="dark">
                <AuthProvider>
                    <Header/>
                    <div className="container mx-auto my-8 px-4 w-screen h-full">
                        <PageTransitionHandler>
                            {children}
                        </PageTransitionHandler>
                    </div>
                </AuthProvider>
            </ThemeProvider>
        </NextUIProvider>
        </body>
        </html>
    );
}
