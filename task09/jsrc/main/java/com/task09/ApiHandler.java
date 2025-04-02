package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "api_handler",
    roleName = "api_handler-role",
    isPublishVersion = true,
    aliasName = "learn",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@LambdaUrlConfig(
    authType = AuthType.NONE,
    invokeMode = InvokeMode.BUFFERED
)
@LambdaLayer(
    layerName = "weather_sdk",
    libraries = {"lib/weather-sdk-1.0.0.jar"},
    runtime = DeploymentRuntime.JAVA11,
    artifactExtension = ArtifactExtension.ZIP
)
public class ApiHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final WeatherSDK weatherSDK = new WeatherSDK();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        resultMap.put("headers", headers);
        resultMap.put("isBase64Encoded", false);

        try {
            // Extract path and method from the event
            Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
            Map<String, Object> http = (Map<String, Object>) requestContext.get("http");
            String path = (String) input.get("rawPath");
            String method = (String) http.get("method");

            // Only process GET requests to /weather endpoint
            if ("/weather".equals(path) && "GET".equals(method)) {
                try {
                    // Get weather forecast
                    String weatherData = weatherSDK.getWeatherForecast();
                    
                    // Parse the JSON string to a Map for proper response formatting
                    Map<String, Object> weatherDataMap = objectMapper.readValue(weatherData, Map.class);
                    
                    resultMap.put("statusCode", 200);
                    resultMap.put("body", weatherDataMap);
                    
                    return resultMap;
                } catch (Exception e) {
                    context.getLogger().log("Error fetching weather data: " + e.getMessage());
                    
                    Map<String, Object> errorBody = new HashMap<>();
                    errorBody.put("statusCode", 500);
                    errorBody.put("message", "Internal server error");
                    
                    resultMap.put("statusCode", 500);
                    resultMap.put("body", errorBody);
                    
                    return resultMap;
                }
            } else {
                // Return bad request for any other endpoint or method
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("statusCode", 400);
                errorBody.put("message", String.format("Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method));
                
                resultMap.put("statusCode", 400);
                resultMap.put("body", errorBody);
                
                return resultMap;
            }
        } catch (Exception e) {
            context.getLogger().log("Error processing request: " + e.getMessage());
            
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("statusCode", 500);
            errorBody.put("message", "Internal server error");
            
            resultMap.put("statusCode", 500);
            resultMap.put("body", errorBody);
            
            return resultMap;
        }
    }
}