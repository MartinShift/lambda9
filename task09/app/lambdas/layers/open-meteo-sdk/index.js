const https = require('https');

/**
 * A lightweight SDK for interacting with the Open-Meteo API
 */
class OpenMeteoSDK {
  /**
   * Fetches weather forecast data from Open-Meteo API
   * @returns {Promise<Object>} - Weather forecast data
   */
  async getWeatherForecast() {
    // Using the exact URL from the task description
    const url = 'https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m';
    
    return this._makeRequest(url);
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