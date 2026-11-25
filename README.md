# Kata 3 — USGS seism catcher

Java client that consumes the **USGS Earthquake** web service with `java.net.http.HttpClient`, deserializes the GeoJSON
response with **Gson**, classifies and summarises the events, and writes the result as a **UTF-8 JSON file** with Gson.

> Kata 3 · Ingeniería de Software 2 · EII ULPGC — *Consumo de un servicio web y serialización JSON*

**Video (≤ 7 min):** _TODO: add link_

---

## Goal

- Make a `GET` request with `HttpClient`, controlling method, timeout, status code and body reading.
- Distinguish **deserialization** of the response (external DTOs) from **serialization** of our own result.
- Keep HTTP communication, data transformation and result writing in separate components.
- Report HTTP errors, connection/timeout failures and invalid JSON responses differently.

## Requirements

| Tool | Version |
|---|---|
| JDK | 21 |
| Maven | 3.9+ |
| Gson | 2.15.0 |
| JUnit Jupiter (tests only) | 5.11.3 |

No API key or credentials are needed: the USGS feeds are public.

## Build and run

```powershell
mvn compile
```

```powershell
mvn -q compile exec:java "-Dexec.mainClass=reivajh06.kata3.Main"
```

The result is written to `temp/results.json` (the folder is created if missing and is git-ignored).

Run the tests:

```powershell
mvn test
```

## Service consumed

| | |
|---|---|
| Base URL | `https://earthquake.usgs.gov/` (default in `USGSSource`, overridable with `USGSSource.ofPath(...)`) |
| Endpoint | `GET earthquakes/feed/v1.0/summary/all_day.geojson` |
| Content | All seismic events of the **last 24 hours** (rolling window), all magnitudes |
| Format | GeoJSON `FeatureCollection`, refreshed by USGS every few minutes |
| Docs | <https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php> |

### Contract: fields consumed

Only these fields are modelled in the input DTOs (`dtos/SeismDto`, `PropertiesDto`, `GeometryDto`); everything else is ignored by Gson.

| JSON path | Type | Notes |
|---|---|---|
| `features[].id` | string | event identifier |
| `features[].properties.mag` | number \| null | **can be null** → event is skipped |
| `features[].properties.place` | string \| null | free text, e.g. `"5 km S of Guánica, Puerto Rico"` |
| `features[].properties.time` / `updated` | number | epoch milliseconds (UTC) |
| `features[].properties.type` | string | `earthquake`, `quarry blast`, … (unknown values → `OTHER`) |
| `features[].properties.title` | string | |
| `features[].geometry.coordinates` | `[lon, lat, depthKm]` | **longitude first** (GeoJSON order) |

### Example input (one feature)

```json
{
  "type": "Feature",
  "id": "us1",
  "properties": {
    "mag": 4.3,
    "place": "18 km SSW of Yonakuni, Japan",
    "time": 1795601925642,
    "updated": 1795602794040,
    "type": "earthquake",
    "title": "M 4.3 - 18 km SSW of Yonakuni, Japan"
  },
  "geometry": { "type": "Point", "coordinates": [122.9622, 24.3005, 46.912] }
}
```

The full fixture used by the tests (5 events, including a non-ASCII place, a quarry blast and an event with `mag: null`)
is [`src/test/resources/usgs/sample.geojson`](src/test/resources/usgs/sample.geojson).

### Example output

Generated from that fixture ([`expected-analysis.json`](src/test/resources/usgs/expected-analysis.json), plus the
`generatedAt` timestamp):

```json
{
  "generatedAt": "2026-11-25T13:01:19.367950300Z",
  "total": 5,
  "skipped": 1,
  "byClass": { "MICRO": 2, "MINOR": 1, "LIGHT": 1 },
  "byType": { "EARTHQUAKE": 3, "QUARRY_BLAST": 1 },
  "strongest": {
    "id": "us1",
    "magnitude": 4.3,
    "type": "EARTHQUAKE",
    "seismicClass": "LIGHT",
    "place": {
      "description": "18 km SSW of Yonakuni, Japan",
      "coordinates": { "latitude": 24.3005, "longitude": 122.9622, "depth": 46.912 }
    },
    "time": "2026-11-25T10:18:45.642Z",
    "updated": "2026-11-25T10:33:14.040Z"
  }
}
```

How the output is derived:

- `total` = events received, `skipped` = events without a usable `mag`, `place`, `time`, `updated`, `type`, `title` or coordinates.
- `byClass` = count per USGS magnitude class; `byType` = count per event type. Only classes/types present are listed.
- `strongest` = event with the highest magnitude (`null` if no event is usable).

### Magnitude classes

