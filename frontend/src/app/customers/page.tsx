"use client";

import { useState } from "react";
import { useQuery, useMutation } from "@apollo/client/react";
import { GET_CUSTOMERS } from "@/graphql/queries/customers";
import { CREATE_CUSTOMER, DELETE_CUSTOMER } from "@/graphql/mutations/customers";
import Header from "@/components/layout/Header";
import { Search, Plus, Trash2, Eye, Mail, Phone, Building2, AlertCircle } from "lucide-react";
import Link from "next/link";
import { can } from "@/lib/acl";
import AclStore from "@/lib/acl";
import { isOn } from "@/lib/featureFlags";



function getInitials(first: string, last: string) {
  return `${first[0]}${last[0]}`.toUpperCase();
}

function getSegmentClass(seg: string) {
  const map: Record<string, string> = {
    Enterprise: "badge-primary", SMB: "badge-info", Startup: "badge-success",
    Consumer: "badge-neutral", Government: "badge-warning",
  };
  return `badge ${map[seg] || "badge-neutral"}`;
}

export default function CustomersPage() {
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ firstName: "", lastName: "", email: "", phone: "", company: "", segment: "Consumer" });

  // Permission & feature-flag checks
  const canViewCustomers = can('p:user_view');
  const canEditCustomers = AclStore.can('p:user_edit');
  const canDeleteCustomers = can('p:user_delete');
  const showNewUserFlow = isOn('NEW_USER_FLOW');
  const showUserProfileV2 = isOn('USER_PROFILE_V2', 'D');

  const { data, loading, error, refetch } = useQuery(GET_CUSTOMERS, {
    variables: { first: 20, filter: search ? { search } : null },
  });

  const [createCustomer, { loading: creating }] = useMutation(CREATE_CUSTOMER, {
    onCompleted: () => { setShowModal(false); setForm({ firstName: "", lastName: "", email: "", phone: "", company: "", segment: "Consumer" }); refetch(); },
  });

  const [deleteCustomer] = useMutation(DELETE_CUSTOMER, {
    onCompleted: () => refetch(),
  });

  const customers = data?.customers?.edges?.map((e: any) => e.node) || [];
  const totalCount = data?.customers?.totalCount || 0;

  if (!canViewCustomers) {
    return (
      <>
        <Header title="Customers" subtitle="Access Denied" />
        <div className="empty-state">
          <div className="empty-state-icon"><AlertCircle size={28} /></div>
          <p className="empty-state-title">Access Denied</p>
          <p className="empty-state-description">You need the user_view permission to see customers.</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Header title="Customers" subtitle={`${totalCount} total customers`} />

      <div style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        {/* Toolbar */}
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "1rem" }}>
          <div className="search-bar" style={{ flex: 1, maxWidth: 400 }}>
            <Search size={15} className="search-icon" />
            <input
              type="text"
              className="input"
              placeholder="Search customers..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ paddingLeft: "2.25rem" }}
            />
          </div>
          {canEditCustomers && (
            <button className="btn btn-primary" onClick={() => setShowModal(true)}>
              <Plus size={16} /> Add Customer
            </button>
          )}
        </div>

        {/* Table */}
        {loading ? (
          <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
            {[...Array(6)].map((_, i) => (
              <div key={i} className="skeleton" style={{ height: 60, borderRadius: 10 }} />
            ))}
          </div>
        ) : error ? (
          <div className="empty-state">
            <div className="empty-state-icon"><AlertCircle size={28} /></div>
            <p className="empty-state-title">Failed to load customers</p>
            <p className="empty-state-description">{error.message}</p>
          </div>
        ) : customers.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon"><Search size={28} /></div>
            <p className="empty-state-title">No customers found</p>
            <p className="empty-state-description">Try adjusting your search or add a new customer.</p>
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>Customer</th>
                  <th>Contact</th>
                  <th>Company</th>
                  <th>Segment</th>
                  <th>Tags</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((c: any) => (
                  <tr key={c.id}>
                    <td>
                      <div style={{ display: "flex", alignItems: "center", gap: "0.75rem" }}>
                        <div className="avatar">{getInitials(c.firstName, c.lastName)}</div>
                        <div>
                          <div style={{ fontWeight: 500 }}>{c.firstName} {c.lastName}</div>
                          <div style={{ fontSize: "0.75rem", color: "var(--color-text-muted)" }}>{c.email}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div style={{ display: "flex", flexDirection: "column", gap: "0.25rem" }}>
                        {c.email && (
                          <div style={{ display: "flex", alignItems: "center", gap: 6, fontSize: "0.8125rem", color: "var(--color-text-secondary)" }}>
                            <Mail size={12} />{c.email}
                          </div>
                        )}
                        {c.phone && (
                          <div style={{ display: "flex", alignItems: "center", gap: 6, fontSize: "0.8125rem", color: "var(--color-text-secondary)" }}>
                            <Phone size={12} />{c.phone}
                          </div>
                        )}
                      </div>
                    </td>
                    <td>
                      {c.company ? (
                        <div style={{ display: "flex", alignItems: "center", gap: 6, fontSize: "0.875rem" }}>
                          <Building2 size={13} style={{ color: "var(--color-text-muted)" }} /> {c.company}
                        </div>
                      ) : <span style={{ color: "var(--color-text-muted)" }}>—</span>}
                    </td>
                    <td>{c.segment ? <span className={getSegmentClass(c.segment)}>{c.segment}</span> : "—"}</td>
                    <td>
                      <div style={{ display: "flex", flexWrap: "wrap", gap: 4 }}>
                        {c.tags?.map((tag: string) => (
                          <span key={tag} className="badge badge-primary">{tag}</span>
                        ))}
                      </div>
                    </td>
                    <td>
                      <div style={{ display: "flex", gap: 6 }}>
                        <Link href={`/customers/${c.id}`}>
                          <button className="btn btn-ghost btn-sm" style={{ padding: "0.375rem" }}>
                            <Eye size={14} />
                          </button>
                        </Link>
                        {canDeleteCustomers && (
                          <button
                            className="btn btn-danger btn-sm"
                            style={{ padding: "0.375rem" }}
                            onClick={() => {
                              if (confirm("Delete this customer?")) {
                                deleteCustomer({ variables: { id: c.id } });
                              }
                            }}
                          >
                            <Trash2 size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Create Customer Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Add New Customer</h3>
              <button className="btn btn-ghost btn-sm" onClick={() => setShowModal(false)} style={{ padding: "0.375rem" }}>✕</button>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">First Name *</label>
                <input className="input" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} placeholder="John" />
              </div>
              <div className="form-group">
                <label className="form-label">Last Name *</label>
                <input className="input" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} placeholder="Doe" />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Email *</label>
              <input className="input" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="john@company.com" />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Phone</label>
                <input className="input" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="+1-555-0000" />
              </div>
              <div className="form-group">
                <label className="form-label">Company</label>
                <input className="input" value={form.company} onChange={(e) => setForm({ ...form, company: e.target.value })} placeholder="Acme Corp" />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Segment</label>
              <select className="input select" value={form.segment} onChange={(e) => setForm({ ...form, segment: e.target.value })}>
                {["Consumer", "Enterprise", "SMB", "Startup", "Government"].map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
              <button
                className="btn btn-primary"
                disabled={creating || !form.firstName || !form.lastName || !form.email}
                onClick={() => createCustomer({ variables: { input: { ...form, tags: [] } } })}
              >
                {creating ? "Creating..." : "Create Customer"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
