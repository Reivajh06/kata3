package reivajh06.kata3.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeismicTypeTest {

	@ParameterizedTest(name = "\"{0}\" -> {1}")
	@CsvSource({
			"earthquake,         EARTHQUAKE",
			"quarry blast,       QUARRY_BLAST",
			"nuclear explosion,  NUCLEAR_EXPLOSION",
			"'  Landslide ',     LANDSLIDE",
			"ice quake,          OTHER",
			"sonic boom,         OTHER"
	})
	void mapsUsgsTypeNames(String usgsType, SeismicType expected) {
		assertEquals(expected, SeismicType.parse(usgsType));
	}
}
