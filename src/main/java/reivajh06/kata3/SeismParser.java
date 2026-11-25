package reivajh06.kata3;

import reivajh06.kata3.dtos.GeometryDto;
import reivajh06.kata3.dtos.PropertiesDto;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SeismParser {

	private final Logger logger;

	public SeismParser() {
		this.logger = Logger.getLogger(SeismParser.class.getName());
	}

	public Optional<Seism> parse(SeismDto dto) {
		if(dto.id() == null || dto.id().isEmpty()) {
			logger.severe("MALFORMED DTO ENCOUNTERED: ID IS EITHER EMPTY OR NULL");
			return Optional.empty();
		}

		if(invalidProperties(dto.properties())) {
			logger.warning("DTO OF ID %s HAS MALFORMED OR MISSING PROPERTIES: %s".formatted(dto.id(), dto.properties()));
			return Optional.empty();
		}

		if(invalidGeometry(dto.geometry())) {
			logger.warning("DTO OF ID %s HAS MALFORMED OR MISSING GEOMETRY: %s".formatted(dto.id(), dto.geometry()));
			return Optional.empty();
		}

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
		if(!isValid(geometry.coordinates().get(2))) return false;

		return isValid(geometry.coordinates().get(2));
	}

	private boolean invalidProperties(PropertiesDto properties) {
		if(properties.type() == null || properties.type().isEmpty()) return true;
		if(properties.title() == null || properties.title().isEmpty()) return true;
		if(properties.place() == null || properties.place().isEmpty()) return true;
		if(properties.mag() == null) return true;
		if(properties.time() == null || properties.time().isInfinite() || properties.time().isNaN()) return true;

		return properties.updated() == null || properties.updated().isInfinite() || properties.updated().isNaN();
	}

	public List<Optional<Seism>> parseAll(List<SeismDto> dtos) {
		return dtos.stream()
				.map(this::parse)
				.toList();
	}

	private boolean isValid(Double value) {
		return value != null && !value.isNaN() && !value.isInfinite();
	}
}
