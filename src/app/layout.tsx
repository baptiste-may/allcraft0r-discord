import type { Metadata } from "next";
import "./globals.css";
import { ReactNode, Suspense } from "react";
import Header from "@/components/Header";
import PageTransitionHandler from "@/components/PageTransition";
import { Providers } from "@/components/Providers";

export const metadata: Metadata = {
  title: "Allcraft0r's Discord",
  description: "Site web pour le discord d'Allcraft0r",
  icons: "/logo.webp",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  return (
    <html lang="fr" className="dark" suppressHydrationWarning>
      <body className="min-h-screen bg-background font-sans antialiased">
        <Providers>
          <Header />
          <main className="container mx-auto my-8 px-4 w-screen h-full">
            <Suspense fallback={<div>Chargement...</div>}>
              <PageTransitionHandler>{children}</PageTransitionHandler>
            </Suspense>
          </main>
        </Providers>
      </body>
    </html>
  );
}
