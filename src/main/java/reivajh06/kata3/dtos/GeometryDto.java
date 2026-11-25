package reivajh06.kata3.dtos;

import java.util.Arrays;

public record GeometryDto(
		double[] coordinates
) {
	@Override
	public String toString() {
		return "GeometryDto{" +
				"coordinates=" + Arrays.toString(coordinates) +
				'}';
	}
}
