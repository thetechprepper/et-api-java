## ET API

Note: This is not official API documentation for EmComm Tools. The API is
      still in development. These are only working notes.

### National Weather Service API

List closet NWS forecst zones near my current location.

```
curl "http://localhost:1981/api/nws/forecast-zones
```

List NWS forecst zones near lat/lon sorted by closest distance.

```
curl "http://localhost:1981/api/nws/forecast-zones?lat=33.0&lon=-112.0
```

Fetch plain text weather forecast for current location. *Online access required*
```
curl "http://localhost:1981/api/nws/forecast/raw"
```

Fetch plain text weather forecast for Linden, TN. *Online access required*
``
curl "http://localhost:1981/api/nws/forecast/raw?lat=35.797&lon=-87.753"
```

Create a Winlink email for the FTPmail forecast request based on your area.
```
curl -X POST "http://localhost:1981/api/winlink/messages/weather/nws/forecast
```
