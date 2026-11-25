package reivajh06.kata3.dtos;

public record PropertiesDto(
		Double mag,
		String place,
		Double time,
		Double updated,
		String type,
		String title
) {

	@Override
	public String toString() {
		return "PropertiesDto{" +
				"mag=" + mag +
				", place='" + place + '\'' +
				", time=" + time +
				", updated=" + updated +
				", type='" + type + '\'' +
				", title='" + title + '\'' +
				'}';
	}
}
