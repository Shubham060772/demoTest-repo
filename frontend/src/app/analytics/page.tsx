"use client";

import { useState } from "react";
import { useQuery } from "@apollo/client/react";
import { GET_ANALYTICS_SUMMARY } from "@/graphql/queries/analytics";
import { GET_CHANNELS } from "@/graphql/queries/channels";
import Header from "@/components/layout/Header";
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  BarChart, Bar, PieChart, Pie, Cell, Legend
} from "recharts";
import { TrendingUp, Eye, MousePointer, MessageSquare, AlertCircle } from "lucide-react";

const RADIAN = Math.PI / 180;

function formatNumber(n: number) {
  if (n >= 1_000_000) return (n / 1_000_000).toFixed(1) + "M";
  if (n >= 1_000) return (n / 1_000).toFixed(1) + "K";
  return n?.toString() || "0";
}

const CHART_COLORS = ["#6366f1", "#8b5cf6", "#06b6d4", "#10b981", "#f59e0b", "#ef4444"];

export default function AnalyticsPage() {
  const [channelId, setChannelId] = useState<string | null>(null);

  const channelsQ = useQuery(GET_CHANNELS);
  const { data, loading, error } = useQuery(GET_ANALYTICS_SUMMARY, {
    variables: { channelId: channelId || null },
  });

  const channels = channelsQ.data?.channels || [];
  const summary = data?.analyticsSummary;

  const sentimentData = summary ? [
    { name: "Positive", value: summary.sentimentBreakdown.positive, color: "#10b981" },
    { name: "Neutral", value: summary.sentimentBreakdown.neutral, color: "#6366f1" },
    { name: "Negative", value: summary.sentimentBreakdown.negative, color: "#ef4444" },
  ] : [];

  const dailyData = (summary?.dailyMetrics || []).map((d: any) => ({
    date: new Date(d.date).toLocaleDateString("en-US", { month: "short", day: "numeric" }),
    impressions: d.impressions,
    clicks: d.clicks,
    engagements: d.engagements,
  }));

  const channelPerfData = (summary?.channelPerformance || []).map((cp: any) => ({
    name: cp.channel.name,
    engagementRate: parseFloat(cp.engagementRate.toFixed(2)),
    impressions: cp.impressions,
    color: cp.channel.color,
  }));

  const tooltipStyle = {
    contentStyle: { background: "var(--color-bg-card)", border: "1px solid var(--color-border)", borderRadius: 8 },
    labelStyle: { color: "var(--color-text-primary)" },
    itemStyle: { color: "var(--color-text-secondary)" },
  };

  return (
    <>
      <Header title="Analytics" subtitle="Performance insights across all channels" />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
        {/* Channel Selector */}
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
          <select
            className="input select"
            style={{ maxWidth: 220 }}
            value={channelId || ""}
            onChange={(e) => setChannelId(e.target.value || null)}
          >
            <option value="">All Channels</option>
            {channels.map((ch: any) => (
              <option key={ch.id} value={ch.id}>{ch.name}</option>
            ))}
          </select>
        </div>

        {loading ? (
          <div className="grid-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 120, borderRadius: 16 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load analytics</p>
            <p className="empty-state-description">{error.message}</p>
          </div>
        ) : (
          <>
            {/* KPI Cards */}
            <div className="grid-4">
              {[
                { label: "Total Impressions", value: formatNumber(summary?.totalImpressions || 0), icon: Eye, color: "#6366f1" },
                { label: "Total Clicks", value: formatNumber(summary?.totalClicks || 0), icon: MousePointer, color: "#06b6d4" },
                { label: "Total Engagements", value: formatNumber(summary?.totalEngagements || 0), icon: MessageSquare, color: "#8b5cf6" },
                { label: "Avg Engagement Rate", value: `${(summary?.averageEngagementRate || 0).toFixed(1)}%`, icon: TrendingUp, color: "#10b981" },
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

            <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: "1rem" }}>
              {/* Daily Impressions Chart */}
              <div className="card">
                <h3 style={{ marginBottom: "1.5rem" }}>Daily Impressions & Clicks</h3>
                <ResponsiveContainer width="100%" height={250}>
                  <AreaChart data={dailyData.slice(-30)}>
                    <defs>
                      <linearGradient id="impressGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3} />
                        <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                      </linearGradient>
                      <linearGradient id="clickGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.3} />
                        <stop offset="95%" stopColor="#06b6d4" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(99,102,241,0.08)" />
                    <XAxis dataKey="date" tick={{ fill: "#8892b0", fontSize: 11 }} tickLine={false} axisLine={false} interval={4} />
                    <YAxis tick={{ fill: "#8892b0", fontSize: 11 }} tickLine={false} axisLine={false} tickFormatter={formatNumber} />
                    <Tooltip {...tooltipStyle} />
                    <Area type="monotone" dataKey="impressions" stroke="#6366f1" fill="url(#impressGrad)" strokeWidth={2} name="Impressions" />
                    <Area type="monotone" dataKey="clicks" stroke="#06b6d4" fill="url(#clickGrad)" strokeWidth={2} name="Clicks" />
                  </AreaChart>
                </ResponsiveContainer>
              </div>

              {/* Sentiment Pie */}
              <div className="card">
                <h3 style={{ marginBottom: "1.5rem" }}>Sentiment Breakdown</h3>
                <ResponsiveContainer width="100%" height={200}>
                  <PieChart>
                    <Pie data={sentimentData} cx="50%" cy="50%" innerRadius={50} outerRadius={80} paddingAngle={4} dataKey="value">
                      {sentimentData.map((entry, i) => (
                        <Cell key={i} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={{ background: "var(--color-bg-card)", border: "1px solid var(--color-border)", borderRadius: 8 }} />
                  </PieChart>
                </ResponsiveContainer>
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

            {/* Channel Performance Bar Chart */}
            <div className="card">
              <h3 style={{ marginBottom: "1.5rem" }}>Channel Engagement Rate Comparison</h3>
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={channelPerfData} barSize={32}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(99,102,241,0.08)" vertical={false} />
                  <XAxis dataKey="name" tick={{ fill: "#8892b0", fontSize: 12 }} tickLine={false} axisLine={false} />
                  <YAxis tick={{ fill: "#8892b0", fontSize: 12 }} tickLine={false} axisLine={false} tickFormatter={(v) => `${v}%`} />
                  <Tooltip {...tooltipStyle} formatter={(v: any) => [`${Number(v).toFixed(1)}%`, "Engagement Rate"]} />
                  <Bar dataKey="engagementRate" radius={[6, 6, 0, 0]}>
                    {channelPerfData.map((entry: any, index: number) => (
                      <Cell key={index} fill={entry.color || CHART_COLORS[index % CHART_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </>
        )}
      </div>
    </>
  );
}
