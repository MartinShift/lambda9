const https = require('https');

/**
 * A lightweight SDK for interacting with the Open-Meteo API
 */
class OpenMeteoSDK {
  /**
   * Fetches weather forecast data from Open-Meteo API
   * @param {Object} options - Configuration options
   * @param {number} options.latitude - Latitude coordinate
   * @param {number} options.longitude - Longitude coordinate
   * @param {Array} options.current - Current weather variables to include
   * @param {Array} options.hourly - Hourly forecast variables to include
   * @returns {Promise<Object>} - Weather forecast data
   */
  async getWeatherForecast(options = {}) {
    const {
      latitude = 52.52,
      longitude = 13.41,
      current = ['temperature_2m', 'wind_speed_10m'],
      hourly = ['temperature_2m', 'relative_humidity_2m', 'wind_speed_10m']
    } = options;

    const url = this._buildUrl(latitude, longitude, current, hourly);
    
    return this._makeRequest(url);
  }

  /**
   * Builds the URL for the Open-Meteo API request
   * @private
   */
  _buildUrl(latitude, longitude, current, hourly) {
    const baseUrl = 'https://api.open-meteo.com/v1/forecast';
    const currentParams = current.join(',');
    const hourlyParams = hourly.join(',');
    
    return `${baseUrl}?latitude=${latitude}&longitude=${longitude}&current=${currentParams}&hourly=${hourlyParams}`;
  }

  /**
   * Makes an HTTPS request to the Open-Meteo API
   * @private
   */
  _makeRequest(url) {
    return new Promise((resolve, reject) => {
      https.get(url, (res) => {
        let data = '';
        
        res.on('data', (chunk) => {
          data += chunk;
        });
        
        res.on('end', () => {
          try {
            const parsedData = JSON.parse(data);
            resolve(parsedData);
          } catch (error) {
            reject(new Error(`Error parsing response: ${error.message}`));
          }
        });
      }).on('error', (error) => {
        reject(new Error(`Error making request: ${error.message}`));
      });
    });
  }
}

module.exports = OpenMeteoSDK;