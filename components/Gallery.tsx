import React, { useState } from 'react';
import { Region } from '../types';
import { PREDEFINED_REGIONS, TILE_URL_TEMPLATE } from '../constants';
import { latLonToTile, getTileUrl } from '../services/tileMath';
import { MapPin, Search, X } from 'lucide-react';
import { Logo } from './Logo';

interface GalleryProps {
  onSelect: (region: Region) => void;
}

const Gallery: React.FC<GalleryProps> = ({ onSelect }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const getThumbnail = (region: Region) => {
    const tile = latLonToTile(region.location.lat, region.location.lon, region.zoom);
    return getTileUrl(TILE_URL_TEMPLATE, tile.x, tile.y, tile.z);
  };

  const filteredRegions = PREDEFINED_REGIONS.filter(region => 
    region.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
    region.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  return (
    <div className="w-full h-screen bg-slate-950 flex flex-col pt-8">
      
      {/* Header */}
      <div className="px-6 mb-6 flex items-center justify-center">
        <Logo />
      </div>

      {/* Search Header */}
      <div className="px-6 mb-6 w-full max-w-4xl mx-auto z-10">
        <div className="relative group">
          <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
            <Search size={18} className="text-slate-500 group-focus-within:text-cyan-400 transition-colors" />
          </div>
          <input
            type="text"
            placeholder="Search location or tag..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-slate-900 border border-slate-800 focus:border-cyan-500/50 rounded-2xl py-3 pl-10 pr-10 text-white placeholder-slate-500 outline-none transition-all shadow-lg"
          />
          {searchTerm && (
            <button 
              onClick={() => setSearchTerm('')}
              className="absolute inset-y-0 right-3 flex items-center text-slate-500 hover:text-white"
            >
              <X size={18} />
            </button>
          )}
        </div>
      </div>

      {/* Scrollable Grid */}
      <div className="flex-1 overflow-y-auto px-6 pb-20 no-scrollbar">
        <div className="max-w-4xl mx-auto">
           <h2 className="text-sm uppercase tracking-widest text-slate-500 mb-4 font-bold">
             {searchTerm ? `Results (${filteredRegions.length})` : 'Curated Locations'}
           </h2>
           
           {filteredRegions.length === 0 ? (
             <div className="text-center py-20 text-slate-600">
               <p>No orbits found matching "{searchTerm}"</p>
             </div>
           ) : (
             <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
               {filteredRegions.map(region => (
                 <button
                    key={region.id}
                    onClick={() => onSelect(region)}
                    className="group relative aspect-[3/4] rounded-2xl overflow-hidden bg-slate-900 border border-slate-800 hover:border-cyan-500 transition-all hover:scale-[1.02] shadow-lg active:scale-95"
                 >
                    {/* Thumbnail Image */}
                    <div className="absolute inset-0 bg-slate-800">
                       {/* eslint-disable-next-line */}
                       <img 
                          src={getThumbnail(region)}
                          alt={region.name}
                          className="w-full h-full object-cover opacity-60 group-hover:opacity-100 transition-opacity duration-500"
                       />
                       {/* Gradient overlay for text readability */}
                       <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent" />
                    </div>

                    {/* Content */}
                    <div className="absolute bottom-0 left-0 right-0 p-4 text-left">
                      <div className="flex items-center gap-1 text-cyan-400 mb-1 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0">
                        <MapPin size={12} />
                        <span className="text-[10px] font-bold uppercase tracking-wider">Explore</span>
                      </div>
                      <h3 className="font-bold text-white leading-tight mb-1">{region.name}</h3>
                      <div className="flex flex-wrap gap-1">
                         {region.tags.slice(0, 2).map(tag => (
                           <span key={tag} className="text-[10px] px-1.5 py-0.5 rounded bg-white/10 text-white/80 backdrop-blur-md">
                             #{tag}
                           </span>
                         ))}
                      </div>
                    </div>
                 </button>
               ))}
             </div>
           )}
           
           {!searchTerm && (
              <div className="mt-12 text-center text-slate-600 text-sm mb-10">
                <p>More locations added weekly.</p>
              </div>
           )}
        </div>
      </div>
    </div>
  );
};

export default Gallery;
