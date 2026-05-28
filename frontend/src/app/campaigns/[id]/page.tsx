"use client";

import { useQuery } from "@apollo/client/react";
import { GET_CAMPAIGN } from "@/graphql/queries/campaigns";
import Header from "@/components/layout/Header";
import { ArrowLeft, AlertCircle, Target, TrendingUp, MousePointer, Eye } from "lucide-react";
import Link from "next/link";
import { use } from "react";

function getStatusBadgeClass(status: string) {
  const map: Record<string, string> = {
    ACTIVE: "badge-success", DRAFT: "badge-neutral", SCHEDULED: "badge-info",
    PAUSED: "badge-warning", COMPLETED: "badge-primary", CANCELLED: "badge-danger",
  };
  return `badge ${map[status] || "badge-neutral"}`;
}

function formatCurrency(n: number) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD", maximumFractionDigits: 0 }).format(n);
}

function formatNumber(n: number) {
  if (n >= 1_000_000) return (n / 1_000_000).toFixed(1) + "M";
  if (n >= 1_000) return (n / 1_000).toFixed(1) + "K";
  return n.toString();
}

export default function CampaignDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const { data, loading, error } = useQuery(GET_CAMPAIGN, { variables: { id } });

  if (loading) return (
    <>
      <Header title="Campaign Detail" />
      <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
        <div className="skeleton" style={{ height: 160, borderRadius: 16 }} />
        <div className="skeleton" style={{ height: 300, borderRadius: 16 }} />
      </div>
    </>
  );

  if (error || !data?.campaign) return (
    <>
      <Header title="Campaign Not Found" />
      <div className="empty-state">
        <div className="empty-state-icon"><AlertCircle size={28} /></div>
        <p className="empty-state-title">Campaign not found</p>
        <Link href="/campaigns"><button className="btn btn-secondary" style={{ marginTop: "1rem" }}>Back to Campaigns</button></Link>
      </div>
    </>
  );

  const c = data.campaign;

  return (
    <>
      <Header title={c.name} subtitle="Campaign Details" />
      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        <Link href="/campaigns">
          <button className="btn btn-ghost btn-sm"><ArrowLeft size={14} /> Back to Campaigns</button>
        </Link>

        {/* Header Card */}
        <div className="card">
          <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: "1rem" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", marginBottom: "0.5rem" }}>
                <h2>{c.name}</h2>
                <span className={getStatusBadgeClass(c.status)}>{c.status}</span>
              </div>
              <p style={{ fontSize: "0.875rem" }}>{c.description || "No description provided."}</p>
              {c.targetAudience && (
                <p style={{ fontSize: "0.8125rem", color: "var(--color-brand-primary)", marginTop: "0.5rem" }}>
                  🎯 {c.targetAudience}
                </p>
              )}
            </div>
            <div style={{ textAlign: "right", fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
              {c.startDate && <div>Started: {new Date(c.startDate).toLocaleDateString()}</div>}
              {c.endDate && <div>Ends: {new Date(c.endDate).toLocaleDateString()}</div>}
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid-4">
          {[
            { label: "Impressions", value: formatNumber(c.impressions), icon: Eye, color: "#6366f1" },
            { label: "Clicks", value: formatNumber(c.clicks), icon: MousePointer, color: "#06b6d4" },
            { label: "Engagement Rate", value: `${c.engagementRate.toFixed(1)}%`, icon: TrendingUp, color: "#10b981" },
            { label: "Budget Spent", value: c.budget ? `${((c.spentBudget / c.budget) * 100).toFixed(0)}%` : "N/A", icon: Target, color: "#f59e0b" },
          ].map((stat) => (
            <div key={stat.label} className="stat-card">
              <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", marginBottom: "0.75rem" }}>
                <div style={{ width: 36, height: 36, borderRadius: 8, background: `${stat.color}20`, border: `1px solid ${stat.color}30`, display: "flex", alignItems: "center", justifyContent: "center", color: stat.color }}>
                  <stat.icon size={16} />
                </div>
              </div>
              <div className="stat-value" style={{ fontSize: "1.5rem" }}>{stat.value}</div>
              <div className="stat-label">{stat.label}</div>
            </div>
          ))}
        </div>

        {/* Budget & Channels */}
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
          {c.budget && (
            <div className="card">
              <h3 style={{ marginBottom: "1.25rem" }}>Budget Overview</h3>
              <div style={{ marginBottom: "0.75rem" }}>
                <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "0.5rem" }}>
                  <span style={{ fontSize: "0.875rem", color: "var(--color-text-secondary)" }}>Spent</span>
                  <span style={{ fontWeight: 600 }}>{formatCurrency(c.spentBudget)}</span>
                </div>
                <div className="progress-bar" style={{ height: 8 }}>
                  <div className="progress-bar-fill" style={{ width: `${Math.min((c.spentBudget / c.budget) * 100, 100)}%` }} />
                </div>
              </div>
              <div style={{ display: "flex", justifyContent: "space-between", fontSize: "0.8125rem" }}>
                <span style={{ color: "var(--color-text-muted)" }}>Total Budget</span>
                <span style={{ color: "var(--color-text-primary)", fontWeight: 500 }}>{formatCurrency(c.budget)}</span>
              </div>
            </div>
          )}

          <div className="card">
            <h3 style={{ marginBottom: "1.25rem" }}>Channels ({c.channels?.length || 0})</h3>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.625rem" }}>
              {c.channels?.map((ch: any) => (
                <div key={ch.id} style={{ display: "flex", alignItems: "center", gap: "0.75rem", padding: "0.5rem 0.75rem", background: "var(--color-bg-elevated)", borderRadius: "var(--radius-md)", border: "1px solid var(--color-border)" }}>
                  <div className="channel-icon" style={{ background: `${ch.color}20`, color: ch.color, width: 28, height: 28 }}>
                    {ch.type.charAt(0)}
                  </div>
                  <div>
                    <div style={{ fontWeight: 500, fontSize: "0.875rem" }}>{ch.name}</div>
                    <div style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{ch.handle} · {(ch.followerCount || 0).toLocaleString()} followers</div>
                  </div>
                </div>
              ))}
              {!c.channels?.length && <p style={{ color: "var(--color-text-muted)", fontSize: "0.875rem" }}>No channels assigned</p>}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
