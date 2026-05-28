import type { Metadata } from "next";
import "./globals.css";
import { ApolloClientProvider } from "@/lib/apollo-provider";
import AppShell from "@/components/layout/AppShell";

export const metadata: Metadata = {
  title: {
    default: "Nexus CXM — Unified Customer Experience Platform",
    template: "%s | Nexus CXM",
  },
  description:
    "Nexus CXM is a corporate-grade unified customer experience management platform for managing customers, campaigns, channels, and analytics.",
  keywords: ["CXM", "CRM", "marketing", "analytics", "customer experience"],
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>
        <ApolloClientProvider>
          <AppShell>{children}</AppShell>
        </ApolloClientProvider>
      </body>
    </html>
  );
}
