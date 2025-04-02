import requests

class WeatherSDK:
    BASE_URL = "https://api.open-meteo.com/v1/forecast"

    @staticmethod
    def get_weather(latitude=52.52, longitude=13.41):
        params = {
            "latitude": latitude,
            "longitude": longitude,
            "current": "temperature_2m,wind_speed_10m",
            "hourly": "temperature_2m,relative_humidity_2m,wind_speed_10m"
        }
        response = requests.get(WeatherSDK.BASE_URL, params=params)
        response.raise_for_status()
        return response.json()