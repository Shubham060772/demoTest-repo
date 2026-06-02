"use client";

import { useState } from "react";
import { useQuery, useMutation } from "@apollo/client/react";
import { GET_MESSAGES } from "@/graphql/queries/messages";
import { MARK_MESSAGE_READ } from "@/graphql/mutations/messages";
import Header from "@/components/layout/Header";
import { MessageSquare, AlertCircle, CheckCircle, Filter, ArrowDown, ArrowUp } from "lucide-react";
import { can } from "@/lib/acl";
import AclStore from "@/lib/acl";
import { isOn } from "@/lib/featureFlags";

function getSentimentClass(s: string) {
  const map: Record<string, string> = { POSITIVE: "badge-success", NEUTRAL: "badge-neutral", NEGATIVE: "badge-danger" };
  return `badge ${map[s] || "badge-neutral"}`;
}

function getStatusClass(s: string) {
  const map: Record<string, string> = { UNREAD: "badge-warning", READ: "badge-neutral", REPLIED: "badge-success", ARCHIVED: "badge-info" };
  return `badge ${map[s] || "badge-neutral"}`;
}

function getChannelIcon(type: string) {
  const icons: Record<string, string> = {
    TWITTER: "𝕏", INSTAGRAM: "📷", FACEBOOK: "f", LINKEDIN: "in",
    YOUTUBE: "▶", EMAIL: "✉", SMS: "💬", WHATSAPP: "✅",
  };
  return icons[type] || "?";
}

function timeAgo(date: string) {
  const d = new Date(date);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  const hours = Math.floor(diff / (1000 * 60 * 60));
  if (hours < 1) return "Just now";
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

export default function MessagesPage() {
  const [statusFilter, setStatusFilter] = useState("");
  const [sentimentFilter, setSentimentFilter] = useState("");
  const [inboundFilter, setInboundFilter] = useState("");

  // Permission & feature-flag checks
  const canViewMessages = can('p:user_view');
  const canReplyMessages = AclStore.can('p:user_edit');
  const showSmartRecommendations = isOn('SMART_RECOMMENDATIONS');
  const showUserProfileV2 = isOn('USER_PROFILE_V2');

  const { data, loading, error, refetch } = useQuery(GET_MESSAGES, {
    variables: {
      first: 30,
      filter: {
        status: statusFilter || null,
        sentiment: sentimentFilter || null,
        isInbound: inboundFilter === "" ? null : inboundFilter === "true",
      },
    },
  });

  const [markRead] = useMutation(MARK_MESSAGE_READ, { onCompleted: () => refetch() });

  const messages = data?.messages?.edges?.map((e: any) => e.node) || [];
  const totalCount = data?.messages?.totalCount || 0;

  if (!canViewMessages) {
    return (
      <>
        <Header title="Messages" subtitle="Access Denied" />
        <div className="empty-state">
          <div className="empty-state-icon"><AlertCircle size={28} /></div>
          <p className="empty-state-title">Access Denied</p>
          <p className="empty-state-description">You need the user_view permission to see messages.</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Header title="Messages" subtitle={`${totalCount} total messages`} />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        {/* Filters */}
        <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap", alignItems: "center" }}>
          <Filter size={15} style={{ color: "var(--color-text-muted)" }} />
          <select className="input select" style={{ width: 140 }} value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Status</option>
            {["UNREAD", "READ", "REPLIED", "ARCHIVED"].map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <select className="input select" style={{ width: 150 }} value={sentimentFilter} onChange={(e) => setSentimentFilter(e.target.value)}>
            <option value="">All Sentiments</option>
            {["POSITIVE", "NEUTRAL", "NEGATIVE"].map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <select className="input select" style={{ width: 140 }} value={inboundFilter} onChange={(e) => setInboundFilter(e.target.value)}>
            <option value="">All Messages</option>
            <option value="true">Inbound</option>
            <option value="false">Outbound</option>
          </select>
        </div>

        {/* Message List */}
        {loading ? (
          <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
            {[...Array(8)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 90, borderRadius: 12 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load messages</p>
          </div>
        ) : messages.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><MessageSquare size={28} /></div>
            <p className="empty-state-title">No messages found</p>
          </div>
        ) : (
          <div style={{ display: "flex", flexDirection: "column", gap: "0.625rem" }}>
            {messages.map((msg: any) => (
              <div key={msg.id} style={{
                display: "flex",
                gap: "1rem",
                padding: "1rem 1.25rem",
                background: msg.status === "UNREAD" ? "var(--color-bg-card)" : "var(--color-bg-secondary)",
                border: `1px solid ${msg.status === "UNREAD" ? "var(--color-border-hover)" : "var(--color-border)"}`,
                borderRadius: "var(--radius-md)",
                transition: "all var(--transition-fast)",
              }}>
                {/* Channel Icon */}
                <div style={{
                  width: 40, height: 40, borderRadius: 10, flexShrink: 0,
                  background: `${msg.channel.color}20`, border: `1px solid ${msg.channel.color}30`,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  color: msg.channel.color, fontWeight: 700, fontSize: "0.9rem"
                }}>
                  {getChannelIcon(msg.channel.type)}
                </div>

                {/* Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.375rem", flexWrap: "wrap" }}>
                    <span style={{ fontWeight: 600, fontSize: "0.875rem" }}>
                      {msg.customer ? `${msg.customer.firstName} ${msg.customer.lastName}` : "Anonymous"}
                    </span>
                    <span className={getStatusClass(msg.status)}>{msg.status}</span>
                    <span className={getSentimentClass(msg.sentiment)}>{msg.sentiment}</span>
                    <span style={{ display: "flex", alignItems: "center", gap: 3, fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
                      {msg.isInbound ? <ArrowDown size={10} /> : <ArrowUp size={10} />}
                      {msg.isInbound ? "Inbound" : "Outbound"}
                    </span>
                  </div>
                  {msg.subject && (
                    <div style={{ fontSize: "0.8125rem", fontWeight: 500, color: "var(--color-text-secondary)", marginBottom: "0.25rem" }}>{msg.subject}</div>
                  )}
                  <p style={{ fontSize: "0.875rem", color: "var(--color-text-secondary)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                    {msg.body}
                  </p>
                </div>

                {/* Actions */}
                <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: "0.5rem", flexShrink: 0 }}>
                  <span style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{timeAgo(msg.createdAt)}</span>
                  <span className="badge badge-neutral" style={{ background: `${msg.channel.color}15`, color: msg.channel.color }}>{msg.channel.name}</span>
                  {msg.status === "UNREAD" && canReplyMessages && (
                    <button
                      className="btn btn-ghost btn-sm"
                      onClick={() => markRead({ variables: { id: msg.id } })}
                      style={{ padding: "0.25rem 0.5rem", fontSize: "0.75rem" }}
                    >
                      <CheckCircle size={12} /> Mark Read
                    </button>
                  )}
                  {showSmartRecommendations && msg.isInbound && (
                    <span className="badge badge-info" style={{ fontSize: "0.6875rem" }}>AI Reply</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
