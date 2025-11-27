'use client';

import { useEffect, useState } from 'react';
import { Users, BookOpen, AlertCircle, Activity } from 'lucide-react';

interface DashboardStats {
  totalUsers: number;
  totalWords: number;
  pendingContributions: number;
  systemUptime?: number;
}

export default function AdminDashboard() {
  const [stats, setStats] = useState<DashboardStats>({
    totalUsers: 0,
    totalWords: 0,
    pendingContributions: 0,
    systemUptime: 99.9,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch dashboard stats from API
    const fetchStats = async () => {
      try {
        const response = await fetch('/api/admin/stats', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setStats({
            totalUsers: data.data?.totalUsers || 0,
            totalWords: data.data?.totalWords || 0,
            pendingContributions: data.data?.pendingContributions || 0,
            systemUptime: 99.9,
          });
        }
      } catch (error) {
        console.error('Failed to fetch stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const statCards = [
    {
      icon: Users,
      label: 'TOTAL USERS',
      value: stats.totalUsers.toLocaleString(),
      unit: 'active accounts',
      alert: false,
    },
    {
      icon: BookOpen,
      label: 'TOTAL WORDS',
      value: stats.totalWords.toLocaleString(),
      unit: 'in database',
      alert: false,
    },
    {
      icon: AlertCircle,
      label: 'PENDING CONTRIBUTIONS',
      value: stats.pendingContributions.toString(),
      unit: 'awaiting review',
      alert: true,
    },
    {
      icon: Activity,
      label: 'SYSTEM UPTIME',
      value: `${stats.systemUptime}%`,
      unit: 'operational status',
      alert: false,
    },
  ];

  return (
    <div>
      <h1 className="text-3xl font-bold mb-8 tracking-wider text-glow">
        &gt; DASHBOARD_OVERVIEW
      </h1>

      {loading ? (
        <div className="text-[var(--primary-color)] text-glow">Loading stats...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
          {statCards.map((card, index) => {
            const Icon = card.icon;
            return (
              <div
                key={index}
                className={`
                  bg-[#0a0a0a] border-2 p-6 relative overflow-hidden transition-all duration-300 border-glow
                  ${card.alert
                    ? 'border-[var(--alert-color)] border-glow'
                    : 'border-[var(--primary-color)]'
                  }
                  hover:border-glow hover:-translate-y-1
                `}
              >
                {/* Animated scan line */}
                <div
                  className="absolute top-0 left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-[var(--primary-color)] to-transparent animate-pulse"
                  style={{
                    animation: 'scan 2s infinite',
                  }}
                />

                <div className="mb-4">
                  <Icon
                    className={`
                      w-8 h-8 mb-4 text-glow
                      ${card.alert ? 'text-[var(--alert-color)]' : 'text-[var(--primary-color)]'}
                    `}
                  />
                </div>

                <div className="text-xs tracking-wide uppercase mb-2.5 text-[#888]">
                  {card.label}
                </div>

                <div
                  className={`
                    text-4xl font-bold mb-1 font-mono tracking-wider text-glow
                    ${card.alert ? 'text-[var(--alert-color)]' : 'text-[var(--primary-color)]'}
                  `}
                >
                  {card.value}
                </div>

                <div className="text-sm text-[#666]">{card.unit}</div>
              </div>
            );
          })}
        </div>
      )}

      <style jsx>{`
        @keyframes scan {
          0%, 100% { opacity: 0; }
          50% { opacity: 1; }
        }
      `}</style>
    </div>
  );
}

