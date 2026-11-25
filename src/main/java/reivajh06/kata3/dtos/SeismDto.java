package reivajh06.kata3.dtos;

public record SeismDto(
		String id,
		PropertiesDto properties,
		GeometryDto geometry
) {

	@Override
	public String toString() {
		return "SeismDto{" +
				"id='" + id + '\'' +
				", properties=" + properties +
				", geometry=" + geometry +
				'}';
	}
}
