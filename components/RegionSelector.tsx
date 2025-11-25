import React, { useState } from 'react';
import { Region } from '../types';
import { PREDEFINED_REGIONS } from '../constants';
import { MapPin, Search, X } from 'lucide-react';

interface RegionSelectorProps {
  onSelect: (region: Region) => void;
  currentRegionId: string;
  isOpen: boolean;
  onClose: () => void;
}

const RegionSelector: React.FC<RegionSelectorProps> = ({ onSelect, currentRegionId, isOpen, onClose }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredRegions = PREDEFINED_REGIONS.filter(r => 
    r.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
    r.tags.some(t => t.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  if (!isOpen) return null;

  return (
    <div className="absolute inset-0 z-50 bg-slate-950/95 backdrop-blur-sm flex flex-col">
      <div className="p-4 flex items-center gap-2 border-b border-slate-800">
        <Search className="text-slate-400" size={20} />
        <input 
          type="text" 
          placeholder="Search locations..." 
          className="bg-transparent border-none outline-none text-white placeholder-slate-500 flex-1 text-lg"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          autoFocus
        />
        <button onClick={onClose} className="p-2 hover:bg-slate-800 rounded-full">
          <X size={24} />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {filteredRegions.map(region => (
          <button
            key={region.id}
            onClick={() => {
              onSelect(region);
              onClose();
            }}
            className={`w-full text-left p-4 rounded-xl border transition-all flex items-start gap-3
              ${region.id === currentRegionId 
                ? 'bg-cyan-950/50 border-cyan-500/50' 
                : 'bg-slate-900 border-slate-800 hover:border-slate-600'
              }`}
          >
            <div className={`mt-1 p-2 rounded-full ${region.id === currentRegionId ? 'bg-cyan-500 text-white' : 'bg-slate-800 text-slate-400'}`}>
              <MapPin size={16} />
            </div>
            <div>
              <h4 className="font-bold text-lg">{region.name}</h4>
              <p className="text-slate-400 text-sm mb-2">Lat: {region.location.lat.toFixed(2)}, Lon: {region.location.lon.toFixed(2)}</p>
              <div className="flex flex-wrap gap-1">
                {region.tags.map(tag => (
                  <span key={tag} className="text-xs px-2 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                    #{tag}
                  </span>
                ))}
              </div>
            </div>
          </button>
        ))}
        
        {filteredRegions.length === 0 && (
          <div className="text-center text-slate-500 mt-10">
            No regions found matching "{searchTerm}"
          </div>
        )}
      </div>
    </div>
  );
};

export default RegionSelector;