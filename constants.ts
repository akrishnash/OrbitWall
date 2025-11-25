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

export const CATEGORIES = [
  'All',
  'Urban',
  'Nature',
  'Ocean',
  'Desert',
  'Mountain',
  'Ice',
  'Agriculture',
  'Geology'
];

export const PREDEFINED_REGIONS: Region[] = [
  // --- ORIGINALS ---
  { id: '1', name: 'Richat Structure', location: { lat: 21.1269, lon: -11.4016 }, zoom: 12, tags: ['desert', 'geology', 'africa'] },
  { id: '2', name: 'Palm Jumeirah', location: { lat: 25.1124, lon: 55.1390 }, zoom: 14, tags: ['urban', 'island', 'middle-east'] },
  { id: '3', name: 'Grand Prismatic Spring', location: { lat: 44.5250, lon: -110.8382 }, zoom: 16, tags: ['nature', 'colorful', 'usa'] },
  { id: '4', name: 'Mount Fuji', location: { lat: 35.3606, lon: 138.7274 }, zoom: 13, tags: ['mountain', 'snow', 'asia'] },
  { id: '5', name: 'Great Barrier Reef', location: { lat: -18.2871, lon: 147.6992 }, zoom: 15, tags: ['ocean', 'nature', 'australia'] },
  { id: '6', name: 'Central Park, NYC', location: { lat: 40.7829, lon: -73.9654 }, zoom: 14, tags: ['urban', 'city', 'usa'] },

  // --- URBAN & ARCHITECTURE ---
  { id: 'u1', name: 'Eixample, Barcelona', location: { lat: 41.3888, lon: 2.1590 }, zoom: 16, tags: ['urban', 'pattern', 'europe'] },
  { id: 'u2', name: 'Arc de Triomphe', location: { lat: 48.8738, lon: 2.2950 }, zoom: 17, tags: ['urban', 'europe', 'landmark'] },
  { id: 'u3', name: 'Brasilia Axis', location: { lat: -15.7934, lon: -47.8823 }, zoom: 14, tags: ['urban', 'pattern', 'south-america'] },
  { id: 'u4', name: 'Sun City, Arizona', location: { lat: 33.5975, lon: -112.2718 }, zoom: 14, tags: ['urban', 'pattern', 'usa'] },
  { id: 'u5', name: 'La Plata, Argentina', location: { lat: -34.9214, lon: -57.9545 }, zoom: 14, tags: ['urban', 'geometry', 'south-america'] },
  { id: 'u6', name: 'New Delhi Lotus Temple', location: { lat: 28.5535, lon: 77.2588 }, zoom: 17, tags: ['urban', 'asia', 'architecture'] },
  { id: 'u7', name: 'Burning Man (Black Rock)', location: { lat: 40.7864, lon: -119.2065 }, zoom: 14, tags: ['urban', 'desert', 'usa'] },
  { id: 'u8', name: 'Venice, Italy', location: { lat: 45.4340, lon: 12.3380 }, zoom: 15, tags: ['urban', 'water', 'europe'] },
  { id: 'u9', name: 'Forbidden City', location: { lat: 39.9163, lon: 116.3972 }, zoom: 16, tags: ['urban', 'asia', 'history'] },
  { id: 'u10', name: 'Giza Pyramids', location: { lat: 29.9753, lon: 31.1376 }, zoom: 15, tags: ['desert', 'history', 'africa'] },
  { id: 'u11', name: 'Naarden Fortress', location: { lat: 52.2950, lon: 5.1610 }, zoom: 16, tags: ['urban', 'star-fort', 'europe'] },
  { id: 'u12', name: 'Bourtange Star Fort', location: { lat: 53.0066, lon: 7.1920 }, zoom: 17, tags: ['urban', 'star-fort', 'europe'] },
  { id: 'u13', name: 'Palmanova, Italy', location: { lat: 45.9064, lon: 13.3100 }, zoom: 15, tags: ['urban', 'geometry', 'europe'] },
  { id: 'u14', name: 'Chicago Grid', location: { lat: 41.8781, lon: -87.6298 }, zoom: 15, tags: ['urban', 'usa'] },
  { id: 'u15', name: 'Mexico City Sprawl', location: { lat: 19.4326, lon: -99.1332 }, zoom: 13, tags: ['urban', 'dense', 'north-america'] },

  // --- OCEAN & ISLANDS ---
  { id: 'o1', name: 'Bora Bora', location: { lat: -16.5004, lon: -151.7415 }, zoom: 13, tags: ['ocean', 'island', 'pacific'] },
  { id: 'o2', name: 'Heart Island, Croatia', location: { lat: 43.9780, lon: 15.3837 }, zoom: 16, tags: ['ocean', 'island', 'europe'] },
  { id: 'o3', name: 'Maldives Atolls', location: { lat: 3.2028, lon: 73.2207 }, zoom: 12, tags: ['ocean', 'island', 'asia'] },
  { id: 'o4', name: 'Whitsunday Islands', location: { lat: -20.2850, lon: 149.0360 }, zoom: 12, tags: ['ocean', 'sand', 'australia'] },
  { id: 'o5', name: 'Lighthouse Reef (Blue Hole)', location: { lat: 17.3160, lon: -87.5350 }, zoom: 14, tags: ['ocean', 'geology', 'caribbean'] },
  { id: 'o6', name: 'Tahaa, French Polynesia', location: { lat: -16.6130, lon: -151.4980 }, zoom: 13, tags: ['ocean', 'island', 'pacific'] },
  { id: 'o7', name: 'Eleuthera, Bahamas', location: { lat: 25.1310, lon: -76.2400 }, zoom: 11, tags: ['ocean', 'blue', 'caribbean'] },
  { id: 'o8', name: 'Key West', location: { lat: 24.5551, lon: -81.7800 }, zoom: 15, tags: ['ocean', 'usa', 'urban'] },
  { id: 'o9', name: 'Musandam Fjords', location: { lat: 26.1950, lon: 56.3680 }, zoom: 12, tags: ['ocean', 'geology', 'middle-east'] },
  { id: 'o10', name: 'Ha Long Bay', location: { lat: 20.9101, lon: 107.1839 }, zoom: 13, tags: ['ocean', 'nature', 'asia'] },
  { id: 'o11', name: 'Zakynthos (Shipwreck)', location: { lat: 37.8590, lon: 20.6250 }, zoom: 16, tags: ['ocean', 'beach', 'europe'] },

  // --- AGRICULTURE & PATTERNS ---
  { id: 'a1', name: 'Tulip Fields, Lisse', location: { lat: 52.2742, lon: 4.5488 }, zoom: 15, tags: ['agriculture', 'colorful', 'europe'] },
  { id: 'a2', name: 'Kansas Pivot Irrigation', location: { lat: 37.6690, lon: -100.8250 }, zoom: 12, tags: ['agriculture', 'pattern', 'usa'] },
  { id: 'a3', name: 'Yuanyang Rice Terraces', location: { lat: 23.1190, lon: 102.7680 }, zoom: 14, tags: ['agriculture', 'texture', 'asia'] },
  { id: 'a4', name: 'Almeria Greenhouses', location: { lat: 36.7800, lon: -2.7300 }, zoom: 13, tags: ['agriculture', 'white', 'europe'] },
  { id: 'a5', name: 'Olive Groves, Spain', location: { lat: 37.9500, lon: -3.6000 }, zoom: 15, tags: ['agriculture', 'pattern', 'europe'] },
  { id: 'a6', name: 'Saudi Arabia Irrigation', location: { lat: 30.1000, lon: 38.3000 }, zoom: 12, tags: ['agriculture', 'desert', 'middle-east'] },
  { id: 'a7', name: 'Rapeseed Fields, Luoping', location: { lat: 24.8830, lon: 104.3160 }, zoom: 14, tags: ['agriculture', 'yellow', 'asia'] },
  { id: 'a8', name: 'Palouse Fields', location: { lat: 46.8660, lon: -117.3800 }, zoom: 12, tags: ['agriculture', 'hills', 'usa'] },

  // --- DESERT & GEOLOGY ---
  { id: 'd1', name: 'Namib Dunes', location: { lat: -24.7270, lon: 15.3400 }, zoom: 13, tags: ['desert', 'orange', 'africa'] },
  { id: 'd2', name: 'Rub\' al Khali', location: { lat: 20.0000, lon: 50.0000 }, zoom: 11, tags: ['desert', 'pattern', 'middle-east'] },
  { id: 'd3', name: 'Monument Valley', location: { lat: 36.9980, lon: -110.0980 }, zoom: 14, tags: ['desert', 'geology', 'usa'] },
  { id: 'd4', name: 'Uluru (Ayers Rock)', location: { lat: -25.3444, lon: 131.0369 }, zoom: 14, tags: ['desert', 'red', 'australia'] },
  { id: 'd5', name: 'Salar de Uyuni', location: { lat: -20.1338, lon: -67.4891 }, zoom: 11, tags: ['desert', 'white', 'south-america'] },
  { id: 'd6', name: 'Dasht-e Kavir', location: { lat: 34.5000, lon: 54.5000 }, zoom: 10, tags: ['desert', 'pattern', 'asia'] },
  { id: 'd7', name: 'Danakil Depression', location: { lat: 14.2417, lon: 40.3000 }, zoom: 12, tags: ['desert', 'colorful', 'africa'] },
  { id: 'd8', name: 'Tsingy de Bemaraha', location: { lat: -19.0000, lon: 44.7500 }, zoom: 14, tags: ['geology', 'rock', 'africa'] },
  { id: 'd9', name: 'Nazca Lines', location: { lat: -14.7390, lon: -75.1300 }, zoom: 16, tags: ['desert', 'history', 'south-america'] },
  { id: 'd10', name: 'Badlands National Park', location: { lat: 43.7500, lon: -102.5000 }, zoom: 14, tags: ['geology', 'texture', 'usa'] },
  { id: 'd11', name: 'Wave Rock, Arizona', location: { lat: 36.9960, lon: -112.0000 }, zoom: 16, tags: ['geology', 'pattern', 'usa'] },

  // --- VOLCANOES & MOUNTAINS ---
  { id: 'm1', name: 'Mount Kilimanjaro', location: { lat: -3.0674, lon: 37.3556 }, zoom: 13, tags: ['mountain', 'nature', 'africa'] },
  { id: 'm2', name: 'Mount Vesuvius', location: { lat: 40.8224, lon: 14.4289 }, zoom: 14, tags: ['mountain', 'volcano', 'europe'] },
  { id: 'm3', name: 'Mount St. Helens', location: { lat: 46.2000, lon: -122.1900 }, zoom: 13, tags: ['mountain', 'volcano', 'usa'] },
  { id: 'm4', name: 'Aogashima', location: { lat: 32.4570, lon: 139.7670 }, zoom: 14, tags: ['mountain', 'island', 'asia'] },
  { id: 'm5', name: 'Mount Everest', location: { lat: 27.9881, lon: 86.9250 }, zoom: 12, tags: ['mountain', 'snow', 'asia'] },
  { id: 'm6', name: 'Lençóis Maranhenses', location: { lat: -2.5300, lon: -43.1200 }, zoom: 12, tags: ['nature', 'sand', 'south-america'] },
  { id: 'm7', name: 'Chocolate Hills', location: { lat: 9.9160, lon: 124.1700 }, zoom: 14, tags: ['nature', 'hills', 'asia'] },
  { id: 'm8', name: 'Rainbow Mountain', location: { lat: -13.8690, lon: -71.3030 }, zoom: 15, tags: ['mountain', 'colorful', 'south-america'] },
  { id: 'm9', name: 'Santorini Caldera', location: { lat: 36.4040, lon: 25.3960 }, zoom: 13, tags: ['mountain', 'island', 'europe'] },

  // --- ICE & GLACIERS ---
  { id: 'i1', name: 'Perito Moreno Glacier', location: { lat: -50.4800, lon: -73.0500 }, zoom: 13, tags: ['ice', 'glacier', 'south-america'] },
  { id: 'i2', name: 'Vatnajökull', location: { lat: 64.4000, lon: -16.8000 }, zoom: 10, tags: ['ice', 'glacier', 'europe'] },
  { id: 'i3', name: 'Antarctica Dry Valleys', location: { lat: -77.4660, lon: 162.5000 }, zoom: 11, tags: ['ice', 'desert', 'antarctica'] },
  { id: 'i4', name: 'Greenland Ice Sheet', location: { lat: 72.0000, lon: -40.0000 }, zoom: 9, tags: ['ice', 'white', 'north-america'] },
  { id: 'i5', name: 'Jökulsárlón', location: { lat: 64.0480, lon: -16.1790 }, zoom: 14, tags: ['ice', 'lake', 'europe'] },

  // --- RIVERS & LAKES ---
  { id: 'w1', name: 'Amazon River Delta', location: { lat: -0.1600, lon: -50.6000 }, zoom: 9, tags: ['water', 'river', 'south-america'] },
  { id: 'w2', name: 'Lake Hillier (Pink)', location: { lat: -34.0950, lon: 123.2030 }, zoom: 15, tags: ['water', 'colorful', 'australia'] },
  { id: 'w3', name: 'Nile River Delta', location: { lat: 30.9000, lon: 31.1000 }, zoom: 9, tags: ['water', 'green', 'africa'] },
  { id: 'w4', name: 'Mississippi Delta', location: { lat: 29.1500, lon: -89.2500 }, zoom: 10, tags: ['water', 'pattern', 'usa'] },
  { id: 'w5', name: 'Victoria Falls', location: { lat: -17.9243, lon: 25.8560 }, zoom: 15, tags: ['water', 'nature', 'africa'] },
  { id: 'w6', name: 'Plitvice Lakes', location: { lat: 44.8800, lon: 15.6100 }, zoom: 15, tags: ['water', 'nature', 'europe'] },
  { id: 'w7', name: 'Lake Baikal', location: { lat: 53.5000, lon: 108.0000 }, zoom: 9, tags: ['water', 'ice', 'asia'] },
  { id: 'w8', name: 'Lake Natron', location: { lat: -2.4160, lon: 36.0000 }, zoom: 12, tags: ['water', 'red', 'africa'] },

  // --- MISC & MAN-MADE ---
  { id: 'x1', name: 'Golden Gate Bridge', location: { lat: 37.8199, lon: -122.4783 }, zoom: 16, tags: ['urban', 'bridge', 'usa'] },
  { id: 'x2', name: 'Sydney Opera House', location: { lat: -33.8568, lon: 151.2153 }, zoom: 17, tags: ['urban', 'landmark', 'australia'] },
  { id: 'x3', name: 'Ferrari World', location: { lat: 24.4840, lon: 54.6070 }, zoom: 16, tags: ['urban', 'red', 'middle-east'] },
  { id: 'x4', name: 'Millau Viaduct', location: { lat: 44.0770, lon: 3.0230 }, zoom: 15, tags: ['urban', 'bridge', 'europe'] },
  { id: 'x5', name: 'Three Gorges Dam', location: { lat: 30.8230, lon: 111.0030 }, zoom: 14, tags: ['urban', 'water', 'asia'] },
  { id: 'x6', name: 'Kennedy Space Center', location: { lat: 28.5721, lon: -80.6480 }, zoom: 14, tags: ['urban', 'tech', 'usa'] },
  { id: 'x7', name: 'Area 51', location: { lat: 37.2340, lon: -115.8110 }, zoom: 13, tags: ['urban', 'desert', 'usa'] },
  { id: 'x8', name: 'Chernobyl Reactor 4', location: { lat: 51.3890, lon: 30.0990 }, zoom: 16, tags: ['urban', 'history', 'europe'] },
  { id: 'x9', name: 'Angkor Wat', location: { lat: 13.4125, lon: 103.8667 }, zoom: 16, tags: ['urban', 'history', 'asia'] },
  { id: 'x10', name: 'Machu Picchu', location: { lat: -13.1631, lon: -72.5450 }, zoom: 16, tags: ['mountain', 'history', 'south-america'] }
];
