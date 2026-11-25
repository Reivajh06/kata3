package reivajh06.kata3.dtos;

import reivajh06.kata3.model.Seism;
import reivajh06.kata3.model.SeismicClass;
import reivajh06.kata3.model.SeismicType;

import java.util.Map;

public record AnalysisDto(
		String generatedAt,
		int total,
		int skipped,
		Map<SeismicClass, Integer> byClass,
		Map<SeismicType, Integer> byType,
		Seism strongest
) {
}
