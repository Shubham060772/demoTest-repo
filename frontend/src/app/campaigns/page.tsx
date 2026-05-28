"use client";

import { useState } from "react";
import { useQuery, useMutation } from "@apollo/client/react";
import { GET_CAMPAIGNS } from "@/graphql/queries/campaigns";
import { CREATE_CAMPAIGN, UPDATE_CAMPAIGN } from "@/graphql/mutations/campaigns";
import Header from "@/components/layout/Header";
import { Search, Plus, Eye, AlertCircle, Target, TrendingUp } from "lucide-react";
import Link from "next/link";

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

export default function CampaignsPage() {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: "", description: "", budget: "" });

  const { data, loading, error, refetch } = useQuery(GET_CAMPAIGNS, {
    variables: {
      first: 20,
      filter: { search: search || null, status: statusFilter || null },
    },
  });

  const [createCampaign, { loading: creating }] = useMutation(CREATE_CAMPAIGN, {
    onCompleted: () => { setShowModal(false); setForm({ name: "", description: "", budget: "" }); refetch(); },
  });

  const campaigns = data?.campaigns?.edges?.map((e: any) => e.node) || [];
  const totalCount = data?.campaigns?.totalCount || 0;

  const statuses = ["DRAFT", "SCHEDULED", "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED"];

  return (
    <>
      <Header title="Campaigns" subtitle={`${totalCount} total campaigns`} />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        {/* Toolbar */}
        <div style={{ display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap" }}>
          <div className="search-bar" style={{ flex: 1, maxWidth: 360 }}>
            <Search size={15} className="search-icon" />
            <input
              className="input"
              placeholder="Search campaigns..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ paddingLeft: "2.25rem" }}
            />
          </div>
          <select className="input select" style={{ width: 160 }} value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All Statuses</option>
            {statuses.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            <Plus size={16} /> New Campaign
          </button>
        </div>

        {/* Campaign Cards */}
        {loading ? (
          <div className="grid-3">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 200, borderRadius: 16 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load campaigns</p>
            <p className="empty-state-description">{error.message}</p>
          </div>
        ) : campaigns.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><Target size={28} /></div>
            <p className="empty-state-title">No campaigns found</p>
            <p className="empty-state-description">Create your first campaign to get started.</p>
          </div>
        ) : (
          <div className="grid-3">
            {campaigns.map((c: any) => (
              <Link key={c.id} href={`/campaigns/${c.id}`}>
                <div className="card" style={{ cursor: "pointer", height: "100%" }}>
                  <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", marginBottom: "0.75rem" }}>
                    <div
                      style={{
                        width: 40, height: 40, borderRadius: 10,
                        background: "rgba(99, 102, 241, 0.12)",
                        border: "1px solid rgba(99, 102, 241, 0.2)",
                        display: "flex", alignItems: "center", justifyContent: "center",
                        color: "var(--color-brand-primary)"
                      }}
                    >
                      <Target size={18} />
                    </div>
                    <span className={getStatusBadgeClass(c.status)}>{c.status}</span>
                  </div>

                  <h4 style={{ marginBottom: "0.375rem", lineHeight: 1.4 }}>{c.name}</h4>
                  <p style={{ fontSize: "0.8125rem", color: "var(--color-text-muted)", marginBottom: "1rem", lineClamp: 2, overflow: "hidden", display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical" }}>
                    {c.description || "No description"}
                  </p>

                  <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1rem", flexWrap: "wrap" }}>
                    {c.channels?.slice(0, 3).map((ch: any) => (
                      <span key={ch.id} className="badge" style={{ background: `${ch.color}20`, color: ch.color, borderColor: `${ch.color}40` }}>
                        {ch.name}
                      </span>
                    ))}
                    {c.channels?.length > 3 && <span className="badge badge-neutral">+{c.channels.length - 3}</span>}
                  </div>

                  <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.75rem", paddingTop: "0.75rem", borderTop: "1px solid var(--color-border)" }}>
                    <div>
                      <div style={{ fontSize: "0.6875rem", color: "var(--color-text-muted)", marginBottom: 2 }}>IMPRESSIONS</div>
                      <div style={{ fontWeight: 600, fontSize: "0.9375rem" }}>{formatNumber(c.impressions)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: "0.6875rem", color: "var(--color-text-muted)", marginBottom: 2 }}>ENGAGEMENT</div>
                      <div style={{ fontWeight: 600, fontSize: "0.9375rem", color: "var(--color-success)" }}>{c.engagementRate.toFixed(1)}%</div>
                    </div>
                    {c.budget && (
                      <div style={{ gridColumn: "span 2" }}>
                        <div style={{ fontSize: "0.6875rem", color: "var(--color-text-muted)", marginBottom: 4 }}>BUDGET USAGE</div>
                        <div className="progress-bar">
                          <div className="progress-bar-fill" style={{ width: `${Math.min((c.spentBudget / c.budget) * 100, 100)}%` }} />
                        </div>
                        <div style={{ display: "flex", justifyContent: "space-between", marginTop: 4 }}>
                          <span style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{formatCurrency(c.spentBudget)} spent</span>
                          <span style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{formatCurrency(c.budget)} total</span>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      {/* Create Campaign Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">New Campaign</h3>
              <button className="btn btn-ghost btn-sm" onClick={() => setShowModal(false)} style={{ padding: "0.375rem" }}>✕</button>
            </div>
            <div className="form-group">
              <label className="form-label">Campaign Name *</label>
              <input className="input" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Summer Launch 2024" />
            </div>
            <div className="form-group">
              <label className="form-label">Description</label>
              <textarea className="input" rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Campaign description..." style={{ resize: "vertical" }} />
            </div>
            <div className="form-group">
              <label className="form-label">Budget (USD)</label>
              <input className="input" type="number" value={form.budget} onChange={(e) => setForm({ ...form, budget: e.target.value })} placeholder="50000" />
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
              <button
                className="btn btn-primary"
                disabled={creating || !form.name}
                onClick={() => createCampaign({
                  variables: {
                    input: {
                      name: form.name,
                      description: form.description || null,
                      budget: form.budget ? parseFloat(form.budget) : null,
                    }
                  }
                })}
              >
                {creating ? "Creating..." : "Create Campaign"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
