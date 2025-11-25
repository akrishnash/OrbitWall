import { GeoLocation, TileCoordinate } from '../types';

export const latLonToTile = (lat: number, lon: number, zoom: number): TileCoordinate => {
  const n = Math.pow(2, zoom);
  const x = Math.floor(n * ((lon + 180) / 360));
  const latRad = (lat * Math.PI) / 180;
  const y = Math.floor(
    (n * (1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2
  );
  return { x, y, z: zoom };
};

// Returns floating point tile coordinates for smooth panning math
export const latLonToTilePoint = (lat: number, lon: number, zoom: number): { x: number; y: number } => {
  const n = Math.pow(2, zoom);
  const x = n * ((lon + 180) / 360);
  const latRad = (lat * Math.PI) / 180;
  const y = (n * (1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2;
  return { x, y };
};

export const tileToLatLon = (x: number, y: number, z: number): GeoLocation => {
  const n = Math.pow(2, z);
  const lon = (x / n) * 360 - 180;
  const latRad = Math.atan(Math.sinh(Math.PI * (1 - (2 * y) / n)));
  const lat = (latRad * 180) / Math.PI;
  return { lat, lon };
};

// Calculate how many meters per pixel at a given latitude and zoom
// (Approximate for spherical mercator)
export const metersPerPixel = (lat: number, zoom: number): number => {
  return (156543.03392 * Math.cos((lat * Math.PI) / 180)) / Math.pow(2, zoom);
};

export const getTileUrl = (template: string, x: number, y: number, z: number): string => {
  return template
    .replace('{z}', z.toString())
    .replace('{x}', x.toString())
    .replace('{y}', y.toString());
};
