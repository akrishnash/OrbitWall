import React from 'react';
import { WallSettings, Resolution } from '../types';
import { Sliders, Sun, Eye, ZoomIn, Palette, Layers, Monitor } from 'lucide-react';

interface ControlsProps {
  settings: WallSettings;
  onChange: (s: WallSettings) => void;
  isProcessing: boolean;
}

const Controls: React.FC<ControlsProps> = ({ settings, onChange, isProcessing }) => {
  const handleChange = (key: keyof WallSettings, value: number | string) => {
    onChange({ ...settings, [key]: value });
  };

  return (
    <div className="bg-slate-900/90 backdrop-blur-xl p-6 rounded-t-3xl border-t border-slate-700 w-full max-w-md mx-auto shadow-2xl pb-8 pointer-events-auto">
      <div className="flex items-center gap-2 mb-6 text-cyan-400">
        <Sliders size={18} />
        <h3 className="font-bold text-xs uppercase tracking-widest">Editor</h3>
      </div>

      <div className="space-y-6">
        {/* Row 1: Brightness & Blur */}
        <div className="grid grid-cols-2 gap-6">
           <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-400">
                <span className="flex items-center gap-1"><Sun size={12}/> Brightness</span>
                <span>{Math.round(settings.brightness * 100)}%</span>
              </div>
              <input
                type="range"
                min="0.5"
                max="1.5"
                step="0.1"
                disabled={isProcessing}
                value={settings.brightness}
                onChange={(e) => handleChange('brightness', parseFloat(e.target.value))}
                className="w-full h-1 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-cyan-500"
              />
           </div>
           <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-400">
                <span className="flex items-center gap-1"><Eye size={12}/> Blur</span>
                <span>{settings.blur}px</span>
              </div>
              <input
                type="range"
                min="0"
                max="10"
                step="0.5"
                disabled={isProcessing}
                value={settings.blur}
                onChange={(e) => handleChange('blur', parseFloat(e.target.value))}
                className="w-full h-1 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-cyan-500"
              />
           </div>
        </div>

        {/* Row 2: Zoom & Overlay Opacity */}
        <div className="grid grid-cols-2 gap-6">
           <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-400">
                <span className="flex items-center gap-1"><ZoomIn size={12}/> Zoom</span>
                <span>{settings.zoomOffset > 0 ? '+' : ''}{Math.round(settings.zoomOffset)}</span>
              </div>
              <input
                type="range"
                min="-3"
                max="3"
                step="1" 
                disabled={isProcessing}
                value={settings.zoomOffset}
                onChange={(e) => handleChange('zoomOffset', parseFloat(e.target.value))}
                className="w-full h-1 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-cyan-500"
              />
           </div>
           
           <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-400">
                 <span className="flex items-center gap-1"><Layers size={12}/> Overlay</span>
                 <span>{Math.round(settings.overlayOpacity * 100)}%</span>
              </div>
              <input
                type="range"
                min="0"
                max="0.8"
                step="0.05"
                disabled={isProcessing}
                value={settings.overlayOpacity}
                onChange={(e) => handleChange('overlayOpacity', parseFloat(e.target.value))}
                className="w-full h-1 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-cyan-500"
              />
           </div>
        </div>

        {/* Row 3: Quality & Color */}
        <div className="grid grid-cols-2 gap-6 pt-2 border-t border-slate-800">
            {/* Resolution Selector */}
            <div className="space-y-2">
                <div className="flex justify-between text-xs text-slate-400">
                  <span className="flex items-center gap-1"><Monitor size={12}/> Quality</span>
                </div>
                <div className="flex bg-slate-800 rounded-lg p-1">
                  {(['screen', '2k', '4k'] as Resolution[]).map((res) => (
                    <button
                      key={res}
                      onClick={() => handleChange('resolution', res)}
                      className={`flex-1 text-[10px] font-bold py-1 rounded-md uppercase transition-all ${
                        settings.resolution === res 
                          ? 'bg-slate-600 text-white shadow-sm' 
                          : 'text-slate-500 hover:text-slate-300'
                      }`}
                    >
                      {res}
                    </button>
                  ))}
                </div>
            </div>

            {/* Color Picker */}
            <div className="space-y-2">
                 <div className="flex justify-between text-xs text-slate-400">
                   <span className="flex items-center gap-1"><Palette size={12}/> Overlay Color</span>
                   <span className="font-mono">{settings.overlayColor}</span>
                 </div>
                 <div className="flex items-center gap-2">
                   <input 
                     type="color" 
                     value={settings.overlayColor}
                     onChange={(e) => handleChange('overlayColor', e.target.value)}
                     className="w-full h-8 rounded-lg overflow-hidden border-none outline-none cursor-pointer"
                   />
                 </div>
            </div>
        </div>
      </div>
    </div>
  );
};

export default Controls;
