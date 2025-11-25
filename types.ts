export interface GeoLocation {
  lat: number;
  lon: number;
}

export interface Region {
  id: string;
  name: string;
  location: GeoLocation;
  zoom: number; // Suggested base zoom
  tags: string[];
}

export type Resolution = 'screen' | '2k' | '4k';

export interface WallSettings {
  blur: number; // 0-20px
  brightness: number; // 0.5 - 1.5
  overlayOpacity: number; // 0 - 1
  overlayColor: string; // hex
  zoomOffset: number; // -1 to +2 relative to base
  resolution: Resolution;
}

export interface Dimensions {
  width: number;
  height: number;
}

export interface TileCoordinate {
  x: number;
  y: number;
  z: number;
}