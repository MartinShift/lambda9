from commons.log_helper import get_logger
from commons.abstract_lambda import AbstractLambda
import json
import requests

_LOG = get_logger(__name__)

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

class ApiHandler(AbstractLambda):

   def validate_request(self, event) -> dict:
    if not event:
        return {
            'is_valid': False,
            'status_code': 400,
            'body': {
                'message': "Invalid event structure"
            }
        }

    path = event.get('rawPath', '')
    method = event.get('requestContext', {}).get('http', {}).get('method', '')

    if path == '/weather' and method == 'GET':
        return {'is_valid': True}
    
    return {
        'is_valid': False,
        'status_code': 400,
        'body': {
            'message': f"Bad request syntax or unsupported method. Request path: {path}. HTTP method: {method}"
        }
    }
        
    def handle_request(self, event, context):
        validation_result = self.validate_request(event)
        if not validation_result['is_valid']:
            return validation_result['status_code'], validation_result['body']

        try:
            weather_data = WeatherSDK.get_weather()
            return 200, weather_data
        except Exception as e:
            _LOG.error(f"Error fetching weather data: {str(e)}")
            return 500, {'error': 'Internal server error'}

HANDLER = ApiHandler()

def lambda_handler(event, context):
    status_code, body = HANDLER.lambda_handler(event=event, context=context)
    return {
        'statusCode': status_code,
        'body': json.dumps(body),
        'headers': {
            'Content-Type': 'application/json'
        },
        'isBase64Encoded': False
    }