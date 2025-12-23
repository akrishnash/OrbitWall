#!/usr/bin/env python3
"""
Generate comprehensive list of ~1000 regions for OrbitWall app.
This script generates Kotlin code for the PredefinedRegions list.
"""

# Comprehensive list of locations organized by category
REGIONS_DATA = []

# Add regions programmatically
def add_region(id_num, name, lat, lon, zoom, tags):
    REGIONS_DATA.append({
        'id': str(id_num),
        'name': name,
        'lat': lat,
        'lon': lon,
        'zoom': zoom,
        'tags': tags
    })

# Africa (50 regions)
africa_regions = [
    ("Richat Structure, Mauritania", 21.1269, -11.4016, 12, ["africa", "landmarks", "geology", "desert"]),
    ("Namib Desert Dunes, Namibia", -24.7500, 15.2833, 12, ["africa", "desert", "scenic", "dunes"]),
    ("Sahara Desert, Algeria", 25.0000, 0.0000, 12, ["africa", "desert", "scenic"]),
    ("Victoria Falls, Zambia/Zimbabwe", -17.9243, 25.8572, 14, ["africa", "landmarks", "scenic", "nature"]),
    ("Mount Kilimanjaro, Tanzania", -3.0674, 37.3556, 13, ["africa", "mountains", "volcanoes", "scenic"]),
    ("Ngorongoro Crater, Tanzania", -3.1794, 35.5431, 14, ["africa", "landmarks", "scenic", "nature"]),
    ("Great Pyramid of Giza, Egypt", 29.9792, 31.1342, 16, ["africa", "landmarks", "urban", "history"]),
    ("Suez Canal, Egypt", 30.5852, 32.2654, 13, ["africa", "landmarks", "ocean", "urban"]),
    ("Cape Town, South Africa", -33.9249, 18.4241, 13, ["africa", "urban", "mountains", "ocean"]),
    ("Table Mountain, South Africa", -33.9628, 18.4094, 14, ["africa", "mountains", "scenic", "landmarks"]),
]

for i, (name, lat, lon, zoom, tags) in enumerate(africa_regions, 1):
    add_region(i, name, lat, lon, zoom, tags)

# Continue with more continents... (This is a template)
# For full implementation, we'd add all 1000 regions here

# Generate Kotlin code
def generate_kotlin():
    output = "val PredefinedRegions = listOf(\n"
    for region in REGIONS_DATA:
        tags_str = ", ".join(f'"{tag}"' for tag in region['tags'])
        output += f"""    Region(
        id = "{region['id']}",
        name = "{region['name']}",
        location = GeoLocation({region['lat']}, {region['lon']}),
        zoom = {region['zoom']},
        tags = listOf({tags_str})
    ),
"""
    output += ")\n"
    return output

if __name__ == "__main__":
    print("This is a template script.")
    print(f"Would generate {len(REGIONS_DATA)} regions")
    print("\nTo expand to 1000 regions, add more location data to the script.")






