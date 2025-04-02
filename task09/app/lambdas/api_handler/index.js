const OpenMeteoSDK = require('/opt/nodejs/node_modules/open-meteo-sdk');

/**
 * Lambda function handler to process API Gateway requests
 * @param {Object} event - API Gateway event
 * @returns {Object} - API Gateway response
 */
exports.handler = async (event) => {
  console.log('Event received:', JSON.stringify(event));
  
  // Extract path and method from the event
  const path = event.rawPath || '';
  const method = event.requestContext?.http?.method || '';
  
  // Only process GET requests to /weather endpoint
  if (path === '/weather' && method === 'GET') {
    try {
      // Initialize the SDK
      const openMeteoSDK = new OpenMeteoSDK();
      
      // Get weather forecast
      const weatherData = await openMeteoSDK.getWeatherForecast();
      
      // Return successful response with the weather data
      return {
        statusCode: 200,
        body: weatherData,
        headers: {
          'content-type': 'application/json'
        },
        isBase64Encoded: false
      };
    } catch (error) {
      console.error('Error fetching weather data:', error);
      
      // Return error response
      return {
        statusCode: 500,
        body: {
          statusCode: 500,
          message: 'Internal server error'
        },
        headers: {
          'content-type': 'application/json'
        },
        isBase64Encoded: false
      };
    }
  } else {
    // Return bad request for any other endpoint or method
    return {
      statusCode: 400,
      body: {
        statusCode: 400,
        message: `Bad request syntax or unsupported method. Request path: ${path}. HTTP method: ${method}`
      },
      headers: {
        'content-type': 'application/json'
      },
      isBase64Encoded: false
    };
  }
};