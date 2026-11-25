package reivajh06.kata3;

import reivajh06.kata3.dtos.AnalysisDto;
import reivajh06.kata3.model.Seism;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SeismAnalyzer {

	public AnalysisDto analyze(int skipped, List<Seism> seisms) {
		return new AnalysisDto(
				Instant.now().toString(),
				skipped + seisms.size(),
				skipped,
				seisms.stream().collect(Collectors.toMap(Seism::seismicClass, s -> 1, Integer::sum)),
				seisms.stream().collect(Collectors.toMap(Seism::type, s -> 1, Integer::sum)),
				seisms.stream().max(Comparator.comparing(Seism::magnitude)).orElse(null)
		);
	}
}
