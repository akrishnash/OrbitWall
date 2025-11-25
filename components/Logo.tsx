import React from 'react';
import { Orbit } from 'lucide-react';

export const Logo: React.FC<{ className?: string }> = ({ className = "" }) => (
  <div className={`flex items-center gap-2 font-bold tracking-tighter ${className}`}>
    <div className="relative flex items-center justify-center w-8 h-8 rounded-lg bg-gradient-to-br from-cyan-500 to-blue-600 shadow-lg shadow-cyan-500/20">
      <Orbit className="text-white w-5 h-5 animate-spin-slow" style={{ animationDuration: '10s' }} />
      <div className="absolute w-2 h-2 bg-white rounded-full shadow-[0_0_10px_rgba(255,255,255,0.8)]" />
    </div>
    <span className="text-2xl bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">
      OrbitWall
    </span>
  </div>
);