Based on the USGS chart [*Earthquake and energy comparisons (updated 2025)*](https://www.usgs.gov/media/images/earthquake-and-energy-comparisons-updated-2025).
A value equal to a lower bound belongs to the higher class (6.0 → `STRONG`).

| Class | Magnitude |
|---|---|
| `MICRO` | < 3.0 *(own name: USGS does not name this band)* |
| `MINOR` | 3.0 – < 4.0 |
| `LIGHT` | 4.0 – < 5.0 |
| `MODERATE` | 5.0 – < 6.0 |
| `STRONG` | 6.0 – < 7.0 |
| `MAJOR` | 7.0 – < 8.0 |
| `GREAT` | ≥ 8.0 |

## Structure

```
src/main/java/reivajh06/kata3/
├── Main.java               wiring: source → provider → analyzer → writer
├── sources/
│   ├── SeismSource         interface: List<SeismDto> requestSeisms()
│   └── USGSSource          HttpClient, timeout, status check, Gson → DTOs
├── dtos/                   SeismDto, PropertiesDto, GeometryDto (input) · AnalysisDto (output)
├── SeismParser             validates a DTO and maps it to a domain Seism (skips unusable events, logs why)
├── SeismsProvider          source + parser → List<Seism>, counts skipped events
├── SeismAnalyzer           pure transformation: List<Seism> → AnalysisDto
├── model/                  Seism, Place, Coordinate, SeismicClass, SeismicRange, SeismicType, SeismBuilder
├── writers/
│   ├── SeismWriter         interface
│   └── FileSeismWriter     Gson → UTF-8 file
├── JsonHandler             Gson configuration (Instant as ISO-8601 string)
└── exceptions/             SeismSourceException, SeismSourceConnectionException, SeismSourceInterruptedException
```

Separation of responsibilities:

| Responsibility | Where | Knows about HTTP? | Knows about files? |
|---|---|---|---|
| Communication | `USGSSource` | yes | no |
| Deserialization + validation | `USGSSource` (JSON → DTO), `SeismParser` (DTO → `Seism`) | no | no |
| Transformation | `SeismAnalyzer` | no | no |
| Writing | `FileSeismWriter` | no | yes |

## Error handling

| Situation | Reported as |
|---|---|
| Status outside 200–299 | `SeismSourceException`: *"Server responded with &lt;code&gt; code …"* (checked **before** parsing) |
| Connection refused / request timeout | `SeismSourceConnectionException` |
| Body is not valid JSON, or has no `features` array | `SeismSourceException`: *"Obtained invalid response …"* |
| Request interrupted | `SeismSourceInterruptedException` |
| Single event with missing/invalid fields (e.g. `mag: null`) | Not an error: the event is skipped, a warning is logged and it counts in `skipped` |

## Verification

All checks are automated (`mvn test`, 38 tests). HTTP tests start a local JDK `HttpServer` on `127.0.0.1` with a random
port, so every response is controlled and no internet connection is needed.

| Test | What it verifies |
|---|---|
| `KnownResponseEndToEndTest` | **Known input → expected output**: serves `sample.geojson`, runs the full flow and compares the written file with `expected-analysis.json` (ignoring `generatedAt`) |
| `USGSSourceTest` | valid response, HTTP 500, `200` with a plain-text body, malformed JSON, JSON without `features`, timeout, refused connection |
| `SeismParserTest` | DTO → `Seism` mapping, `[lon, lat, depth]` order, timestamps, non-ASCII places, skipping null `mag`/`place` |
| `SeismAnalyzerTest` | counts, total, strongest event; empty input |
| `SeismicClassTest` | class boundaries (−0.4, 2.99, 3.0, 4.95, 8.0, …) |
| `SeismicTypeTest` | USGS type names → enum, unknown → `OTHER` |
| `FileSeismWriterTest` | UTF-8 output, missing folders created, ISO timestamps, previous content overwritten |

### Repeating the checks manually

Compare a known response with the expected result:

1. Serve the fixture locally, e.g. from `src/test/resources/usgs`, with any static server, at the path
   `earthquakes/feed/v1.0/summary/all_day.geojson`.
2. Point the client at it: `USGSSource.ofPath("http://localhost:<port>")` in `Main`.
3. Run the client and compare `temp/results.json` with `expected-analysis.json` (all fields except `generatedAt`).

Reproduce the error cases:

- **HTTP error (404):** `USGSSource.ofPath("https://earthquake.usgs.gov/doesnotexist/")`.
- **Connection failure:** `USGSSource.ofPath("http://localhost:1")`.
- **Timeout:** `USGSSource.create().setRequestTimeout(Duration.ofMillis(1))`.
- **Invalid response:** serve any non-JSON file from a local server.

## Repetitions

The kata was repeated keeping the same service and the same query → transformation → JSON flow. Each repetition is
marked with a git tag.

| Repetition | Tag | Variation |
|---|---|---|
| 1 | [`rep-1`](../../tree/rep-1) | First version: `all_day` feed, counts per class and type, strongest event |
| 2 | `rep-2` | _TODO_ |
| 3 | `rep-3` | _TODO_ |

Check out a repetition:

```powershell
git checkout rep-1
```

The `master` branch contains the final evaluable version.
