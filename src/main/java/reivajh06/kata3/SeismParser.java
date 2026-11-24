package reivajh06.kata3;

import reivajh06.kata3.dtos.SeismDto;
import reivajh06.kata3.model.Seism;

import java.util.List;

public class SeismParser {

	public Seism parse(SeismDto dto) {

		return null;
	}

	public List<Seism> parseAll(List<SeismDto> dtos) {
		return dtos.stream()
				.map(this::parse)
				.toList();
	}
}
