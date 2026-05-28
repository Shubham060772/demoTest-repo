"use client";

import { useQuery } from "@apollo/client/react";
import { GET_CUSTOMER } from "@/graphql/queries/customers";
import Header from "@/components/layout/Header";
import { Mail, Phone, Building2, ArrowLeft, Tag, AlertCircle } from "lucide-react";
import Link from "next/link";
import { use } from "react";

function getInitials(first: string, last: string) {
  return `${first[0]}${last[0]}`.toUpperCase();
}

function getSentimentClass(s: string) {
  const map: Record<string, string> = { POSITIVE: "badge-success", NEUTRAL: "badge-neutral", NEGATIVE: "badge-danger" };
  return `badge ${map[s] || "badge-neutral"}`;
}

function getStatusClass(s: string) {
  const map: Record<string, string> = { UNREAD: "badge-warning", READ: "badge-neutral", REPLIED: "badge-success", ARCHIVED: "badge-info" };
  return `badge ${map[s] || "badge-neutral"}`;
}

export default function CustomerDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const { data, loading, error } = useQuery(GET_CUSTOMER, { variables: { id } });

  if (loading) return (
    <>
      <Header title="Customer Detail" />
      <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
        <div className="skeleton" style={{ height: 200, borderRadius: 16 }} />
        <div className="skeleton" style={{ height: 300, borderRadius: 16 }} />
      </div>
    </>
  );

  if (error || !data?.customer) return (
    <>
      <Header title="Customer Not Found" />
      <div className="empty-state">
        <div className="empty-state-icon"><AlertCircle size={28} /></div>
        <p className="empty-state-title">Customer not found</p>
        <Link href="/customers" className="btn btn-secondary" style={{ marginTop: "1rem" }}>Back to Customers</Link>
      </div>
    </>
  );

  const c = data.customer;

  return (
    <>
      <Header title={`${c.firstName} ${c.lastName}`} subtitle="Customer Profile" />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        <Link href="/customers">
          <button className="btn btn-ghost btn-sm">
            <ArrowLeft size={14} /> Back to Customers
          </button>
        </Link>

        {/* Profile Card */}
        <div className="card">
          <div style={{ display: "flex", alignItems: "flex-start", gap: "1.5rem" }}>
            <div className="avatar avatar-lg" style={{ fontSize: "1.25rem" }}>
              {getInitials(c.firstName, c.lastName)}
            </div>
            <div style={{ flex: 1 }}>
              <h2 style={{ marginBottom: "0.375rem" }}>{c.firstName} {c.lastName}</h2>
              <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem" }}>
                <div style={{ display: "flex", alignItems: "center", gap: 6, color: "var(--color-text-secondary)", fontSize: "0.875rem" }}>
                  <Mail size={14} /> {c.email}
                </div>
                {c.phone && (
                  <div style={{ display: "flex", alignItems: "center", gap: 6, color: "var(--color-text-secondary)", fontSize: "0.875rem" }}>
                    <Phone size={14} /> {c.phone}
                  </div>
                )}
                {c.company && (
                  <div style={{ display: "flex", alignItems: "center", gap: 6, color: "var(--color-text-secondary)", fontSize: "0.875rem" }}>
                    <Building2 size={14} /> {c.company}
                  </div>
                )}
              </div>
              <div style={{ display: "flex", gap: "0.5rem", marginTop: "0.75rem", flexWrap: "wrap" }}>
                {c.segment && <span className="badge badge-primary">{c.segment}</span>}
                {c.tags?.map((tag: string) => (
                  <span key={tag} className="badge badge-neutral"><Tag size={10} /> {tag}</span>
                ))}
              </div>
            </div>
            <div style={{ fontSize: "0.75rem", color: "var(--color-text-muted)", textAlign: "right" }}>
              <div>Joined {new Date(c.createdAt).toLocaleDateString()}</div>
              <div>Updated {new Date(c.updatedAt).toLocaleDateString()}</div>
            </div>
          </div>
        </div>

        {/* Message History */}
        <div className="card">
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "1.25rem" }}>
            <h3>Message History <span style={{ color: "var(--color-text-muted)", fontWeight: 400, fontSize: "0.875rem" }}>({c.messages?.totalCount || 0})</span></h3>
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            {c.messages?.edges?.length === 0 ? (
              <p style={{ color: "var(--color-text-muted)", fontSize: "0.875rem" }}>No messages yet</p>
            ) : (
              c.messages?.edges?.map((edge: any) => {
                const msg = edge.node;
                return (
                  <div key={msg.id} style={{
                    padding: "0.875rem",
                    background: "var(--color-bg-elevated)",
                    borderRadius: "var(--radius-md)",
                    border: "1px solid var(--color-border)",
                  }}>
                    <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "0.5rem" }}>
                      <div style={{ display: "flex", gap: "0.5rem" }}>
                        <span className={getStatusClass(msg.status)}>{msg.status}</span>
                        <span className={getSentimentClass(msg.sentiment)}>{msg.sentiment}</span>
                        <span className="badge badge-neutral" style={{ background: `${msg.channel.color}20`, color: msg.channel.color }}>
                          {msg.channel.name}
                        </span>
                      </div>
                      <span style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
                        {new Date(msg.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <p style={{ fontSize: "0.875rem", color: "var(--color-text-primary)" }}>{msg.body}</p>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </>
  );
}
