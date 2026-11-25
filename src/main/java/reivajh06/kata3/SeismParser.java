package reivajh06.kata3;

import reivajh06.kata3.dtos.GeometryDto;
import reivajh06.kata3.dtos.PropertiesDto;
import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.model.*;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class SeismParser {

	private final Logger logger;
	Map<String, Object> invalidValues;

	public SeismParser() {
		this.logger = Logger.getLogger(SeismParser.class.getName());
	}

	public Optional<Seism> parse(SeismDto dto) {
		invalidValues = new HashMap<>();

		if(dto.id() == null || dto.id().isEmpty()) {
			logger.severe("MALFORMED DTO ENCOUNTERED: ID IS EITHER EMPTY OR NULL");
			return Optional.empty();
		}

		validateProperties(dto.properties());
		validateGeometry(dto.geometry());

		if(!invalidValues.isEmpty()) {
			logger.warning("DTO OF ID %s HAS MALFORMED OR MISSING VALUES: %s".formatted(dto.id(), invalidValues));
			return Optional.empty();
		}

		Place place = new Place(
				dto.properties().place(),
				new Coordinate(
						dto.geometry().coordinates()[0],
						dto.geometry().coordinates()[1],
						dto.geometry().coordinates()[2]
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

	private void validateGeometry(GeometryDto geometry) {
		if(!isValid(geometry.coordinates()[0])) invalidValues.put("latitude", geometry.coordinates()[0]);
		if(!isValid(geometry.coordinates()[1])) invalidValues.put("longitude", geometry.coordinates()[1]);
		if(!isValid(geometry.coordinates()[2])) invalidValues.put("depth", geometry.coordinates()[2]);
	}

	private void validateProperties(PropertiesDto properties) {
		if(properties.type() == null || properties.type().isEmpty()) invalidValues.put("type", properties.type());
		if(properties.title() == null || properties.title().isEmpty()) invalidValues.put("title", properties.title());
		if(properties.place() == null || properties.place().isEmpty()) invalidValues.put("place", properties.place());

		if(!isValid(properties.mag())) invalidValues.put("magnitude", properties.mag());
		if(!isValid(properties.time())) invalidValues.put("time", properties.time());
		if(!isValid(properties.updated())) invalidValues.put("updated", properties.updated());
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
