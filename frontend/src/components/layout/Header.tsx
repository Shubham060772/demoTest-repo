"use client";

import { Bell, Search, Settings } from "lucide-react";
import styles from "./Header.module.css";

interface HeaderProps {
  title: string;
  subtitle?: string;
}

export default function Header({ title, subtitle }: HeaderProps) {
  return (
    <header className={styles.header}>
      <div className={styles.left}>
        <h1 className={styles.title}>{title}</h1>
        {subtitle && <p className={styles.subtitle}>{subtitle}</p>}
      </div>
      <div className={styles.right}>
        <div className={styles.searchBar}>
          <Search size={15} className={styles.searchIcon} />
          <input
            type="text"
            placeholder="Search anything..."
            className={styles.searchInput}
          />
        </div>
        <button className={styles.iconBtn} aria-label="Notifications">
          <Bell size={18} />
          <span className={styles.notifBadge}>3</span>
        </button>
        <button className={styles.iconBtn} aria-label="Settings">
          <Settings size={18} />
        </button>
        <div className={styles.avatar}>AC</div>
      </div>
    </header>
  );
}
