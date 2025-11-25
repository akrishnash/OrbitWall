import React, { useEffect, useState } from 'react';
import { Dimensions } from '../types';
import { Lock } from 'lucide-react';

interface PreviewProps {
  imageSrc: string | null;
  isLoading: boolean;
  dimensions: Dimensions;
}

const Preview: React.FC<PreviewProps> = ({ imageSrc, isLoading, dimensions }) => {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' });
  };

  return (
    <div className="relative w-full h-full bg-slate-950 flex items-center justify-center overflow-hidden shadow-2xl">
      {/* Background Image - Centered and Allowed to Overflow for Pan/Overscan */}
      {imageSrc ? (
         // eslint-disable-next-line
        <img 
          src={imageSrc} 
          alt="Preview" 
          className="max-w-none object-cover transition-opacity duration-500 pointer-events-none"
          style={{
            // If the image is loaded, it might be larger than dimensions (overscan). 
            // We want it centered. Flex parent handles centering.
            // max-w-none is critical so browser doesn't shrink it.
          }}
          draggable={false}
        />
      ) : (
        <div className="absolute inset-0 flex items-center justify-center text-slate-600 bg-slate-950">
            <span className="animate-pulse tracking-widest text-xs uppercase">Acquiring Data...</span>
        </div>
      )}

      {/* Loading Overlay */}
      {isLoading && (
        <div className="absolute inset-0 bg-black/50 backdrop-blur-sm z-10 flex flex-col items-center justify-center pointer-events-none">
          <div className="w-12 h-12 border-4 border-cyan-500 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p className="text-cyan-400 font-mono text-xs animate-pulse tracking-widest">DOWNLOADING TILES</p>
        </div>
      )}

      {/* Lock Screen UI Simulation */}
      <div className="absolute inset-0 z-20 flex flex-col items-center pt-24 pointer-events-none text-white/90 select-none">
        <div className="flex flex-col items-center drop-shadow-lg">
            <Lock size={20} className="mb-4 opacity-70" />
            <div className="text-7xl font-light tracking-tighter mb-2 font-mono">
                {formatTime(time)}
            </div>
            <div className="text-lg font-medium opacity-90">
                {formatDate(time)}
            </div>
        </div>
        
        <div className="mt-auto mb-10 flex gap-12 opacity-40">
            <div className="w-12 h-12 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center">
                 <div className="w-6 h-6 border-2 border-white rounded-sm"></div>
            </div>
            <div className="w-12 h-12 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center">
                 <div className="w-6 h-6 border-2 border-white rounded-sm"></div>
            </div>
        </div>
      </div>
    </div>
  );
};

export default Preview;