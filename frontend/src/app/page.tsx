"use client";

import { useQuery } from "@apollo/client/react";
import { GET_DASHBOARD } from "@/graphql/queries/analytics";
import Header from "@/components/layout/Header";
import {
  Users, Megaphone, MessageSquare, Radio,
  TrendingUp, AlertCircle, ArrowUpRight, ArrowDownRight
} from "lucide-react";
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell
} from "recharts";
import styles from "./page.module.css";
import { can } from "@/lib/acl";
import { isOn } from "@/lib/featureFlags";

function getStatusBadgeClass(status: string) {
  const map: Record<string, string> = {
    ACTIVE: "badge-success",
    DRAFT: "badge-neutral",
    SCHEDULED: "badge-info",
    PAUSED: "badge-warning",
    COMPLETED: "badge-primary",
    CANCELLED: "badge-danger",
  };
  return `badge ${map[status] || "badge-neutral"}`;
}

function getSentimentClass(sentiment: string) {
  const map: Record<string, string> = {
    POSITIVE: "badge-success",
    NEUTRAL: "badge-neutral",
    NEGATIVE: "badge-danger",
  };
  return `badge ${map[sentiment] || "badge-neutral"}`;
}

function getChannelIcon(type: string) {
  const icons: Record<string, string> = {
    TWITTER: "𝕏", INSTAGRAM: "📷", FACEBOOK: "f",
    LINKEDIN: "in", YOUTUBE: "▶", EMAIL: "✉", SMS: "💬", WHATSAPP: "✅",
  };
  return icons[type] || "?";
}

function formatNumber(n: number) {
  if (n >= 1_000_000) return (n / 1_000_000).toFixed(1) + "M";
  if (n >= 1_000) return (n / 1_000).toFixed(1) + "K";
  return n.toString();
}

