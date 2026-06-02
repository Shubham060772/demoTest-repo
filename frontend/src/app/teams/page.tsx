"use client";

import { useQuery } from "@apollo/client/react";
import { GET_TEAMS } from "@/graphql/queries/teams";
import Header from "@/components/layout/Header";
import { Users, AlertCircle, Shield, BarChart2, Headphones, Eye, UserCheck } from "lucide-react";
import { can } from "@/lib/acl";
import { isOn } from "@/lib/featureFlags";

function getRoleIcon(role: string) {
  const map: Record<string, any> = {
    ADMIN: Shield, MANAGER: UserCheck, ANALYST: BarChart2,
    AGENT: Headphones, VIEWER: Eye,
  };
  return map[role] || Users;
}

function getRoleBadgeClass(role: string) {
  const map: Record<string, string> = {
    ADMIN: "badge-danger", MANAGER: "badge-primary", ANALYST: "badge-info",
    AGENT: "badge-success", VIEWER: "badge-neutral",
  };
  return `badge ${map[role] || "badge-neutral"}`;
}

function getInitials(first: string, last: string) {
  return `${first[0]}${last[0]}`.toUpperCase();
}

const AVATAR_COLORS = ["#6366f1", "#8b5cf6", "#06b6d4", "#10b981", "#f59e0b", "#ef4444"];

export default function TeamsPage() {
  // Permission & feature-flag checks
  const canManageUsers = can('p:user_manage');
  const showUserProfileV2 = isOn('USER_PROFILE_V2');

  const { data, loading, error } = useQuery(GET_TEAMS, {
    skip: !canManageUsers,
  });
  const teams = data?.teams || [];

  if (!canManageUsers) {
    return (
      <>
        <Header title="Teams" subtitle="Access Denied" />
        <div className="empty-state">
          <div className="empty-state-icon"><AlertCircle size={28} /></div>
          <p className="empty-state-title">Access Denied</p>
          <p className="empty-state-description">You need the user_manage permission to view teams.</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Header title="Teams" subtitle={`${teams.length} teams`} />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.5rem" }}>
        {loading ? (
          <div className="grid-2">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 300, borderRadius: 16 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load teams</p>
          </div>
        ) : (
          <div className="grid-2">
            {teams.map((team: any, teamIdx: number) => (
              <div key={team.id} className="card">
                <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", marginBottom: "1.25rem" }}>
                  <div>
                    <h3 style={{ marginBottom: "0.25rem" }}>{team.name}</h3>
                    <p style={{ fontSize: "0.8125rem" }}>{team.description}</p>
                  </div>
                  <span className="badge badge-primary">{team.members?.length || 0} members</span>
                </div>

                <div style={{ display: "flex", flexDirection: "column", gap: "0.625rem" }}>
                  {team.members?.map((member: any, idx: number) => {
                    const RoleIcon = getRoleIcon(member.role);
                    const color = AVATAR_COLORS[(teamIdx * 3 + idx) % AVATAR_COLORS.length];
                    return (
                      <div key={member.id} style={{
                        display: "flex", alignItems: "center", gap: "0.875rem",
                        padding: "0.75rem", borderRadius: "var(--radius-md)",
                        background: "var(--color-bg-elevated)",
                        border: "1px solid var(--color-border)",
                        transition: "all var(--transition-fast)",
                      }}
                        onMouseEnter={(e) => {
                          (e.currentTarget as HTMLElement).style.borderColor = "var(--color-border-hover)";
                          (e.currentTarget as HTMLElement).style.background = "var(--color-bg-card-hover)";
                        }}
                        onMouseLeave={(e) => {
                          (e.currentTarget as HTMLElement).style.borderColor = "var(--color-border)";
                          (e.currentTarget as HTMLElement).style.background = "var(--color-bg-elevated)";
                        }}
                      >
                        <div className="avatar" style={{ background: `${color}20`, color, border: `2px solid ${color}40` }}>
                          {getInitials(member.firstName, member.lastName)}
                        </div>
                        <div style={{ flex: 1 }}>
                          <div style={{ fontWeight: 500, fontSize: "0.875rem" }}>{member.firstName} {member.lastName}</div>
                          <div style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{member.email}</div>
                        </div>
                        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                          <span className={getRoleBadgeClass(member.role)}>
                            <RoleIcon size={10} /> {member.role}
                          </span>
                          {!member.isActive && <span className="badge badge-danger">Inactive</span>}
                        </div>
                      </div>
                    );
                  })}
                </div>

                <div style={{ marginTop: "1rem", paddingTop: "0.75rem", borderTop: "1px solid var(--color-border)", fontSize: "0.75rem", color: "var(--color-text-muted)" }}>
                  Created {new Date(team.createdAt).toLocaleDateString()}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
