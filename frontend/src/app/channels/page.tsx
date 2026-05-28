"use client";

import { useQuery } from "@apollo/client/react";
import { GET_CHANNELS } from "@/graphql/queries/channels";
import Header from "@/components/layout/Header";
import { Radio, AlertCircle, CheckCircle2, XCircle } from "lucide-react";

function getChannelIcon(type: string) {
  const icons: Record<string, string> = {
    TWITTER: "𝕏", INSTAGRAM: "📷", FACEBOOK: "f",
    LINKEDIN: "in", YOUTUBE: "▶", EMAIL: "✉", SMS: "💬", WHATSAPP: "✅", TIKTOK: "♪",
  };
  return icons[type] || "?";
}

function formatFollowers(n: number) {
  if (n >= 1_000_000) return (n / 1_000_000).toFixed(1) + "M";
  if (n >= 1_000) return (n / 1_000).toFixed(1) + "K";
  return n.toString();
}

export default function ChannelsPage() {
  const { data, loading, error } = useQuery(GET_CHANNELS);

  const channels = data?.channels || [];

  return (
    <>
      <Header title="Channels" subtitle={`${channels.length} connected channels`} />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        {loading ? (
          <div className="grid-3">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 180, borderRadius: 16 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load channels</p>
            <p className="empty-state-description">{error.message}</p>
          </div>
        ) : (
          <div className="grid-3">
            {channels.map((ch: any) => (
              <div key={ch.id} className="card" style={{ position: "relative", overflow: "hidden" }}>
                {/* Color accent bar */}
                <div style={{ position: "absolute", top: 0, left: 0, right: 0, height: 3, background: ch.color }} />

                <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", marginBottom: "1rem", paddingTop: "0.25rem" }}>
                  <div style={{
                    width: 52, height: 52, borderRadius: 14,
                    background: `${ch.color}20`, border: `1px solid ${ch.color}30`,
                    display: "flex", alignItems: "center", justifyContent: "center",
                    fontSize: "1.25rem", color: ch.color, fontWeight: 700,
                  }}>
                    {getChannelIcon(ch.type)}
                  </div>
                  <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                    {ch.isActive ? (
                      <span className="badge badge-success"><CheckCircle2 size={10} /> Active</span>
                    ) : (
                      <span className="badge badge-danger"><XCircle size={10} /> Inactive</span>
                    )}
                  </div>
                </div>

                <h3 style={{ marginBottom: "0.25rem" }}>{ch.name}</h3>
                <p style={{ fontSize: "0.8125rem", color: "var(--color-text-muted)", marginBottom: "1rem" }}>{ch.handle}</p>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.75rem", paddingTop: "1rem", borderTop: "1px solid var(--color-border)" }}>
                  <div>
                    <div style={{ fontSize: "0.6875rem", color: "var(--color-text-muted)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.05em" }}>Followers</div>
                    <div style={{ fontWeight: 700, fontSize: "1.125rem" }}>{formatFollowers(ch.followerCount)}</div>
                  </div>
                  <div>
                    <div style={{ fontSize: "0.6875rem", color: "var(--color-text-muted)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.05em" }}>Type</div>
                    <div style={{ fontWeight: 600, fontSize: "0.875rem", color: ch.color }}>{ch.type}</div>
                  </div>
                </div>

                <div style={{ marginTop: "0.75rem", fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
                  Connected {new Date(ch.connectedAt).toLocaleDateString()}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
