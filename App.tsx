import React, { useState, useEffect, useRef } from 'react';
import { Download, RefreshCw, ChevronLeft } from 'lucide-react';
import { Region, WallSettings, Dimensions, GeoLocation } from './types';
import { PREDEFINED_REGIONS, DEFAULT_SETTINGS, PREVIEW_OVERSCAN } from './constants';
import { generateWallpaper } from './services/imageStitcher';
import { latLonToTilePoint, tileToLatLon } from './services/tileMath';
import Controls from './components/Controls';
import Preview from './components/Preview';
import Gallery from './components/Gallery';
import { Logo } from './components/Logo';

// Utility hook for debouncing values
function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  useEffect(() => {
    const handler = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(handler);
  }, [value, delay]);
  return debouncedValue;
}

type ViewMode = 'gallery' | 'editor';

export default function App() {
  // State
  const [view, setView] = useState<ViewMode>('gallery');
  const [activeRegion, setActiveRegion] = useState<Region>(PREDEFINED_REGIONS[0]);
  
  // Independent location state allows panning away from the initial region center
  const [currentLocation, setCurrentLocation] = useState<GeoLocation>(PREDEFINED_REGIONS[0].location);

  const [settings, setSettings] = useState<WallSettings>(DEFAULT_SETTINGS);
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [isDownloading, setIsDownloading] = useState(false);
  const [screenDim, setScreenDim] = useState<Dimensions>({ width: 1080, height: 1920 });
  const [error, setError] = useState<string | null>(null);
  const [showUI, setShowUI] = useState(false); // Default to hidden for immersive view

  // Gesture State
  const [pinchScale, setPinchScale] = useState(1);
  const [panOffset, setPanOffset] = useState({ x: 0, y: 0 });
  
  // Refs for gesture tracking
  const pinchStartDist = useRef<number | null>(null);
  const pinchStartZoom = useRef<number>(0);
  const panStart = useRef<{x: number, y: number} | null>(null);
  const lastPanPos = useRef<{x: number, y: number} | null>(null);
  const totalPanDistance = useRef<number>(0); // To distinguish tap from drag

  const debouncedSettings = useDebounce(settings, 500);

  // Detect Screen Size & Pixel Ratio
  useEffect(() => {
    const updateDimensions = () => {
      const dpr = window.devicePixelRatio || 1;
      setScreenDim({
        width: Math.floor(window.innerWidth * dpr),
        height: Math.floor(window.innerHeight * dpr)
      });
    };
    updateDimensions();
    window.addEventListener('resize', updateDimensions);
    return () => window.removeEventListener('resize', updateDimensions);
  }, []);

  // Wallpaper Generation Effect (Preview Only)
  useEffect(() => {
    if (view !== 'editor') return;

    let isMounted = true;
    const fetchWallpaper = async () => {
      setIsProcessing(true);
      setError(null);
      try {
        const effectiveZoom = activeRegion.zoom + debouncedSettings.zoomOffset;
        
        // Fetch with Overscan to allow panning without black edges immediately
        const fetchDim = {
          width: screenDim.width + PREVIEW_OVERSCAN,
          height: screenDim.height + PREVIEW_OVERSCAN
        };

        const result = await generateWallpaper(
          currentLocation,
          fetchDim,
          debouncedSettings,
          effectiveZoom
        );
        if (isMounted) {
          setPreviewImage(result.dataUrl);
        }
      } catch (err) {
        if (isMounted) {
          console.error(err);
          setError("Failed to generate imagery.");
        }
      } finally {
        if (isMounted) setIsProcessing(false);
      }
    };
    
    fetchWallpaper();
    return () => { isMounted = false; };
  }, [
    currentLocation, 
    activeRegion.zoom, 
    debouncedSettings.zoomOffset,
    debouncedSettings.brightness,
    debouncedSettings.blur,
    debouncedSettings.overlayOpacity,
    debouncedSettings.overlayColor,
    screenDim, 
    view
  ]);

  // --- Gesture Handlers ---

  const handleTouchStart = (e: React.TouchEvent) => {
    // 2 Fingers: Pinch Zoom
    if (e.touches.length === 2) {
      const dist = Math.hypot(
        e.touches[0].clientX - e.touches[1].clientX,
        e.touches[0].clientY - e.touches[1].clientY
      );
      pinchStartDist.current = dist;
      pinchStartZoom.current = settings.zoomOffset;
      // Clear pan if switching to pinch
      panStart.current = null;
      lastPanPos.current = null;
      setPanOffset({ x: 0, y: 0 });
    } 
    // 1 Finger: Pan or Tap
    else if (e.touches.length === 1) {
      const x = e.touches[0].clientX;
      const y = e.touches[0].clientY;
      panStart.current = { x, y };
      lastPanPos.current = { x, y };
      totalPanDistance.current = 0;
    }
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    // Pinch Logic
    if (e.touches.length === 2 && pinchStartDist.current !== null) {
      const dist = Math.hypot(
        e.touches[0].clientX - e.touches[1].clientX,
        e.touches[0].clientY - e.touches[1].clientY
      );
      const scale = dist / pinchStartDist.current;
      setPinchScale(scale);
    }
    // Pan Logic
    else if (e.touches.length === 1 && panStart.current !== null) {
      const x = e.touches[0].clientX;
      const y = e.touches[0].clientY;
      
      const dx = x - panStart.current.x;
      const dy = y - panStart.current.y;
      setPanOffset({ x: dx, y: dy });
      
      if (lastPanPos.current) {
        const moveX = x - lastPanPos.current.x;
        const moveY = y - lastPanPos.current.y;
        totalPanDistance.current += Math.hypot(moveX, moveY);
      }
      lastPanPos.current = { x, y };
    }
  };

  const handleTouchEnd = () => {
    const effectiveZoom = activeRegion.zoom + settings.zoomOffset;

    // End Pinch
    if (pinchStartDist.current !== null) {
      const zoomDelta = Math.log2(pinchScale);
      let newZoom = pinchStartZoom.current + zoomDelta;
      // Snap to integer
      newZoom = Math.round(newZoom);
      newZoom = Math.max(-3, Math.min(3, newZoom));
      setSettings(prev => ({ ...prev, zoomOffset: newZoom }));
      
      pinchStartDist.current = null;
      setPinchScale(1);
    } 
    // End Pan
    else if (panStart.current !== null) {
      // Differentiate Tap vs Pan
      // If movement is very small, treat as tap to toggle UI
      if (Math.abs(panOffset.x) < 10 && Math.abs(panOffset.y) < 10 && totalPanDistance.current < 20) {
        setShowUI(prev => !prev);
      } else {
        // Confirm Pan -> Update Location -> New Fetch
        // 1. Get current tile coordinates (float) for the center
        const centerTile = latLonToTilePoint(currentLocation.lat, currentLocation.lon, effectiveZoom);
        
        // 2. Apply delta (in tiles). 256px = 1 tile.
        // Direction is inverted (dragging right moves view left)
        const newTileX = centerTile.x - (panOffset.x / 256);
        const newTileY = centerTile.y - (panOffset.y / 256);

        // 3. Convert back to Lat/Lon
        const newLoc = tileToLatLon(newTileX, newTileY, effectiveZoom);
        
        setCurrentLocation(newLoc);
        // Clear preview image to prevent "snap-back" glitch.
        // We want the user to see we are loading new data for the new center.
        setPreviewImage(null);
      }

      panStart.current = null;
      lastPanPos.current = null;
      setPanOffset({ x: 0, y: 0 });
    }
  };

  const handleDownload = async () => {
    if (isDownloading || isProcessing) return;
    setIsDownloading(true);

    try {
      // Determine output resolution
      let outputDim = { ...screenDim };
      const aspectRatio = screenDim.width / screenDim.height;
      let widthRatio = 1;

      if (settings.resolution === '2k') {
        const targetWidth = 1440;
        if (screenDim.width < screenDim.height) {
             outputDim = { width: targetWidth, height: Math.round(targetWidth / aspectRatio) };
        } else {
             outputDim = { width: 2560, height: 1440 };
        }
        widthRatio = outputDim.width / screenDim.width;
      } else if (settings.resolution === '4k') {
        const targetWidth = 2160;
        if (screenDim.width < screenDim.height) {
            outputDim = { width: targetWidth, height: Math.round(targetWidth / aspectRatio) };
        } else {
             outputDim = { width: 3840, height: 2160 };
        }
        widthRatio = outputDim.width / screenDim.width;
      }

      const fovCorrection = Math.round(Math.log2(widthRatio));
      const effectiveZoom = activeRegion.zoom + settings.zoomOffset + fovCorrection;
      
      const result = await generateWallpaper(
        currentLocation,
        outputDim,
        settings,
        effectiveZoom
      );

      const link = document.createElement('a');
      link.href = result.dataUrl;
      const resLabel = settings.resolution === 'screen' ? 'HD' : settings.resolution.toUpperCase();
      link.download = `orbitwall-${activeRegion.name.replace(/\s+/g, '-').toLowerCase()}-${resLabel}.jpg`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

    } catch (e) {
      console.error("Download failed", e);
      setError("Failed to generate high-res download.");
    } finally {
      setIsDownloading(false);
    }
  };

  const navigateToEditor = (region: Region) => {
    setActiveRegion(region);
    setCurrentLocation(region.location);
    setShowUI(true); // Show UI initially to let user know controls exist
    setView('editor');
  };

  return (
    <div className="relative w-full h-screen bg-slate-950 overflow-hidden font-sans text-white">
      {/* Main Content Area */}
      {view === 'gallery' ? (
        <Gallery onSelect={navigateToEditor} />
      ) : (
        <>
           {/* Editor View */}
           <div 
             className="absolute inset-0 z-0 touch-none cursor-move bg-black"
             onTouchStart={handleTouchStart}
             onTouchMove={handleTouchMove}
             onTouchEnd={handleTouchEnd}
             // Fallback for mouse dragging
             onMouseDown={(e) => {
                if(e.button !== 0) return;
                panStart.current = { x: e.clientX, y: e.clientY };
                lastPanPos.current = { x: e.clientX, y: e.clientY };
                totalPanDistance.current = 0;
             }}
             onMouseMove={(e) => {
                if(panStart.current && e.buttons === 1) {
                   const x = e.clientX;
                   const y = e.clientY;
                   setPanOffset({ x: x - panStart.current.x, y: y - panStart.current.y });
                   if (lastPanPos.current) {
                     totalPanDistance.current += Math.hypot(x - lastPanPos.current.x, y - lastPanPos.current.y);
                   }
                   lastPanPos.current = { x, y };
                }
             }}
             onMouseUp={handleTouchEnd}
             onMouseLeave={() => { if(panStart.current) handleTouchEnd(); }}
             style={{ 
               transform: `translate(${panOffset.x}px, ${panOffset.y}px) scale(${pinchScale})`, 
               transition: (pinchStartDist.current || panStart.current) ? 'none' : 'transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1)' 
             }}
           >
             <Preview 
                imageSrc={previewImage} 
                isLoading={isProcessing} 
                dimensions={screenDim} 
             />
           </div>

           {/* Header */}
           <div className={`absolute top-0 left-0 right-0 z-30 p-6 pt-8 bg-gradient-to-b from-black/80 to-transparent pointer-events-none transition-transform duration-300 ${showUI ? 'translate-y-0' : '-translate-y-full'}`}>
            <div className="flex items-center gap-4 pointer-events-auto justify-between">
              <div className="flex items-center gap-2">
                <button 
                  onClick={() => setView('gallery')}
                  className="p-2 -ml-2 rounded-full hover:bg-white/10 active:scale-95 transition-all text-white"
                >
                  <ChevronLeft size={28} />
                </button>
                <div className="scale-75 origin-left">
                  <Logo />
                </div>
              </div>
              {/* Removed live metadata to avoid confusion as requested */}
            </div>
          </div>

           {/* Error Toast */}
           {error && (
             <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-red-500/90 px-6 py-3 rounded-lg backdrop-blur-md shadow-xl text-center pointer-events-none">
               <p className="font-bold">Connection Error</p>
               <p className="text-sm">{error}</p>
             </div>
           )}

           {/* Loading / Processing Indicator for Download */}
           {isDownloading && (
              <div className="absolute inset-0 z-50 bg-black/80 backdrop-blur-md flex flex-col items-center justify-center pointer-events-auto">
                 <RefreshCw className="animate-spin text-cyan-400 mb-4" size={40} />
                 <h2 className="text-xl font-bold">Rendering High-Res...</h2>
                 <p className="text-slate-400 text-sm mt-2">Stitching tiles for {settings.resolution.toUpperCase()} output</p>
              </div>
           )}

           {/* Bottom Controls */}
           <div className={`absolute bottom-0 left-0 right-0 z-20 flex flex-col items-center pointer-events-none transition-transform duration-300 ${showUI ? 'translate-y-0' : 'translate-y-full'}`}>
             {/* Action Buttons */}
             <div className="w-full max-w-md px-6 flex gap-4 mb-4 pointer-events-auto">
               <button 
                 onClick={handleDownload}
                 disabled={!previewImage || isProcessing || isDownloading}
                 className="flex-1 bg-cyan-500 hover:bg-cyan-400 disabled:bg-slate-700 disabled:text-slate-500 text-white font-bold py-4 rounded-xl shadow-lg shadow-cyan-900/20 flex items-center justify-center gap-2 transition-all active:scale-95"
               >
                 <Download size={20} />
                 {isDownloading ? 'Processing...' : 'Save Wallpaper'}
               </button>
             </div>

             {/* Editor Controls */}
             <Controls 
               settings={settings} 
               onChange={setSettings} 
               isProcessing={isProcessing} 
             />
           </div>
        </>
      )}
    </div>
  );
}