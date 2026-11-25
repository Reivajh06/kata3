package reivajh06.kata3.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeismicClassTest {

	// Boundaries follow the USGS magnitude classes; a value equal to a lower bound belongs to the higher class.
	@ParameterizedTest(name = "M {0} -> {1}")
	@CsvSource({
			"-0.4, MICRO",
			"0.0,  MICRO",
			"2.99, MICRO",
			"3.0,  MINOR",
			"3.99, MINOR",
			"4.0,  LIGHT",
			"4.95, LIGHT",
			"5.0,  MODERATE",
			"6.0,  STRONG",
			"7.0,  MAJOR",
			"7.99, MAJOR",
			"8.0,  GREAT",
			"9.5,  GREAT"
	})
	void classifiesMagnitudeByUsgsClasses(double magnitude, SeismicClass expected) {
		assertEquals(expected, SeismicClass.fromMagnitude(magnitude));
	}
}
