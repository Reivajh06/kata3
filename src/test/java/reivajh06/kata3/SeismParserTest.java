package reivajh06.kata3;

import org.junit.jupiter.api.Test;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.model.Seism;
import reivajh06.kata3.model.SeismicClass;
import reivajh06.kata3.model.SeismicType;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SeismParserTest {

	private final JsonHandler json = new JsonHandler();
	private final SeismParser parser = new SeismParser();

	private SeismDto dto(String mag, String place, String type) {
		return json.deserialize("""
				{
				  "id": "us1",
				  "properties": { "mag": %s, "place": %s, "time": 1795601925642, "updated": 1795602794040,
				                  "type": "%s", "title": "M 4.3 - test" },
				  "geometry": { "type": "Point", "coordinates": [122.9622, 24.3005, 46.912] }
				}""".formatted(mag, place, type), SeismDto.class);
	}

	@Test
	void mapsAValidFeatureToASeism() {
		Seism seism = parser.parse(dto("4.3", "\"18 km SSW of Yonakuni, Japan\"", "earthquake")).orElseThrow();

		assertEquals("us1", seism.id());
		assertEquals(4.3, seism.magnitude());
		assertEquals(SeismicClass.LIGHT, seism.seismicClass());
		assertEquals(SeismicType.EARTHQUAKE, seism.type());
		assertEquals("18 km SSW of Yonakuni, Japan", seism.place().description());
		assertEquals(Instant.parse("2026-11-25T10:18:45.642Z"), seism.time());
		assertEquals(Instant.parse("2026-11-25T10:33:14.040Z"), seism.updated());
	}

	@Test
	void readsGeoJsonCoordinatesAsLongitudeLatitudeDepth() {
		Seism seism = parser.parse(dto("4.3", "\"x\"", "earthquake")).orElseThrow();

		assertEquals(24.3005, seism.place().coordinates().latitude(), "latitude is coordinates[1]");
		assertEquals(122.9622, seism.place().coordinates().longitude(), "longitude is coordinates[0]");
		assertEquals(46.912, seism.place().coordinates().depth());
	}

	@Test
	void keepsNonAsciiPlaceNames() {
		Seism seism = parser.parse(dto("3.0", "\"5 km S of Guánica, Puerto Rico\"", "earthquake")).orElseThrow();

		assertEquals("5 km S of Guánica, Puerto Rico", seism.place().description());
	}

	@Test
	void skipsEventWithoutMagnitude() {
		assertEquals(Optional.empty(), parser.parse(dto("null", "\"19 km W of Nikiski, Alaska\"", "earthquake")));
	}

	@Test
	void skipsEventWithoutPlace() {
		assertEquals(Optional.empty(), parser.parse(dto("4.3", "null", "earthquake")));
	}

	@Test
	void unknownTypesBecomeOther() {
		Seism seism = parser.parse(dto("1.0", "\"x\"", "ice quake")).orElseThrow();

		assertEquals(SeismicType.OTHER, seism.type());
	}
}
