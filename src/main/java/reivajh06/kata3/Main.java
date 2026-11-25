package reivajh06.kata3;

import reivajh06.kata3.sources.USGSSource;
import reivajh06.kata3.writers.FileSeismWriter;
import reivajh06.kata3.writers.SeismWriter;

public class Main {

	public static void main(String[] args) {
		SeismsProvider provider = new SeismsProvider(USGSSource.create());
		SeismWriter writer = FileSeismWriter.to("temp/results.json");
		writer.write(provider.getSeisms());
	}
}
