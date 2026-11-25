package reivajh06.kata3;

import org.junit.jupiter.api.Test;
import reivajh06.kata3.dtos.AnalysisDto;
import reivajh06.kata3.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SeismAnalyzerTest {

	private final SeismAnalyzer analyzer = new SeismAnalyzer();

	private static Seism seism(String id, double magnitude, SeismicType type) {
		return new SeismBuilder()
				.id(id)
				.magnitude(magnitude)
				.seismicClass(SeismicClass.fromMagnitude(magnitude))
				.type(type)
				.place(new Place("somewhere", new Coordinate(0, 0, 10)))
				.time(Instant.EPOCH)
				.updated(Instant.EPOCH)
				.build();
	}

	@Test
	void summarisesAKnownSetOfSeisms() {
		List<Seism> seisms = List.of(
				seism("a", 1.0, SeismicType.EARTHQUAKE),
				seism("b", 2.5, SeismicType.QUARRY_BLAST),
				seism("c", 3.2, SeismicType.EARTHQUAKE),
				seism("d", 6.1, SeismicType.EARTHQUAKE)
		);

		AnalysisDto analysis = analyzer.analyze(2, seisms);

		assertEquals(6, analysis.total(), "total counts skipped events too");
		assertEquals(2, analysis.skipped());
		assertEquals(Map.of(SeismicClass.MICRO, 2, SeismicClass.MINOR, 1, SeismicClass.STRONG, 1), analysis.byClass());
		assertEquals(Map.of(SeismicType.EARTHQUAKE, 3, SeismicType.QUARRY_BLAST, 1), analysis.byType());
		assertEquals("d", analysis.strongest().id());
		assertNotNull(analysis.generatedAt());
	}

	@Test
	void emptyInputHasNoStrongestAndNoCounts() {
		AnalysisDto analysis = analyzer.analyze(0, List.of());

		assertEquals(0, analysis.total());
		assertTrue(analysis.byClass().isEmpty());
		assertTrue(analysis.byType().isEmpty());
		assertNull(analysis.strongest());
	}
}
