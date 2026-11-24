package reivajh06.kata3.model;

import java.time.Instant;

public record Seism(
		String id,
		double magnitude,
		SeismicType type,
		SeismicClass seismicClass,
		Place place,
		Instant time,
		Instant updated
) {
}
