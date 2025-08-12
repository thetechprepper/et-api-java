# EmComm Tools API

TODO

## Data Sets

### FAA Aircraft Registration CSV Format

The dataset is provided as a pipe-delimited (`|`) file with the following fields:

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