export default function DashboardPage() {
  // Permission & feature-flag checks
  const canViewAnalytics = can('p:analytics_view');
  const canViewUsers = can('p:user_view');
  const canViewCampaigns = can('p:campaign_view');
  const showSmartRecommendations = isOn('SMART_RECOMMENDATIONS');
  const showCampaignDashboardV2 = isOn('CAMPAIGN_DASHBOARD_V2');
  const showAdvancedAnalytics = can('p:advanced_analytics') && isOn('ADVANCED_ANALYTICS');

  const { data, loading, error } = useQuery(GET_DASHBOARD, {
    skip: !canViewAnalytics,
  });

  if (loading) {
    return (
      <>
        <Header title="Dashboard" subtitle="Welcome back, Alice" />
        <div className={styles.loadingGrid}>
          {[...Array(4)].map((_, i) => (
            <div key={i} className="skeleton" style={{ height: 120, borderRadius: 16 }} />
          ))}
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Header title="Dashboard" />
        <div className="empty-state">
          <div className="empty-state-icon"><AlertCircle size={28} /></div>
          <p className="empty-state-title">Failed to load dashboard</p>
          <p className="empty-state-description">{error.message}</p>
        </div>
      </>
    );
  }

  const db = data?.dashboard;

  const stats = [
    { label: "Total Customers", value: formatNumber(db?.totalCustomers || 0), icon: Users, color: "#6366f1", change: "+12%", up: true },
    { label: "Active Campaigns", value: db?.activeCampaigns || 0, icon: Megaphone, color: "#8b5cf6", change: "+5%", up: true },
    { label: "Total Messages", value: formatNumber(db?.totalMessages || 0), icon: MessageSquare, color: "#06b6d4", change: `${db?.unreadMessages || 0} unread`, up: false },
    { label: "Channels", value: db?.totalChannels || 0, icon: Radio, color: "#10b981", change: `${(db?.overallEngagementRate || 0).toFixed(1)}% engagement`, up: true },
  ];

  const sentimentData = [
    { name: "Positive", value: db?.recentMessages?.filter((m: any) => m.sentiment === "POSITIVE").length || 0, color: "#10b981" },
    { name: "Neutral", value: db?.recentMessages?.filter((m: any) => m.sentiment === "NEUTRAL").length || 0, color: "#6366f1" },
    { name: "Negative", value: db?.recentMessages?.filter((m: any) => m.sentiment === "NEGATIVE").length || 0, color: "#ef4444" },
  ];

  return (
    <>
      <Header title="Dashboard" subtitle="Welcome back, Alice Chen" />

      <div className={styles.page}>
        {/* Stats Grid */}
        <div className={styles.statsGrid}>
          {stats.map((stat) => (
            <div key={stat.label} className="stat-card">
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1rem" }}>
                <div
                  style={{
                    width: 40, height: 40, borderRadius: 10,
                    background: `${stat.color}20`,
                    border: `1px solid ${stat.color}40`,
                    display: "flex", alignItems: "center", justifyContent: "center",
                    color: stat.color,
                  }}
                >
                  <stat.icon size={18} />
                </div>
                <span className={`stat-change ${stat.up ? "up" : ""}`} style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  {stat.up ? <ArrowUpRight size={12} /> : <ArrowDownRight size={12} />}
                  {stat.change}
                </span>
              </div>
              <div className="stat-value">{stat.value}</div>
              <div className="stat-label">{stat.label}</div>
            </div>
          ))}
        </div>

        <div className={styles.mainGrid}>
          {/* Channel Performance */}
          <div className="card" style={{ gridColumn: "span 2" }}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.5rem" }}>
              <div>
                <h3 style={{ marginBottom: 4 }}>Channel Performance</h3>
                <p style={{ fontSize: "0.8125rem", color: "var(--color-text-secondary)" }}>Engagement rate by channel</p>
              </div>
            </div>
            <div style={{ display: "grid", gap: "0.75rem" }}>
              {db?.channelBreakdown?.slice(0, 5).map((cp: any) => (
                <div key={cp.channel.id} style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
                  <div className="channel-icon" style={{ background: `${cp.channel.color}20`, color: cp.channel.color }}>
                    {getChannelIcon(cp.channel.type)}
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "0.375rem" }}>
                      <span style={{ fontSize: "0.875rem", fontWeight: 500 }}>{cp.channel.name}</span>
                      <span style={{ fontSize: "0.8125rem", color: "var(--color-text-secondary)" }}>{cp.engagementRate.toFixed(1)}%</span>
                    </div>
                    <div className="progress-bar">
                      <div className="progress-bar-fill" style={{ width: `${Math.min(cp.engagementRate * 10, 100)}%` }} />
                    </div>
                  </div>
                  <span style={{ fontSize: "0.8125rem", color: "var(--color-text-muted)", minWidth: 60, textAlign: "right" }}>
                    {formatNumber(cp.impressions)} imp
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Sentiment Breakdown */}
          <div className="card">
            <h3 style={{ marginBottom: "1.5rem" }}>Sentiment Overview</h3>
            <div style={{ display: "flex", justifyContent: "center", marginBottom: "1rem" }}>
              <PieChart width={160} height={160}>
                <Pie data={sentimentData} cx={80} cy={80} innerRadius={45} outerRadius={70} paddingAngle={3} dataKey="value">
                  {sentimentData.map((entry, index) => (
                    <Cell key={index} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{ background: "var(--color-bg-card)", border: "1px solid var(--color-border)", borderRadius: 8 }}
                  labelStyle={{ color: "var(--color-text-primary)" }}
                />
              </PieChart>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
              {sentimentData.map((s) => (
                <div key={s.name} style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                  <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <div style={{ width: 8, height: 8, borderRadius: "50%", background: s.color }} />
                    <span style={{ fontSize: "0.8125rem", color: "var(--color-text-secondary)" }}>{s.name}</span>
                  </div>
                  <span style={{ fontSize: "0.8125rem", fontWeight: 500 }}>{s.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className={styles.bottomGrid}>
          {/* Recent Messages — gated by p:user_view */}
          {canViewUsers && (
          <div className="card">
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
              <h3>Recent Messages</h3>
              <a href="/messages" className="btn btn-ghost btn-sm">View all</a>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
              {db?.recentMessages?.slice(0, 5).map((msg: any) => (
                <div key={msg.id} style={{
                  display: "flex", gap: "0.75rem", padding: "0.75rem",
                  background: "var(--color-bg-elevated)",
                  borderRadius: "var(--radius-md)",
                  border: "1px solid var(--color-border)"
                }}>
                  <div className="avatar avatar-sm" style={{ background: msg.channel.color + "30", color: msg.channel.color }}>
                    {getChannelIcon(msg.channel.type)}
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 4 }}>
                      <span style={{ fontSize: "0.8125rem", fontWeight: 500 }}>
                        {msg.customer ? `${msg.customer.firstName} ${msg.customer.lastName}` : "Anonymous"}
                      </span>
                      <span className={getSentimentClass(msg.sentiment)}>{msg.sentiment}</span>
                    </div>
                    <p style={{ fontSize: "0.8125rem", color: "var(--color-text-secondary)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                      {msg.body}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
          )}

          {/* Top Campaigns — gated by p:campaign_view + SMART_RECOMMENDATIONS */}
          {canViewCampaigns && showSmartRecommendations && (
          <div className="card">
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
              <h3>Top Campaigns</h3>
              <a href="/campaigns" className="btn btn-ghost btn-sm">View all</a>
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
              {db?.topCampaigns?.slice(0, 5).map((campaign: any, i: number) => (
                <div key={campaign.id} style={{
                  display: "flex", alignItems: "center", gap: "0.75rem",
                  padding: "0.75rem",
                  background: "var(--color-bg-elevated)",
                  borderRadius: "var(--radius-md)",
                  border: "1px solid var(--color-border)"
                }}>
                  <div style={{
                    width: 28, height: 28, borderRadius: "50%",
                    background: "var(--color-brand-gradient)",
                    display: "flex", alignItems: "center", justifyContent: "center",
                    fontSize: "0.75rem", fontWeight: 700, color: "#fff", flexShrink: 0
                  }}>{i + 1}</div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <p style={{ fontSize: "0.8125rem", fontWeight: 500, marginBottom: 2, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                      {campaign.name}
                    </p>
                    <p style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
                      {formatNumber(campaign.impressions)} impressions · {campaign.engagementRate.toFixed(1)}% engagement
                    </p>
                  </div>
                  <span className={getStatusBadgeClass(campaign.status)}>{campaign.status}</span>
                </div>
              ))}
            </div>
          </div>
          )}
        </div>
      </div>
    </>
  );
}
