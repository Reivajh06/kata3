package reivajh06.kata3;

import reivajh06.kata3.dtos.GeometryDto;
import reivajh06.kata3.dtos.PropertiesDto;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class SeismParser {

	public Optional<Seism> parse(SeismDto dto) {
		if(dto.id().isEmpty()) return Optional.empty();

		if(!invalidProperties(dto.properties())) return Optional.empty();
		if(!invalidGeometry(dto.geometry())) return Optional.empty();

		Place place = new Place(
				dto.properties().place(),
				new Coordinate(
						dto.geometry().coordinates().get(0),
						dto.geometry().coordinates().get(1),
						dto.geometry().coordinates().get(2)
						)
		);

		SeismBuilder builder = new SeismBuilder()
				.id(dto.id())
				.magnitude(dto.properties().mag())
				.seismicClass(SeismicClass.fromMagnitude(dto.properties().mag()))
				.type(SeismicType.parse(dto.properties().type()))
				.place(place)
				.time(Instant.ofEpochMilli(dto.properties().time().longValue()))
				.updated(Instant.ofEpochMilli(dto.properties().updated().longValue()));

		return Optional.of(builder.build());
	}

	private boolean invalidGeometry(GeometryDto geometry) {
		if(!isValid(geometry.coordinates().get(0))) return false;
		if(!isValid(geometry.coordinates().get(1))) return false;

		return isValid(geometry.coordinates().get(2));
	}

	private boolean invalidProperties(PropertiesDto properties) {
		if(properties.type().isEmpty()) return false;
		if(properties.title().isEmpty()) return false;

		return true;
	}

	public List<Optional<Seism>> parseAll(List<SeismDto> dtos) {
		return dtos.stream()
				.map(this::parse)
				.toList();
	}

	private boolean isValid(Double value) {
		return value == null || Double.isNaN(value) || Double.isInfinite(value);
	}
}
