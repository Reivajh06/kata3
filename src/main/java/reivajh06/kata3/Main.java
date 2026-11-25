package reivajh06.kata3;

import reivajh06.kata3.model.Seism;
import reivajh06.kata3.sources.USGSSource;
import reivajh06.kata3.writers.FileSeismWriter;
import reivajh06.kata3.writers.SeismWriter;

import java.util.List;

public class Main {

	public static void main(String[] args) {
		SeismsProvider provider = new SeismsProvider(USGSSource.create());
		SeismWriter writer = FileSeismWriter.to("temp/results.json");
		SeismAnalyzer analyzer = new SeismAnalyzer();
		List<Seism> seisms = provider.getSeisms();
		writer.write(analyzer.analyze(provider.skipped(), seisms));
	}
}
