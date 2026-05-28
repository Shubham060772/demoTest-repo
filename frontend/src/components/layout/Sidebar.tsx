"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Users,
  Megaphone,
  Radio,
  MessageSquare,
  BarChart2,
  UsersRound,
  Zap,
} from "lucide-react";
import styles from "./Sidebar.module.css";

const nav = [
  { href: "/", icon: LayoutDashboard, label: "Dashboard" },
  { href: "/customers", icon: Users, label: "Customers" },
  { href: "/campaigns", icon: Megaphone, label: "Campaigns" },
  { href: "/channels", icon: Radio, label: "Channels" },
  { href: "/messages", icon: MessageSquare, label: "Messages" },
  { href: "/analytics", icon: BarChart2, label: "Analytics" },
  { href: "/teams", icon: UsersRound, label: "Teams" },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className={styles.sidebar}>
      <div className={styles.logo}>
        <div className={styles.logoIcon}>
          <Zap size={18} />
        </div>
        <span className={styles.logoText}>Nexus CXM</span>
      </div>

      <nav className={styles.nav}>
        <span className={styles.navSection}>MAIN MENU</span>
        {nav.map(({ href, icon: Icon, label }) => {
          const active = pathname === href || (href !== "/" && pathname.startsWith(href));
          return (
            <Link key={href} href={href} className={`${styles.navItem} ${active ? styles.active : ""}`}>
              <Icon size={18} />
              <span>{label}</span>
              {active && <div className={styles.activeIndicator} />}
            </Link>
          );
        })}
      </nav>

      <div className={styles.sidebarFooter}>
        <div className={styles.footerCard}>
          <p className={styles.footerTitle}>GraphQL API</p>
          <p className={styles.footerSub}>Live on port 8080</p>
        </div>
      </div>
    </aside>
  );
}
