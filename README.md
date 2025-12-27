# Just Weather

A standalone Wear OS weather app that displays current conditions, temperature, and sunrise/sunset times directly on your watch. No phone companion app required, no location permissions needed.

## Features

- **Manual city selection** - Enter any city name manually, no location permissions required
- **Current weather display** - Temperature, conditions, and weather icons that adapt to day/night
- **Unit system support** - Switch between metric and imperial units
- **Astronomy data** - Sunrise and sunset times for your selected location
- **Wear OS complications** - Add weather to your watch face with automatic updates
- **Standalone operation** - Works entirely on your watch without a phone connection
- **Material icons** - Uses material icons to display weather status

<div>
   <img width="300" height="300" alt="image" src="https://github.com/user-attachments/assets/8beef09c-516a-4e2b-bbfd-13373458d368" />
   <img width="300" height="300" alt="image" src="https://github.com/user-attachments/assets/baef2eeb-de69-4e49-b2d1-3107dec39f74" />
</div>
<div>
   <img width="300" height="300" alt="image" src="https://github.com/user-attachments/assets/872ac87e-2831-4872-9f77-895bda05bb8b" />
   <img width="300" height="300" alt="image" src="https://github.com/user-attachments/assets/b4ee34b7-dac7-4258-abb1-d5a59f9e4c58" />
</div>

## Requirements

- Wear OS 3.0+ (Android API 30+)
- OpenWeatherMap API key

## Setup

1. Clone this repository
2. Copy `local.properties.example` to `local.properties`
3. Get your API key from [OpenWeatherMap](https://openweathermap.org/api) and add it to `local.properties`:
   ```
   WEATHER_API_KEY=your_api_key_here
   ```
   
   Note: The app uses OpenWeatherMap's Geocoding API to convert city names to coordinates (no location permissions required).
4. Open the project in Android Studio
5. Connect a Wear OS device or start an emulator
6. Build and run the app

On first launch, you'll be prompted to select your unit system and enter a city name.
