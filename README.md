# EmComm Tools API

TODO

## Data Sets

### FAA Aircraft Registration CSV Format

The FAA dataset is provided as a pipe-delimited (`|`) file with the following fields:

| Column           | Type    | Description                                           | Example       |
|------------------|---------|-------------------------------------------------------|---------------|
| `tail_number`    | String  | Aircraft tail number (registration number)            | `N12345`      |
| `make`           | String  | Manufacturer of the aircraft                          | `Cessna`      |
| `model`          | String  | Model designation                                     | `172 Skyhawk` |
| `year`           | Integer | Year of manufacture (may be blank if unknown)         | `1998`        |
| `owner_name`     | String  | Registered owner or organization                      | `John Smith`  |
| `city`           | String  | City of the registrant                                | `Phoenix`     |
| `state`          | String  | Two-letter US state code                              | `AZ`          |
| `mode_s_hex`     | String  | Mode S transponder hexadecimal code (ICAO24)          | `A1B2C3`      |
| `registrant_type`| String  | Registrant category (e.g., Individual, Corporation)   | `Individual`  |

**Note:**  
- Fields are separated by a `|` character, **not** commas.  
- Missing string values are represented by an empty space between delimiters: `...| |...`.  
- Missing years are represented by: `-1`.

### Winlink RMS Channel CSV Format

The Winlink RMS channel dataset is provided as a pipe-delimited (`|`) CSV.  Each line in the output 
represents one RMS gateway channel record.

| Column           | Type    | Description                                           | Example       |
|------------------|---------|-------------------------------------------------------|---------------|
| `Base Callsign`  | String  | Callsign without SSID suffix                          | `KT7RUN`      |
| `Callsign`       | String  | Station callsign with optional SSID suffix            | `KT7RUN-10`   |
| `Latitude`       | Double  | Latitude in decimal degrees                           | `33.0`        |
| `Longitude`      | Double  | Longitude in decimal degrees                          | `-112.0`      |
| `Mode `          | String  | Winlink mode as friendly name                         | `Packet 1200` |
| `Mode Code`      | String  | Winlink mode code from Winlink                        | `0`           |
| `Frequency`      | Double  | Frequency in Hertz                                    | `145710000`   |
