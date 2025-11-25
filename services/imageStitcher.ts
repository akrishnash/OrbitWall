import { Dimensions, GeoLocation, WallSettings } from '../types';
import { getTileUrl } from './tileMath';
import { TILE_URL_TEMPLATE } from '../constants';

const TILE_SIZE = 256;

interface StitchResult {
  dataUrl: string;
  blob: Blob | null;
}

/**
 * Downloads tiles covering the viewport defined by center location and screen dimensions,
 * stitches them, applies effects, and returns the result.
 */
export const generateWallpaper = async (
  center: GeoLocation,
  screen: Dimensions,
  settings: WallSettings,
  zoomLevel: number
): Promise<StitchResult> => {
  
  // Ensure strict integer zoom for URL fetching, but logic allows for scaling if needed in future
  const z = Math.floor(zoomLevel);

  // 1. Calculate the center tile coordinates (fractional)
  const n = Math.pow(2, z);
  const centerX_World = n * ((center.lon + 180) / 360);
  const latRad = (center.lat * Math.PI) / 180;
  const centerY_World = (n * (1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2;

  // 2. Determine bounds in Tile Space
  // screen.width / TILE_SIZE is how many tiles wide the screen is
  const screenTilesW = screen.width / TILE_SIZE;
  const screenTilesH = screen.height / TILE_SIZE;

  const minTileX = Math.floor(centerX_World - screenTilesW / 2);
  const maxTileX = Math.floor(centerX_World + screenTilesW / 2) + 1;
  const minTileY = Math.floor(centerY_World - screenTilesH / 2);
  const maxTileY = Math.floor(centerY_World + screenTilesH / 2) + 1;

  // 3. Prepare Canvas
  const canvas = document.createElement('canvas');
  canvas.width = screen.width;
  canvas.height = screen.height;
  const ctx = canvas.getContext('2d');

  if (!ctx) throw new Error('Could not get 2D context');

  // Fill background
  ctx.fillStyle = '#000';
  ctx.fillRect(0, 0, screen.width, screen.height);

  // 4. Fetch and Draw Tiles
  const promises: Promise<void>[] = [];

  for (let x = minTileX; x <= maxTileX; x++) {
    for (let y = minTileY; y <= maxTileY; y++) {
      promises.push(
        (async () => {
          // Handle wrapping for longitude (optional, standard mercator)
          const normX = ((x % n) + n) % n; 
          
          const url = getTileUrl(TILE_URL_TEMPLATE, normX, y, z);
          
          try {
            const img = new Image();
            img.crossOrigin = 'Anonymous';
            
            await new Promise((resolve, reject) => {
              img.onload = resolve;
              img.onerror = reject;
              img.src = url;
            });

            // Calculate pixel position on canvas
            // Offset in tiles from center * TILE_SIZE + Screen Center
            // We floor the coordinates to avoid anti-aliasing seams between tiles
            const drawX = (x - centerX_World) * TILE_SIZE + screen.width / 2;
            const drawY = (y - centerY_World) * TILE_SIZE + screen.height / 2;

            // Draw slightly larger to overlap seams
            ctx.drawImage(img, Math.floor(drawX), Math.floor(drawY), TILE_SIZE + 1, TILE_SIZE + 1);
          } catch (e) {
            console.warn(`Failed to load tile ${x},${y}`, e);
            // Optionally draw a placeholder
            ctx.fillStyle = '#111';
            const drawX = (x - centerX_World) * TILE_SIZE + screen.width / 2;
            const drawY = (y - centerY_World) * TILE_SIZE + screen.height / 2;
            ctx.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
          }
        })()
      );
    }
  }

  await Promise.all(promises);

  // 5. Apply Effects (Post-processing)
  
  // Clone to apply filters cleanly
  const outputCanvas = document.createElement('canvas');
  outputCanvas.width = screen.width;
  outputCanvas.height = screen.height;
  const outCtx = outputCanvas.getContext('2d');
  if(!outCtx) throw new Error("No Out Context");

  // Brightness / Contrast / Blur via filter string
  // Note: Canvas filter support is good in modern browsers.
  const blurPx = settings.blur;
  const brightness = settings.brightness * 100; // to percentage
  outCtx.filter = `brightness(${brightness}%) blur(${blurPx}px)`;
  
  outCtx.drawImage(canvas, 0, 0);
  outCtx.filter = 'none'; // reset

  // Overlay
  if (settings.overlayOpacity > 0) {
    outCtx.fillStyle = settings.overlayColor;
    outCtx.globalAlpha = settings.overlayOpacity;
    outCtx.fillRect(0, 0, screen.width, screen.height);
    outCtx.globalAlpha = 1.0;
  }

  // 6. Output
  return new Promise((resolve, reject) => {
    outputCanvas.toBlob((blob) => {
      if (blob) {
        resolve({
          dataUrl: outputCanvas.toDataURL('image/jpeg', 0.92),
          blob: blob
        });
      } else {
        reject(new Error('Canvas to Blob failed'));
      }
    }, 'image/jpeg', 0.92);
  });
};
