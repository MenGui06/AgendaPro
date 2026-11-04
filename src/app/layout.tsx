import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AgendaPro",
  description: "Calendário de agendamentos",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}
