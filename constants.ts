import { Region, WallSettings } from './types';

// ESRI World Imagery URL Template
// Uses standard XYZ.
export const TILE_URL_TEMPLATE = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}';

// Extra pixels to fetch around the screen to allow for panning
export const PREVIEW_OVERSCAN = 512;

export const DEFAULT_SETTINGS: WallSettings = {
  blur: 0,
  brightness: 1,
  overlayOpacity: 0.1,
  overlayColor: '#000000',
  zoomOffset: -2,
  resolution: '2k',
};

export const PREDEFINED_REGIONS: Region[] = [
  {
    id: '1',
    name: 'Richat Structure',
    location: { lat: 21.1269, lon: -11.4016 },
    zoom: 12,
    tags: ['desert', 'geology', 'africa'],
  },
  {
    id: '2',
    name: 'Palm Jumeirah',
    location: { lat: 25.1124, lon: 55.1390 },
    zoom: 14,
    tags: ['urban', 'island', 'middle-east'],
  },
  {
    id: '3',
    name: 'Grand Prismatic Spring',
    location: { lat: 44.5250, lon: -110.8382 },
    zoom: 16,
    tags: ['nature', 'colorful', 'usa'],
  },
  {
    id: '4',
    name: 'Mount Fuji',
    location: { lat: 35.3606, lon: 138.7274 },
    zoom: 13,
    tags: ['mountain', 'snow', 'asia'],
  },
  {
    id: '5',
    name: 'Great Barrier Reef',
    location: { lat: -18.2871, lon: 147.6992 },
    zoom: 15,
    tags: ['ocean', 'nature', 'australia'],
  },
  {
    id: '6',
    name: 'Central Park, NYC',
    location: { lat: 40.7829, lon: -73.9654 },
    zoom: 15,
    tags: ['urban', 'city', 'usa'],
  }
];